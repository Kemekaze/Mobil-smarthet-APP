package dat065.mobil_smarthet.alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Griffith on 2016-02-17.
 */
public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String string = intent.getExtras().getString("extra");
        Intent serviceIntent = new  Intent(context, RingtoneService.class);
        serviceIntent.putExtra("extra", string);
        context.startService(serviceIntent);
    }
}
