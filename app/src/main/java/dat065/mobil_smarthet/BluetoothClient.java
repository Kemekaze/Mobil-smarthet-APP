package dat065.mobil_smarthet;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

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
        while (true) {
            try {
                bytes = inStream.read(buffer);
                read(buffer,bytes);
                write("hello from ze otter site".getBytes());
            } catch (IOException e) {
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
}
