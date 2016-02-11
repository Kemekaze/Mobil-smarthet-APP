package dat065.mobil_smarthet;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
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
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private BluetoothAdapter bluetoothAdapter;
    private SwitchCompat toggle;
    private TextView bluetoothText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //New implementation
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        toggle = (SwitchCompat) findViewById(R.id.bluetooth_switch);
        bluetoothText = (TextView) findViewById(R.id.bluetooth_text);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        checkBluetooth();
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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_temperature) {
            setTitle("Temperature");
        } else if (id == R.id.nav_sound) {
            setTitle("Sound");

        } else if (id == R.id.nav_light) {
            setTitle("Light");

        } else if (id == R.id.nav_accelerometer) {
            setTitle("Accelerometer");

        } else if (id == R.id.nav_alarm) {
            setTitle("Alarm");

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public boolean isBluetoothEnabled(){

        if(bluetoothAdapter==null){
            return false;
        }
        return bluetoothAdapter.isEnabled();
    }

    /**
     * Sets the actionbar title to the specific title
     *
     * @param title     the title to set
     */
    public void setTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

    public void checkBluetooth() {
        //Check if bluetooth is on, if not alert and ask if user want to start
        if(!isBluetoothEnabled()){
            final AlertDialog bluetoothDialog;
            final AlertDialog.Builder bluetoothDialogBuilder = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
            bluetoothDialogBuilder.setTitle("Bluetooth error message")
                    .setMessage("You have to activate bluetooth to synchronize data, would yo like to do it now?")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        //this crashes on computer, no bluetooth on emulator phone
                        bluetoothAdapter.enable();
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
            bluetoothText.setText("Connected");
            toggle.setChecked(true);
        }
    }

    /**
     * Listens to button clicks occurring at the home screen.
     *
     * @param v the button that has been clicked
     */
    public void onHomeScreenButtonClick(View v) {
        int id = v.getId();
        String bName = ((Button)v).getText().toString();
        Toast.makeText(this, bName, Toast.LENGTH_SHORT).show();
        /*if(id == R.id.button1) {
        } else if(id == R.id.button2) {
        } else if(id == R.id.button3) {
        } else if(id == R.id.button4) {
        }*/
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
            //bluetoothAdapter.enable();
            Toast.makeText(this, "Bluetooth 'ON'", Toast.LENGTH_SHORT).show();
            bluetoothText.setText("Connected");
        } else {
            //Following line crashes in emulator
            //bluetoothAdapter.disable();
            Toast.makeText(this, "Bluetooth 'OFF'", Toast.LENGTH_SHORT).show();
            bluetoothText.setText("Not Connected");
        }
    }
}