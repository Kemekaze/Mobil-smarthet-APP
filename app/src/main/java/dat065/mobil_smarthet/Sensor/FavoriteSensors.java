package dat065.mobil_smarthet.sensor;

import android.content.Intent;
import android.os.Handler;
import android.util.Pair;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import dat065.mobil_smarthet.GraphActivity;
import dat065.mobil_smarthet.MainActivity;
import dat065.mobil_smarthet.R;
import dat065.mobil_smarthet.constants.Sensors;
import dat065.mobil_smarthet.constants.Settings;
import dat065.mobil_smarthet.database.SensorDBHandler;
import dat065.mobil_smarthet.database.SettingsDBHandler;
import dat065.mobil_smarthet.helpers.Helpers;

/**
 * Created by backevik on 16-02-11.
 */
public class FavoriteSensors{
    private MainActivity activity;

    private TextView favoriteOneText;
    private TextView favoriteTwoText;

    private Sensors favoriteOne;
    private Sensors favoriteTwo;

    private Handler handler;
    private SettingsDBHandler dbSettings;
    private SensorDBHandler dbSensors;

    public FavoriteSensors(final MainActivity activity, SettingsDBHandler dbSettings){
        this.activity = activity;
        this.dbSettings = dbSettings;
        this.dbSensors = new SensorDBHandler(activity.getApplicationContext(),null);
        favoriteOneText = (TextView) this.activity.findViewById(R.id.sensorTextOne);
        favoriteTwoText = (TextView) this.activity.findViewById(R.id.sensorTextTwo);
        favoriteOne = Sensors.match(dbSettings.get(Settings.FAV_SENSOR_1).second);
        favoriteTwo = Sensors.match(dbSettings.get(Settings.FAV_SENSOR_2).second);
        handler = new Handler();


        favoriteOneText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(favoriteOne==null){
                    Toast.makeText(activity.getApplicationContext(),"No sensor favored!",Toast.LENGTH_SHORT);
                }else{
                    createLineChart(favoriteOne);
                }

            }
        });

        favoriteTwoText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(favoriteTwo==null){
                    Toast.makeText(activity.getApplicationContext(),"No sensor favored!",Toast.LENGTH_SHORT);
                }else{
                    createLineChart(favoriteTwo);
                }
            }
        });
    }

    public boolean favorizeSensor(Sensors sensor){
        if(favoriteOne==null){
            favoriteOne = sensor;
            favoriteOneText.setText(Helpers.round(dbSensors.getMostRelevantData(sensor), 1) + " " + sensor.getSymbol());

            dbSettings.add(new Pair<Settings, String>(Settings.FAV_SENSOR_1, sensor.getName()));
            return true;
        }else if(favoriteTwo==null){
            favoriteTwo = sensor;
            favoriteTwoText.setText(Helpers.round(dbSensors.getMostRelevantData(sensor),1) + " " + sensor.getSymbol());

            dbSettings.add(new Pair<Settings, String>(Settings.FAV_SENSOR_2,sensor.getName()));
            return true;
        }else{
            Toast.makeText(activity.getApplicationContext(),"You can't have more than 2 favorized sensors!", Toast.LENGTH_SHORT).show();
            return false;
        }
    }
    public void unfavorizeSensor(Sensors sensor){
        if(favoriteOne.equals(sensor)){
            favoriteOne = null;
            dbSettings.remove(Settings.FAV_SENSOR_1);
            favoriteOneText.setText("Unset");
        }else if(favoriteTwo.equals(sensor)){
            favoriteTwo = null;
            dbSettings.remove(Settings.FAV_SENSOR_2);
            favoriteTwoText.setText("Unset");
        }
    }

    public void createLineChart(Sensors sensor){
        final Intent i = new Intent(activity.getApplicationContext(), GraphActivity.class);
        i.putExtra("sensor", sensor.getId());
        i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        Runnable runnable = new Runnable() {
            @Override
            public void run() {

                //do long stuff (like getting info for intent)
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        activity.startActivity(i);
                        //make new intent
                        //start new activity with intent you just made
                    }
                });
            }
        };
    new Thread(runnable).start();

    }

    //Also create custom listener for the objects that is connected to this class
    //to be able to sync newly added data.

}
