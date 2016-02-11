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

    private final BluetoothSocket socket;
    private final BluetoothDevice device;
    private final InputStream inStream;
    private final OutputStream outStream;

    public BluetoothClient(BluetoothDevice device) {
        BluetoothSocket tmp = null;
        this.device = device;
        try {
            UUID uuid = UUID.fromString("57e33d1f-c167-4f5a-a94f-5f0c58f49356");
            Log.i("bt", uuid.toString());
            tmp = device.createRfcommSocketToServiceRecord(uuid);
        } catch (IOException e) { }
        this.socket = tmp;

        BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
        InputStream tmpIn = null;
        OutputStream tmpOut = null;
        try {
            socket.connect();
            tmpIn = socket.getInputStream();
            tmpOut = socket.getOutputStream();

        } catch (IOException connectException) {
            try {
                socket.close();
            } catch (IOException closeException) { }
        }
        this.inStream = tmpIn;
        this.outStream = tmpOut;
        this.start();
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
    private String read(byte[] data, int bytes){
        String s = "";
        for(int i=0;i<bytes;i++)
            s+=data[i];
        Log.i("bt", "Recieved: "+s);
        return s;
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
