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
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

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
    private boolean tempBool=false,lightBool=false,accBool=false, soundBool=false, co2Bool=false;
    private DrawerLayout drawerLayout;
    private SectionsAdapter sectionsAdapter;
    private ViewPager viewPager;
    private BluetoothClient btc = null;
    private BluetoothDevice btServer = null;
    private String btServerName = "dat065MS";
    private boolean btStatus;
    private SettingsDBHandler dbSettings;
    private SensorDBHandler dbSensor;
    private Menu menu;

    FavoriteSensors favoriteSensors;


    @Override
    protected void onCreate(Bundle savedInstanceState) {		

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sectionsAdapter = new SectionsAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        viewPager = (ViewPager) findViewById(R.id.container);
        viewPager.setAdapter(sectionsAdapter);
        btStatus = false;
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

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
        Random rand = new Random();
        HashMap<Integer,Double> t = new HashMap<>();
        for(int i = 40;i>0;i--){
            int r = rand.nextInt(100)+1;
            Long l = DateTime.now().minusDays(i).getMillis()/1000;
            t.put(Integer.parseInt(l+""),(double) r);
        }
        dbSensor.addData(new SerializableSensor(t,1));



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
                            onBtConnection(1);
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
        this.menu = menu;
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
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, filter);
        Log.i("bt", "Searching for server");
        if(!bluetoothAdapter.isEnabled())
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
     */

    public void onBtConnectionClick(MenuItem item){
        if(btStatus)
            onBtConnection(2);
        else
            onBtConnection(0);
    }
    private void onBtConnection(int status) {
        switch (status){
            case 0:
                btStatus = true;
                menu.getItem(0).setIcon(R.drawable.ic_bluetooth_searching_white_48dp);
                activateBluetooth();
                break;
            case 1:
                menu.getItem(0).setIcon(R.drawable.ic_bluetooth_connected_white_48dp);
                break;
            case 2:
                menu.getItem(0).setIcon(R.drawable.ic_bluetooth_disabled_white_48dp);
                if(bluetoothAdapter.isEnabled()){
                    bluetoothAdapter.cancelDiscovery();
                    bluetoothAdapter.disable();
                }

                if(btc != null)btc.cancel();
                btStatus = false;
                break;
            default:
                break;
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
                            onBtConnection(0);
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
            runActivity(i);
            drawerLayout.closeDrawer(GravityCompat.START);
        } else if (id == R.id.nav_light) {
            i.putExtra("sensor", Sensors.LIGHT.getId());
            runActivity(i);
            drawerLayout.closeDrawer(GravityCompat.START);
        } else if (id == R.id.nav_accelerometer) {
            i.putExtra("sensor", Sensors.MOTION.getId());
            runActivity(i);
            drawerLayout.closeDrawer(GravityCompat.START);
        } else if (id == R.id.nav_co2) {
            i.putExtra("sensor", Sensors.CO2.getId());
            runActivity(i);
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
            case R.id.co2Fav:
                if(!accBool){
                    if(favoriteSensors.favorizeSensor(Sensors.CO2)){
                        item.setIcon(R.drawable.switch_on);
                        co2Bool=true;
                    }
                }else{
                    item.setIcon(R.drawable.switch_off);
                    co2Bool=false;
                    favoriteSensors.unfavorizeSensor(Sensors.CO2);
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