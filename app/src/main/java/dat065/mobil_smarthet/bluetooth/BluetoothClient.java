package dat065.mobil_smarthet.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.util.Log;

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
import java.util.UUID;

import dat065.mobil_smarthet.database.SensorDBHandler;

public class BluetoothClient extends Thread implements Runnable{

    private BluetoothSocket socket;
    private BluetoothDevice device;
    private InputStream inStream;
    private OutputStream outStream;
    private SensorDBHandler db;

    public BluetoothClient(BluetoothDevice device, Context context) {
        db = new SensorDBHandler(context,null);
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
        byte[] buffer = new byte[1048576];
        int bytes;

        write(getMessage((byte) 0x01, (byte) 0x01, 22000));
        while (true) {
            try {
                bytes = inStream.read(buffer);
                byte[] data= read(buffer,bytes);

                ArrayList<SerializableSensor> sensorData = (ArrayList<SerializableSensor>) deserialize(data);
                for(SerializableSensor s: sensorData ){
                    Log.i("bt", "Sensor: " + sensorData.get(0).getSensor());
                    Log.i("bt","Inserting : "+sensorData.get(0).getData().size()+" rows");
                    db.addData(s);
                }
                //write(getMessage((byte) 0x01, (byte) 0x01, 20000));
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
    private byte[] read(byte[] data, int bytes){
        byte[] rtnArr = new byte[bytes];
        for(int i = 0;i < bytes;i++){
            rtnArr[i]=data[i];
        }
        Log.i("bt", "Recieved: " + bytes);
        return rtnArr;
    }
    public void write(byte[] bytes) {
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
        try {
            socket.close();
        } catch (IOException e) { }
    }
    private byte[] getMessage(byte type, byte sensor, int ago){
        byte[] time = getCurrentUnixTimeByte(ago);
        return new byte[]{type, sensor, time[0], time[1], time[2], time[3]};
    }
    public byte[] getUnixTimeByte(int time){
        Log.i("bt", "time: "+time);
        ByteBuffer b = ByteBuffer.allocate(Integer.SIZE);
        b.putInt(time);
        return b.array();
    }
    public byte[] getCurrentUnixTimeByte(){
        return getCurrentUnixTimeByte(0);
    }
    public byte[] getCurrentUnixTimeByte(int ago){
        long time = Instant.now().minus(ago*1000).getMillis()/1000;
        Log.i("bt", "time: "+(int)time);
        return getUnixTimeByte((int)time);
    }
    public byte[] serialize(Object obj) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream os = new ObjectOutputStream(out);
        os.writeObject(obj);
        return out.toByteArray();
    }
    public Object deserialize(byte[] data) throws IOException, ClassNotFoundException {
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        ObjectInputStream is = new ObjectInputStream(in);
        return is.readObject();
    }
}
