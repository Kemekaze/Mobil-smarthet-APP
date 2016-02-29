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
import android.support.design.widget.Snackbar;
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
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import dat065.mobil_smarthet.alarm.AlarmActivity;
import dat065.mobil_smarthet.bluetooth.BluetoothClient;
import dat065.mobil_smarthet.constants.Presets;
import dat065.mobil_smarthet.constants.Sensors;
import dat065.mobil_smarthet.constants.Settings;
import dat065.mobil_smarthet.database.SensorDBHandler;
import dat065.mobil_smarthet.database.SettingsDBHandler;
import dat065.mobil_smarthet.event.FavourizeEvent;
import dat065.mobil_smarthet.event.SensorEvent;
import dat065.mobil_smarthet.event.SnackbarEvent;
import dat065.mobil_smarthet.event.SwitchEvent;
import dat065.mobil_smarthet.event.UpdateGUIEvent;
import dat065.mobil_smarthet.sensor.SensorService;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    private DrawerLayout drawerLayout;
    private SectionsAdapter sectionsAdapter;
    private ViewPager viewPager;
    private NavigationView navigationView;

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothClient btc = null;
    private BluetoothDevice btServer = null;
    private String btServerName = "dat065MS";
    private boolean btStatus;
    private SensorService sensorService;

    private SettingsDBHandler dbSettings;
    private SensorDBHandler dbSensor;
    private Menu menu;

    private Sensors[] favoriteSensors = new Sensors[2];
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        dbSensor = new SensorDBHandler(this,null);
        dbSettings = new SettingsDBHandler(this,null);

        sectionsAdapter = new SectionsAdapter(getSupportFragmentManager());
        viewPager = (ViewPager) findViewById(R.id.container);
        viewPager.setAdapter(sectionsAdapter);
        viewPager.addOnPageChangeListener(pageChangeListener);


        btStatus = false;
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);


        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.setDrawerListener(toggle);
        toggle.syncState();
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        //navigationView.setItemIconTintList(null);
        checkBluetooth();
        /*Random rand = new Random();
        HashMap<Long,Double> t = new HashMap<>();
        for(int i = 40;i>0;i--){
            long r = rand.nextLong(100)+1;
            Long l = DateTime.now().minusDays(i).getMillis();
            t.put(l,(double) r);
        }
        dbSensor.addData(new SerializableSensor(t,1));*/



        ;
        //default switch postion
        for(Settings set: Settings.getfavourites() ){
            Sensors s = Sensors.match(dbSettings.get(set).second);
            if(s != null){
                navigationView.getMenu().getItem(1).getSubMenu().getItem(s.getId()-1).setIcon(R.drawable.switch_on);
            }
        }

        startService(new Intent(this, SensorService.class));
        EventBus.getDefault().postSticky(new UpdateGUIEvent(1));
        Presets preset = Presets.match(dbSettings.get(Settings.PRESET).second);
        if(preset == null) EventBus.getDefault().postSticky(new UpdateGUIEvent(2,0));
        else viewPager.setCurrentItem(preset.getId());

      }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
        if (isBluetoothEnabled()) {
            unregisterReceiver(mReceiver);
        }
        if(isBluetoothEnabled()){
            BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
        }
    }

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
    private ViewPager.OnPageChangeListener pageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

        @Override
        public void onPageSelected(int position) {
            EventBus.getDefault().postSticky(new UpdateGUIEvent(2, position));
        }

        @Override
        public void onPageScrollStateChanged(int state) {}
    };
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updatePager(){

    }
    /**
     * Handles event clicks of the alarm button located at the home screen.
     * When called, it starts a new AlarmActivity
     *
     * @param v the alarm button
     */
    public void onAlarmClick(View v) {
        runActivity(new Intent(this, AlarmActivity.class));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateFavSensorValue(SensorEvent ev){
        int id = getResources().getIdentifier("sensor_"+ev.getSetting().getKey()+"_text", "id", getPackageName());
        if(ev.getSensor() == null)
            ((TextView) findViewById(id)).setText(ev.getAltText());
        else
            ((TextView) findViewById(id)).setText(ev.getValue() + " " + ev.getSensor().getSymbol());
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
     public void snackbar(SnackbarEvent ev){
        Log.i("Main.snackbar", ev.getText());
        switch (ev.getDuration()){
            case Snackbar.LENGTH_LONG:
                Snackbar.make(this.getCurrentFocus(), ev.getText(),Snackbar.LENGTH_LONG).show();
                break;
            default:
                Snackbar.make(this.getCurrentFocus(), ev.getText(),Snackbar.LENGTH_SHORT).show();
                break;

        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void setSwitch(SwitchEvent ev){
        Log.i("Main.setSwitch", ev.getSensor().getName() + " " + ev.getState());
        MenuItem item = navigationView.getMenu().getItem(1).getSubMenu().getItem(ev.getSensor().getId() - 1);
        if(!ev.getState()) {
            item.setIcon(R.drawable.switch_on);
        }else {
            item.setIcon(R.drawable.switch_off);
        }
    }
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.

        String[] id_name = getResources().getResourceName(item.getItemId()).split("\\/");
        String[] action = id_name[id_name.length - 1].split("_");
        Intent i = new Intent(this, GraphActivity.class);
        switch (action[0]) {
            case "nav":
                if (action[1].equals("Alarm")) {
                    startActivity(new Intent(this, AlarmActivity.class));
                } else {
                    i.putExtra("sensor", Sensors.match(action[1]).getId());
                    runActivity(i);
                }
                drawerLayout.closeDrawer(GravityCompat.START);
                break;
            case "fav": {
                EventBus.getDefault().post(new FavourizeEvent(Sensors.match(action[1])));
                break;
            }
            default:
                break;
        }
        return true;
    }

    /**
     * Starts a new Activity in a new Thread. This is done to
     * be able to save the choices of favorite sensors if any has
     * been made and display them again when the home screen is
     * displayed again.
     *
     * @param i The desired Intent to start
     */
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

    //-------------------------------------------------------BLUETOOTH-------------------------------------------------------
    /**
     * Checks whether the bluetooth is on or not. If it is,
     * do nothing. Else create a AlertDialog that asks the
     * user if it wants to activate bluetooth.
     */
    public void checkBluetooth() {
        if (!isBluetoothEnabled()) {
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
        } else {
            activateBluetooth();
            //onBtConnection(0);
        }
    }

    /**
     * Listens on the actions of the bluetooth switch at the home screen.
     * Perform different actions depending on the state of the switch(on or off).
     */
    public void onBtConnectionClick(MenuItem item) {
        if (btStatus)
            onBtConnection(2);
        else
            onBtConnection(0);
    }

    private void onBtConnection(int status) {
        switch (status) {
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
                if (bluetoothAdapter.isEnabled()) {
                    bluetoothAdapter.cancelDiscovery();
                    bluetoothAdapter.disable();
                }

                if (btc != null) btc.cancel();
                btStatus = false;
                break;
            default:
                break;
        }
    }
    /**
     * Activates bluetooth.
     */
    public void activateBluetooth() {
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, filter);
        Log.i("bt", "Searching for server");
        //if(!bluetoothAdapter.isEnabled())
        bluetoothAdapter.enable();
        bluetoothAdapter.startDiscovery();
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Log.i("bt", "Bluetooth device found: " + device.getName());
                if (device.getName() != null) {
                    if (device.getName().equals(btServerName)) {
                        Log.i("bt", "Bluetooth server located!");
                        btServer = device;
                        BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                        if (btc == null) {
                            onBtConnection(1);
                            Log.i("bt", "Connecting to bluetooth server");
                            BluetoothClient btc = new BluetoothClient(btServer, getApplicationContext());
                            btc.start();
                        }
                    }
                }
            }
        }
    };

    public boolean isBluetoothEnabled() {

        if (bluetoothAdapter == null) {
            return false;
        }
        return bluetoothAdapter.isEnabled();
    }
}