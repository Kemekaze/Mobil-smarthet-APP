package dat065.mobil_smarthet.sensor;

import android.content.Intent;
import android.os.Handler;
import android.util.Pair;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.data.Entry;

import java.util.ArrayList;

import dat065.mobil_smarthet.GraphActivity;
import dat065.mobil_smarthet.MainActivity;
import dat065.mobil_smarthet.R;
import dat065.mobil_smarthet.constants.Settings;
import dat065.mobil_smarthet.database.SettingsDBHandler;

/**
 * Created by backevik on 16-02-11.
 */
public class FavoriteSensors{
    private MainActivity activity;

    private TextView favoriteOneText;
    private TextView favoriteTwoText;

    private Sensor favoriteOne;
    private Sensor favoriteTwo;

    private ArrayList<Entry> entries;

    private Handler handler;
    private SettingsDBHandler dbSettings;

    public FavoriteSensors(final MainActivity activity, SettingsDBHandler dbSettings){
        this.activity = activity;
        this.dbSettings = dbSettings;
        favoriteOneText = (TextView) this.activity.findViewById(R.id.sensorTextOne);
        favoriteTwoText = (TextView) this.activity.findViewById(R.id.sensorTextTwo);

        handler = new Handler();

        entries = new ArrayList<>();

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

    public boolean favorizeSensor(Sensor sensor){
        if(favoriteOne==null){
            favoriteOne = sensor;
            if(favoriteOne.getMostRelevantData()==null){
                favoriteOneText.setText("No data");
            }else{
                favoriteOneText.setText(favoriteOne.getMostRelevantData().toString() + " " + favoriteOne.getType().getSymbol());
            }
            dbSettings.add(new Pair<Settings, String>(Settings.FAV_SENSOR_1,sensor.getType().getName()));
            return true;
        }else if(favoriteTwo==null){
            favoriteTwo = sensor;
            if(favoriteTwo.getMostRelevantData()==null){
                favoriteTwoText.setText("No data");
            }else{
                favoriteTwoText.setText(favoriteTwo.getMostRelevantData().toString()+" "+favoriteTwo.getType().getSymbol());
            }
            dbSettings.add(new Pair<Settings, String>(Settings.FAV_SENSOR_2,sensor.getType().getName()));
            return true;
        }else{
            Toast.makeText(activity.getApplicationContext(),"You can't have more than 2 favorized sensors!", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    public ArrayList<Sensor> getFavoriteSensors(){
        ArrayList<Sensor> temp = new ArrayList<>();
        temp.add(favoriteOne);
        temp.add(favoriteTwo);
        return temp;
    }

    public void unfavorizeSensor(Sensor sensor){
        if(favoriteOne.getType().equals(sensor.getType())){
            favoriteOne = null;
            favoriteOneText.setText("Unset");
        }else if(favoriteTwo.getType().equals(sensor.getType())){
            favoriteTwo = null;
            favoriteTwoText.setText("Unset");
        }
    }

    public void createLineChart(Sensor sensor){
        if(sensor.getSensorData().isEmpty()){
            Toast.makeText(activity.getApplicationContext(),"No data found",Toast.LENGTH_SHORT);
            return;
        }
        final Intent i = new Intent(activity.getApplicationContext(), GraphActivity.class);
        i.putExtra("sensor", sensor);
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
