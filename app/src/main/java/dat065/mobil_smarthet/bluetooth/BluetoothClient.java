package dat065.mobil_smarthet.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.util.Log;
import android.util.Pair;

import org.greenrobot.eventbus.EventBus;
import org.joda.time.Instant;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import dat065.mobil_smarthet.constants.Settings;
import dat065.mobil_smarthet.database.SensorDBHandler;
import dat065.mobil_smarthet.database.SettingsDBHandler;
import dat065.mobil_smarthet.event.UpdateGUIEvent;

public class BluetoothClient extends Thread implements Runnable{

    private BluetoothSocket socket;
    private BluetoothDevice device;
    private InputStream inStream;
    private OutputStream outStream;
    private SensorDBHandler dbSensors;
    private SettingsDBHandler dbSettings;
    private byte[] lastMessage;
    private boolean isRunning = true;
    public BluetoothClient(BluetoothDevice device, Context context) {
        dbSensors = new SensorDBHandler(context,null);
        dbSettings = new SettingsDBHandler(context,null);


        this.device = device;
        try {
            UUID uuid = UUID.fromString("57e33d1f-c167-4f5a-a94f-5f0c58f49356");
            Log.i("bt", uuid.toString());
            this.socket = device.createRfcommSocketToServiceRecord(uuid);
            BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
            socket.connect();
            Log.i("bt", "Socket connected!");
            inStream = socket.getInputStream();
            outStream = socket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void run() {
        Log.i("bt", "running");
        byte[] buffer = new byte[1048576];// ~1 MB
        int bytes;
        String stringTime = dbSettings.get(Settings.LAST_SENSOR_TIME).second;
        long lastDataTime = (!stringTime.equals(""))?Long.parseLong(stringTime): 0;
        try{
            while (isRunning) {
                byte[] message = getMessage((byte) 0xFF, (byte) 0xFF, lastDataTime);
                write(message);

                ArrayList<SerializableSensor> sensorData = null;
                ArrayList<Byte> data = new ArrayList<Byte>();

                while (sensorData == null) {

                    bytes = inStream.read(buffer);
                    data.addAll(Arrays.asList(read(buffer, bytes)));
                    sensorData = (ArrayList<SerializableSensor>) deserialize(data);
                    Log.i("bt","Object incomplete: "+data.size());
                }
                Log.i("bt","Object complete");
                for(int i =0 ;i< sensorData.size(); i++){
                    long time = dbSensors.addData(sensorData.get(i));
                    if(time > lastDataTime) lastDataTime = time;
                }
                dbSettings.add(new Pair(Settings.LAST_SENSOR_TIME, String.valueOf(lastDataTime)));
                EventBus.getDefault().post(new UpdateGUIEvent(1));

                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }catch(NegativeArraySizeException| IOException e){
            Log.i("bt","Server disconnected");
            cancel();
        }
    }
    private Byte[] read(byte[] data, int bytes){
        Byte[] rtnArr = new Byte[bytes];
        for(int i = 0;i < bytes;i++){
            rtnArr[i]=data[i];
        }
        Log.i("bt", "Recieved: " + bytes);
        return rtnArr;
    }
    private void write(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X ", b));
        }
        try {
            Log.i("bt", "Sending: "+sb.toString());
            outStream.write(bytes);
        } catch (IOException e) { }
    }
    public void cancel() {
        isRunning = false;
        try {
            socket.close();
        } catch (IOException e) { }
    }

    private byte[] getMessage(byte type, byte sensor, long dTime){
        byte[] time = getUnixTimeByte(dTime);
        return new byte[]{type, sensor, time[0], time[1], time[2], time[3], time[4], time[5], time[6], time[7]};
    }
    private long getDiff(long time){
        long now = Instant.now().getMillis();
        long diff = now - time;
        return diff;
    }
    public byte[] getUnixTimeByte(long time){
        Log.i("bt", "time: "+time);
        ByteBuffer b = ByteBuffer.allocate(Long.SIZE);
        b.putLong(time);
        return b.array();
    }
    public byte[] getCurrentUnixTimeByte(){
        return getCurrentUnixTimeByte(0);
    }
    public byte[] getCurrentUnixTimeByte(long ago){
        long time = Instant.now().minus(ago).getMillis();
        Log.i("bt", "time: " + time);
        return getUnixTimeByte(time);
    }
    public byte[] serialize(Object obj){
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ObjectOutputStream os = new ObjectOutputStream(out);
            os.writeObject(obj);
            return out.toByteArray();
        }catch (IOException e){
            return null;
        }

    }
    public Object deserialize(byte[] data){
        try {
            ByteArrayInputStream in = new ByteArrayInputStream(data);
            ObjectInputStream is = new ObjectInputStream(in);
            return is.readObject();

        }catch (IOException | ClassNotFoundException  e){
            return null;
        }
    }
    public Object deserialize(List<Byte> data){
        final int n = data.size();
        byte ret[] = new byte[n];
        for (int i = 0; i < n; i++) {
            ret[i] = data.get(i);
        }
        return deserialize(ret);
    }
}
