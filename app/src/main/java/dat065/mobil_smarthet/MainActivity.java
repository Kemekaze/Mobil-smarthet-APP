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

import java.util.HashMap;
import java.util.Random;

import dat065.mobil_smarthet.alarm.AlarmActivity;
import dat065.mobil_smarthet.bluetooth.BluetoothClient;
import dat065.mobil_smarthet.bluetooth.SerializableSensor;
import dat065.mobil_smarthet.constants.Sensors;
import dat065.mobil_smarthet.database.SensorDBHandler;
import dat065.mobil_smarthet.database.SettingsDBHandler;
import dat065.mobil_smarthet.sensor.FavoriteSensors;


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
    private SettingsDBHandler dbSettings;
    private SensorDBHandler dbSensor;


    FavoriteSensors favoriteSensors;


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
        dbSettings = new SettingsDBHandler(this,null);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        //navigationView.setItemIconTintList(null);
        checkBluetooth();
        favoriteSensors = new FavoriteSensors(this,dbSettings);
        dbSensor = new SensorDBHandler(this,null);
        //Random rand = new Random();
        //HashMap<Integer,Double> t = new HashMap<>();
        //for(int i = 40;i>0;i--){
        //    Long l = new DateTime(DateTime.now()).minusDays(i).getMillis()/1000;
         //   t.put(Integer.parseInt(l+""),(double) i);
        //}
        //dbSensor.addData(new SerializableSensor(t,1));



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
                            BluetoothClient btc = new BluetoothClient(btServer,getApplicationContext());
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
        Log.i("bt", "Searching for server");
        bluetoothAdapter.enable();
        bluetoothAdapter.startDiscovery();
    }
    /**
     * Listens to the alarm button located at the home screen.
     * When called, it starts a new AlarmActivity
     *
     * @param v the alarm button
     */
    public void onAlarmClick(View v) {
        runActivity(new Intent(this, AlarmActivity.class));
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
        if (id == R.id.nav_temperature) {
            i.putExtra("sensor", Sensors.TEMPERATURE.getId());
            runActivity(i);
            drawerLayout.closeDrawer(GravityCompat.START);
        } else if (id == R.id.nav_sound) {
            i.putExtra("sensor", Sensors.AUDIO.getId());
            startActivity(i);
            drawerLayout.closeDrawer(GravityCompat.START);
        } else if (id == R.id.nav_light) {
            i.putExtra("sensor", Sensors.LIGHT.getId());
            startActivity(i);
            drawerLayout.closeDrawer(GravityCompat.START);
        } else if (id == R.id.nav_accelerometer) {
            i.putExtra("sensor", Sensors.MOTION.getId());
            startActivity(i);
            drawerLayout.closeDrawer(GravityCompat.START);
        } else if (id == R.id.nav_alarm) {
            startActivity(new Intent(this, AlarmActivity.class));
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        switch (id){
            case R.id.tempFav:
                if(!tempBool){
                    if(favoriteSensors.favorizeSensor(Sensors.TEMPERATURE)){
                        item.setIcon(R.drawable.switch_on);
                        tempBool=true;
                    }
                }else{
                    item.setIcon(R.drawable.switch_off);
                    tempBool=false;
                    favoriteSensors.unfavorizeSensor(Sensors.TEMPERATURE);
                }
                break;
            case R.id.soundFav:
                if(!soundBool){
                    if(favoriteSensors.favorizeSensor(Sensors.AUDIO)){
                        item.setIcon(R.drawable.switch_on);
                        soundBool=true;
                    }
                }else{
                    item.setIcon(R.drawable.switch_off);
                    soundBool=false;
                    favoriteSensors.unfavorizeSensor(Sensors.AUDIO);
                }
                break;
            case R.id.lightFav:
                if(!lightBool){
                    if(favoriteSensors.favorizeSensor(Sensors.LIGHT)){
                        item.setIcon(R.drawable.switch_on);
                        lightBool=true;
                    }
                }else{
                    item.setIcon(R.drawable.switch_off);
                    lightBool=false;
                    favoriteSensors.unfavorizeSensor(Sensors.LIGHT);
                }
                break;
            case R.id.accFav:
                if(!accBool){
                    if(favoriteSensors.favorizeSensor(Sensors.MOTION)){
                        item.setIcon(R.drawable.switch_on);
                        accBool=true;
                    }
                }else{
                    item.setIcon(R.drawable.switch_off);
                    accBool=false;
                    favoriteSensors.unfavorizeSensor(Sensors.MOTION);
                }
                break;
        }
        return true;
    }

    private void runActivity(final Intent i) {
        i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
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