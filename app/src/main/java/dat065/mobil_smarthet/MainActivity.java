package dat065.mobil_smarthet;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.joda.time.DateTime;

import dat065.mobil_smarthet.bluetooth.BluetoothClient;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private BluetoothAdapter bluetoothAdapter;
    private SwitchCompat bluetoothToggle;
    private TextView bluetoothText;
    private boolean tempBool=false,lightBool=false,accBool=false, soundBool=false;
    private DrawerLayout drawerLayout;

    private BluetoothClient btc = null;
    private BluetoothDevice btServer = null;
    private String btServerName = "dat065MS";

    FavoriteSensors favoriteSensors;

    Sensor temperatureSensor;
    Sensor lightSensor;
    Sensor soundSensor;
    Sensor accelerometerSensor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {		

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        bluetoothToggle = (SwitchCompat) findViewById(R.id.bluetooth_switch);
        bluetoothText = (TextView) findViewById(R.id.bluetooth_text);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        temperatureSensor = new Sensor(SensorTypes.TEMPERATURE);
        lightSensor = new Sensor(SensorTypes.LIGHT);
        soundSensor = new Sensor(SensorTypes.SOUND);
        accelerometerSensor = new Sensor(SensorTypes.ACCELEROMETER);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        //navigationView.setItemIconTintList(null);
        checkBluetooth();
        favoriteSensors = new FavoriteSensors(this);

        //Test data
        temperatureSensor.addSensorData(new DateTime(2016,2,2,6,0),5);
        temperatureSensor.addSensorData(new DateTime(2016,2,3,7,0),7);
        temperatureSensor.addSensorData(new DateTime(2016,2,4,8,0),14);
        temperatureSensor.addSensorData(new DateTime(2016,2,5,9,0),4);
        temperatureSensor.addSensorData(new DateTime(2016,2,6,10,0),1);
        temperatureSensor.addSensorData(new DateTime(2016,2,7,11,0),0);
        temperatureSensor.addSensorData(new DateTime(2016,2,8,12,0),22);
        temperatureSensor.addSensorData(new DateTime(2016,2,9,13,0),14);
        temperatureSensor.addSensorData(new DateTime(2016,2,10,14,0),13);
        temperatureSensor.addSensorData(new DateTime(2016,2,11,15,0),7);
        temperatureSensor.addSensorData(new DateTime(2016,2,12,16,0),5);
        temperatureSensor.addSensorData(new DateTime(2016,2,13,17,0),3);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(isBluetoothEnabled()) {
            unregisterReceiver(mReceiver);
        }
        BluetoothAdapter.getDefaultAdapter().cancelDiscovery();

    }

	
	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Log.i("bt","Bluetooth device found: "+device.getName());
                if(device.getName() != null) {
                    if (device.getName().equals(btServerName)) {
                        Log.i("bt", "Bluetooth server located!");
                        btServer = device;
                        BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                        if (btc == null) {
                            Log.i("bt", "Connecting to bluetooth server");
                            BluetoothClient btc = new BluetoothClient(btServer);
                            btc.start();
                        }
                    }
                }
            }
        }
    };
	
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        return super.onOptionsItemSelected(item);
    }

    public boolean isBluetoothEnabled(){

        if(bluetoothAdapter==null){
            return false;
        }
        return bluetoothAdapter.isEnabled();
    }

    public void activateBluetooth() {
        bluetoothText.setText("Connected");
        bluetoothToggle.setChecked(true);
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, filter);
        Log.i("bt","Searching for server");
        bluetoothAdapter.startDiscovery();
    }
    /**
     * Listens to button clicks occurring at the home screen.
     *
     * @param v the button that has been clicked
     */
    public void onAlarmClick(View v) {
        Toast.makeText(this, "Alarm activity to be", Toast.LENGTH_SHORT).show();
    }

    /**
     * Listens on the actions of the bluetooth switch at the home screen.
     * Perform different actions depending on if the state of the switch(on or off).
     *
     * @param v the View object of the switch
     */
    public void onToggleClick(View v) {
        SwitchCompat toggle = (SwitchCompat)v;
        if(toggle.isChecked()) {
            //Following line crashes in emulator
            activateBluetooth();
            Toast.makeText(this, "Bluetooth 'ON'", Toast.LENGTH_SHORT).show();
        } else {
            //Following line crashes in emulator
            bluetoothAdapter.disable();
            Toast.makeText(this, "Bluetooth 'OFF'", Toast.LENGTH_SHORT).show();
            bluetoothText.setText("Not Connected");
        }
    }
    public void checkBluetooth() {
        //Check if bluetooth is on, if not alert and ask if user want to start
        if(!isBluetoothEnabled()){
            final AlertDialog bluetoothDialog;
            final AlertDialog.Builder bluetoothDialogBuilder = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
            bluetoothDialogBuilder.setTitle("Bluetooth error message")
                    .setMessage("You have to activate bluetooth to synchronize data, would you like to do it now?")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            //this crashes on computer, no bluetooth on emulator phone
                            activateBluetooth();
                        }
                    }).setIcon(R.drawable.ic_error_outline_black_48dp)
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            bluetoothDialog = bluetoothDialogBuilder.create();
            bluetoothDialog.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;
            bluetoothDialog.show();
            bluetoothText.setText("Not Connected");
        } else{
            activateBluetooth();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
         Intent i = new Intent(this, GraphActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        if (id == R.id.nav_temperature) {
            i.putExtra("sensor", temperatureSensor);
            runActivity(i);
            drawerLayout.closeDrawer(GravityCompat.START);
        } else if (id == R.id.nav_sound) {
            i.putExtra("sensor", soundSensor);
            startActivity(i);
            drawerLayout.closeDrawer(GravityCompat.START);
        } else if (id == R.id.nav_light) {
            i.putExtra("sensor", lightSensor);
            startActivity(i);
            drawerLayout.closeDrawer(GravityCompat.START);
        } else if (id == R.id.nav_accelerometer) {
            i.putExtra("sensor", accelerometerSensor);
            startActivity(i);
            drawerLayout.closeDrawer(GravityCompat.START);
        } else if (id == R.id.nav_alarm) {
            Toast.makeText(this, "Alarm activity to be", Toast.LENGTH_SHORT).show();
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        switch (id){
            case R.id.tempFav:
                if(!tempBool){
                    if(favoriteSensors.favorizeSensor(temperatureSensor)){
                        item.setIcon(R.drawable.switch_on);
                        tempBool=true;
                    }
                }else{
                    item.setIcon(R.drawable.switch_off);
                    tempBool=false;
                    favoriteSensors.unfavorizeSensor(temperatureSensor);
                }
                break;
            case R.id.soundFav:
                if(!soundBool){
                    if(favoriteSensors.favorizeSensor(soundSensor)){
                        item.setIcon(R.drawable.switch_on);
                        soundBool=true;
                    }
                }else{
                    item.setIcon(R.drawable.switch_off);
                    soundBool=false;
                    favoriteSensors.unfavorizeSensor(soundSensor);
                }
                break;
            case R.id.lightFav:
                if(!lightBool){
                    if(favoriteSensors.favorizeSensor(lightSensor)){
                        item.setIcon(R.drawable.switch_on);
                        lightBool=true;
                    }
                }else{
                    item.setIcon(R.drawable.switch_off);
                    lightBool=false;
                    favoriteSensors.unfavorizeSensor(lightSensor);
                }
                break;
            case R.id.accFav:
                if(!accBool){
                    if(favoriteSensors.favorizeSensor(accelerometerSensor)){
                        item.setIcon(R.drawable.switch_on);
                        accBool=true;
                    }
                }else{
                    item.setIcon(R.drawable.switch_off);
                    accBool=false;
                    favoriteSensors.unfavorizeSensor(accelerometerSensor);
                }
                break;
        }
        return true;
    }

    private void runActivity(final Intent i) {
        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {

                //do long stuff (like getting info for intent)
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(i);
                        //make new intent
                        //start new activity with intent you just made
                    }
                });
            }
        };
        new Thread(runnable).start();
    }
}