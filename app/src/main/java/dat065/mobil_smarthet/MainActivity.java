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
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private BluetoothAdapter bluetoothAdapter;
    private SwitchCompat bluetoothToggle;
    private TextView bluetoothText;
    private boolean tempBool=false,lightBool=false,accBool=false, soundBool=false;
    private int nrFavoriteSensors = 0;
    private DrawerLayout drawerLayout;

    private BluetoothClient btc = null;
    private BluetoothDevice btServer = null;
    private String btServerName = "dat065MS";

    private LinearLayout favoriteOne;
    private TextView favoriteOneText;

    private LinearLayout favoriteTwo;
    private TextView favoriteTwoText;

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

        favoriteOne = (LinearLayout) findViewById(R.id.sensorLayoutOne);
        favoriteOneText = (TextView) findViewById(R.id.sensorTextOne);
        favoriteTwo = (LinearLayout) findViewById(R.id.sensorLayoutTwo);
        favoriteTwoText = (TextView) findViewById(R.id.sensorTextTwo);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        //navigationView.setItemIconTintList(null);
        checkBluetooth();

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
                if(device.getName().equals(btServerName)){
                    Log.i("bt","Bluetooth server located!");
                    btServer = device;
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    if(btc == null) {
                        Log.i("bt","Connecting to bluetooth server");
                        BluetoothClient btc = new BluetoothClient(btServer);
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
        bluetoothAdapter.startDiscovery();
    }
    /**
     * Listens to button clicks occurring at the home screen.
     *
     * @param v the button that has been clicked
     */
    public void onHomeScreenButtonClick(View v) {
        int id = v.getId();
        if(id == R.id.button1) {
        } else if(id == R.id.button2) {
        } else if(id == R.id.button3) {
        } else if(id == R.id.alarm_button) {
            startActivity(new Intent(this, IconActivity.class).putExtra("icon", "Alarm"));
        }
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
        if (id == R.id.nav_temperature) {
            startActivity(new Intent(this, IconActivity.class).putExtra("icon", "Temperature"));
            drawerLayout.closeDrawer(GravityCompat.START);
        } else if (id == R.id.nav_sound) {
            startActivity(new Intent(this, IconActivity.class).putExtra("icon", "Sound"));
            drawerLayout.closeDrawer(GravityCompat.START);
        } else if (id == R.id.nav_light) {
            startActivity(new Intent(this, IconActivity.class).putExtra("icon", "Light"));
            drawerLayout.closeDrawer(GravityCompat.START);
        } else if (id == R.id.nav_accelerometer) {
            startActivity(new Intent(this, IconActivity.class).putExtra("icon", "Accelerometer"));
            drawerLayout.closeDrawer(GravityCompat.START);
        } else if (id == R.id.nav_alarm) {
            startActivity(new Intent(this, IconActivity.class).putExtra("icon", "Alarm"));
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        //This code makes me scared. Will remake later on when other things are done.
        //Maybe remake them into typeless object and insert sensor object when correct one is chosen.
        nrFavoriteSensors = nrFavoriteSensors>2 ? 2 : nrFavoriteSensors;
        nrFavoriteSensors = nrFavoriteSensors<0 ? 0 : nrFavoriteSensors;
        switch (id){
            case R.id.tempFav:
                if(!tempBool){
                    if(nrFavoriteSensors==2){Toast.makeText(getApplication().getApplicationContext(),"You can't have more than 2 favorized sensors!", Toast.LENGTH_SHORT).show(); break;}
                    item.setIcon(R.drawable.switch_on);
                    tempBool=true;
                    if(favoriteOneText.getText().toString().contains("Unset")){favoriteOneText.setText("DATA"+" Celsius");} else if(favoriteTwoText.getText().toString().contains("Unset")){favoriteTwoText.setText("DATA"+" Celsius");}
                    nrFavoriteSensors++;
                }else{
                    item.setIcon(R.drawable.switch_off);
                    tempBool=false;
                    nrFavoriteSensors--;
                    if(favoriteOneText.getText().toString().contains("Celsius")){favoriteOneText.setText("Unset");} else if(favoriteTwoText.getText().toString().contains("Celsius")){favoriteTwoText.setText("Unset");}
                }
                break;
            case R.id.soundFav:
                if(!soundBool){
                    if(nrFavoriteSensors==2){Toast.makeText(getApplication().getApplicationContext(),"You can't have more than 2 favorized sensors!", Toast.LENGTH_SHORT).show(); break;}
                    item.setIcon(R.drawable.switch_on);
                    soundBool=true;
                    if(favoriteOneText.getText().toString().contains("Unset")){favoriteOneText.setText("DATA"+" dB");} else if(favoriteTwoText.getText().toString().contains("Unset")){favoriteTwoText.setText("DATA"+" dB");}
                    nrFavoriteSensors++;
                }else{
                    item.setIcon(R.drawable.switch_off);
                    soundBool=false;
                    nrFavoriteSensors--;
                    if(favoriteOneText.getText().toString().contains("dB")){favoriteOneText.setText("Unset");} else if(favoriteTwoText.getText().toString().contains("dB")){favoriteTwoText.setText("Unset");}
                }
                break;
            case R.id.lightFav:
                if(!lightBool){
                    if(nrFavoriteSensors==2){Toast.makeText(getApplication().getApplicationContext(),"You can't have more than 2 favorized sensors!", Toast.LENGTH_SHORT).show(); break;}
                    item.setIcon(R.drawable.switch_on);
                    lightBool=true;
                    if(favoriteOneText.getText().toString().contains("Unset")){favoriteOneText.setText("DATA"+" lux");} else if(favoriteTwoText.getText().toString().contains("Unset")){favoriteTwoText.setText("DATA"+" lux");}
                    nrFavoriteSensors++;
                }else{
                    item.setIcon(R.drawable.switch_off);
                    lightBool=false;
                    nrFavoriteSensors--;
                    if(favoriteOneText.getText().toString().contains("lux")){favoriteOneText.setText("Unset");} else if(favoriteTwoText.getText().toString().contains("lux")){favoriteTwoText.setText("Unset");}
                }
                break;
            case R.id.accFav:
                if(!accBool){
                    if(nrFavoriteSensors==2){Toast.makeText(getApplication().getApplicationContext(),"You can't have more than 2 favorized sensors!", Toast.LENGTH_SHORT).show(); break;}
                    item.setIcon(R.drawable.switch_on);
                    accBool=true;
                    if(favoriteOneText.getText().toString().contains("Unset")){favoriteOneText.setText("DATA"+" speed");} else if(favoriteTwoText.getText().toString().contains("Unset")){favoriteTwoText.setText("DATA"+" speed");}
                    nrFavoriteSensors++;
                }else{
                    item.setIcon(R.drawable.switch_off);
                    accBool=false;
                    nrFavoriteSensors--;
                    if(favoriteOneText.getText().toString().contains("speed")){favoriteOneText.setText("Unset");} else if(favoriteTwoText.getText().toString().contains("speed")){favoriteTwoText.setText("Unset");}
                }
                break;
        }
        Log.d("favoriteSensor: ",""+nrFavoriteSensors);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        //drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}