package dat065.mobil_smarthet;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.UUID;

import dat065.mobil_smarthet.Sensor.SerializableSensor;

public class BluetoothClient extends Thread implements Runnable{

    private BluetoothSocket socket;
    private BluetoothDevice device;
    private InputStream inStream;
    private OutputStream outStream;

    public BluetoothClient(BluetoothDevice device) {
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
            this.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void run() {
        Log.i("bt", "running");
        byte[] buffer = new byte[1024];
        int bytes;
        write(new byte[]{'1','b','c','d','d'});
        while (true) {
            try {
                bytes = inStream.read(buffer);

                ArrayList<SerializableSensor> s = (ArrayList<SerializableSensor>) deserialize(buffer);
                read(buffer,bytes);
                Log.i("bt","Sensor: "+s.get(0).getSensor());
                write("hello from ze otter site".getBytes());
            } catch (IOException | ClassNotFoundException e) {
                break;
            }
        }
    }
    private byte[] read(byte[] data, int bytes){
        byte[] rtnArr = new byte[bytes];
        for(int i = 0;i < bytes;i++){
            rtnArr[i]=data[i];
        }
        Log.i("bt", "Recieved: "+new String(rtnArr));
        return rtnArr;
    }
    public void write(byte[] bytes) {
        try {
            Log.i("bt", "Sending: "+new String(bytes));
            outStream.write(bytes);
        } catch (IOException e) { }
    }
    public void cancel() {
        try {
            socket.close();
        } catch (IOException e) { }
    }

    public static byte[] serialize(Object obj) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream os = new ObjectOutputStream(out);
        os.writeObject(obj);
        return out.toByteArray();
    }
    public static Object deserialize(byte[] data) throws IOException, ClassNotFoundException {
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        ObjectInputStream is = new ObjectInputStream(in);
        return is.readObject();
    }
}
