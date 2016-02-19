package dat065.mobil_smarthet;

import android.app.Activity;
import android.app.Application;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.joda.time.DateTime;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by backevik on 16-02-11.
 */
public class FavoriteSensors{
    private MainActivity activity;

    private TextView favoriteOneText;
    private TextView favoriteTwoText;

    private Sensor favoriteOne;
    private Sensor favoriteTwo;

    private Handler handler;

    private FavoriteSensorsDBHandler favoriteDBHandler;


    public FavoriteSensors(final MainActivity activity, FavoriteSensorsDBHandler fav){
        this.activity = activity;
        favoriteOneText = (TextView) this.activity.findViewById(R.id.sensorTextOne);
        favoriteTwoText = (TextView) this.activity.findViewById(R.id.sensorTextTwo);

        favoriteDBHandler = fav;

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

    public boolean favorizeSensor(Sensor sensor){
        if(favoriteOne==null){
            favoriteOne = sensor;
            if(favoriteOne.getMostRelevantData()==null){
                favoriteOneText.setText("No data");
            }else{
                favoriteOneText.setText(favoriteOne.getMostRelevantData().toString()+" "+favoriteOne.type.toString());
            }
            favoriteDBHandler.removeFavorite(1);
            favoriteDBHandler.addFavorite(sensor.type,1);
            Log.d("addedFav","");
            return true;
        }else if(favoriteTwo==null){
            favoriteTwo = sensor;
            if(favoriteTwo.getMostRelevantData()==null){
                favoriteTwoText.setText("No data");
            }else{
                favoriteTwoText.setText(favoriteTwo.getMostRelevantData().toString()+" "+favoriteTwo.type.toString());
            }
            favoriteDBHandler.removeFavorite(2);
            favoriteDBHandler.addFavorite(sensor.type,2);
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
        if(favoriteOne.type == sensor.type){
            favoriteOne = null;
            favoriteOneText.setText("Unset");
            favoriteDBHandler.removeFavorite(1);
        }else if(favoriteTwo.type == sensor.type){
            favoriteTwo = null;
            favoriteTwoText.setText("Unset");
            favoriteDBHandler.removeFavorite(2);
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
