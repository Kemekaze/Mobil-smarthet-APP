package dat065.mobil_smarthet;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.view.View;
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

    private ArrayList<Entry> entries;


    public FavoriteSensors(final MainActivity activity){
        this.activity = activity;
        favoriteOneText = (TextView) this.activity.findViewById(R.id.sensorTextOne);
        favoriteTwoText = (TextView) this.activity.findViewById(R.id.sensorTextTwo);

        entries = new ArrayList<>();

        favoriteOneText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<Date> dailyData = new ArrayList<Date>(favoriteOne.getWeeklyData().keySet());

                int k = 0;
                ArrayList<String> labels = new ArrayList<String>();
                for(Date date : dailyData){
                    entries.add(new Entry(favoriteOne.getSensorData().get(date),k));
                    labels.add("Day "+date.getDay());
                    k++;
                }
                LineDataSet dataset = new LineDataSet(entries, "Daily "+favoriteOne.type.toString());
                LineChart chart = new LineChart(activity.getApplicationContext());
                activity.setContentView(chart);
                LineData data = new LineData(labels,dataset);
                chart.setData(data);
            }
        });

        favoriteTwoText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    public boolean favorizeSensor(Sensor sensor){
        if(favoriteOne==null){
            favoriteOne = sensor;
            favoriteOneText.setText(favoriteOne.getMostRelevantData().toString()+" "+favoriteOne.type.toString()); //favoriteOne.getMostRelevantData().toString()
            return true;
        }else if(favoriteTwo==null){
            favoriteTwo = sensor;
            favoriteTwoText.setText(favoriteTwo.getMostRelevantData().toString()+" "+favoriteTwo.type.toString()); //favoriteTwo.getMostRelevantData().toString()
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
        }else if(favoriteTwo.type == sensor.type){
            favoriteTwo = null;
            favoriteTwoText.setText("Unset");
        }
    }

    //Also create custom listener for the objects that is connected to this class
    //to be able to sync newly added data.

}
