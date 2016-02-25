package dat065.mobil_smarthet.alarm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import dat065.mobil_smarthet.R;

/**
 * Handles the events of turning on or off the alarm.
 * It includes a media player that plays a tune/song when
 * the alarm clock goes off.
 *
 * @author Kevin H Griffith
 * @version 2016-02-25
 */
public class RingtoneService extends Service {

    MediaPlayer mediaPlayer;
    boolean isRunning;
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * Called by the AlarmReceiver by calling startService(Intent).
     * It checks whether the extended data prompts to turn on or off
     * the alarm clock.
     *
     * @param intent    The Intent supplied to startService(Intent).
     * @param flags     Additional data about this start request.
     * @param startId   A unique integer representing this specific
     *                  request to start.
     * @return          Returns a int value that indicates what
     *                  semantics the system should use for the
     *                  service's current started state.
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        String state = intent.getExtras().getString("extra");

        assert state != null;
        switch (state) {
            case "on":
                startId = 1;
                break;
            case "off":
                startId = 0;
                break;
            default:
                startId = 0;
                break;
        }
        //If there is no music and the user press "alarm on", start music
        if(!isRunning && startId == 1) {
            mediaPlayer = MediaPlayer.create(this, R.raw.hotel);
            mediaPlayer.start();
            isRunning = true;
            setupNotification();
        }
        //If there is music playing and the user press "alarm off", stop music
        else if(isRunning && startId == 0) {
            mediaPlayer.stop();
            mediaPlayer.reset();
            isRunning = false;
        }
        return START_NOT_STICKY;
    }

    /**
     * Sets up the notification that is being displayed whenever
     * the alarm goes off.
     */
    public void setupNotification() {
        Intent intentMain = new Intent(this, AlarmActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intentMain, 0);
        Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle("Time to wake up!")
                .setContentText("Click me to turn off!")
                .setSmallIcon(R.drawable.ic_access_alarm_black_48dp)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build();
        NotificationManager notificationManager = (NotificationManager)
                getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(0, notification);
    }
}