package dat065.mobil_smarthet.alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * This class handles the reception of messages("on" or "off")
 * from AlarmActivity and forwards the message to RingtoneService.
 *
 * @author Kevin H Griffith
 * @version 2016-02-25
 */
public class AlarmReceiver extends BroadcastReceiver {

    /**
     * Receives and collects the extended data from AlarmActivity,
     * starts a service with the extended data.
     *
     * @param context   The Context in which thr receiver is running.
     * @param intent    The Intent being received.
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        String string = intent.getExtras().getString("extra");
        Intent serviceIntent = new  Intent(context, RingtoneService.class);
        serviceIntent.putExtra("extra", string);
        context.startService(serviceIntent);
    }
}
