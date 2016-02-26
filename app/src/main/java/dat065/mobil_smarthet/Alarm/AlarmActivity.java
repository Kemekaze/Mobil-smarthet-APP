package dat065.mobil_smarthet.alarm;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import java.util.Calendar;
import dat065.mobil_smarthet.R;

/**
 * This class is used to create the alarm activity. It is communicating
 * with the AlarmReceiver to send out messages about whether or not the alarm
 * is turned on. The TimePicker is created which represents the visual part
 * of the alarm clock.
 *
 * @author Kevin H Griffith
 * @version 2016-02-25
 */
public class AlarmActivity extends AppCompatActivity {

    //to make alarm manager
    private AlarmManager alarmManager;
    private TimePicker timePicker;
    private TextView updateText;
    private Button alarmOn, alarmOff;
    private PendingIntent pendingIntent;
    private Intent myIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        updateText = (TextView) findViewById(R.id.update_text);
        timePicker = (TimePicker) findViewById(R.id.timePicker);
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmOn = (Button) findViewById(R.id.alarm_on);
        alarmOff = (Button) findViewById(R.id.alarm_off);
        myIntent = new Intent(this, AlarmReceiver.class);
        timePicker.setIs24HourView(true);
    }

    /**
     * Handles button click events, i.e one button to set the alarm and
     * another one to turn it off.
     *
     * @param view  View of button that has been selected.
     */
    @TargetApi(Build.VERSION_CODES.M)
    public void onClickListener(View view) {
        if(view.getId() == R.id.alarm_on) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, timePicker.getCurrentHour());
            calendar.set(Calendar.MINUTE, timePicker.getCurrentMinute());
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            String hour = String.valueOf(timePicker.getCurrentHour());
            int min = timePicker.getCurrentMinute();
            String minute = String.valueOf(min);
            if(min < 10) {
                minute = "0" + minute;
            }
            myIntent.putExtra("extra", "on");
            pendingIntent = PendingIntent.getBroadcast(this, 0, myIntent,0);
            alarmManager.set(AlarmManager.RTC, calendar.getTimeInMillis(), pendingIntent);
            updateText.setText("Alarm set to: " + hour + ":" + minute);
        } else {
            alarmManager.cancel(pendingIntent);
            myIntent.putExtra("extra", "off");
            sendBroadcast(myIntent);
            updateText.setText("Alarm OFF");
        }
    }

    /**
     * Handles click events. This method is called whenever the user selects an item
     * from the options menu, (includes action items in the app bar).
     *
     * @param item  MenuItem that has been selected.
     * @return      true or false whether or not the menu item has been handled successfully.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}