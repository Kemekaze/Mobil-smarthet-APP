package dat065.mobil_smarthet;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by backevik on 16-02-11.
 */
public class FavoriteSensors{
    private MainActivity activity;

    private TextView favoriteOneText;

    private TextView favoriteTwoText;

    private Sensor favoriteOne;
    private Sensor favoriteTwo;

    private int nrFavorites;

    public FavoriteSensors(MainActivity activity){
        nrFavorites = 0;
        this.activity = activity;
        favoriteOneText = (TextView) this.activity.findViewById(R.id.sensorTextOne);
        favoriteTwoText = (TextView) this.activity.findViewById(R.id.sensorTextTwo);
    }

    public boolean favorizeSensor(Sensor sensor){
        if(favoriteOne==null){
            favoriteOne = sensor;
            favoriteOneText.setText("NO VAL "+favoriteOne.type.toString()); //favoriteOne.getMostRelevantData().toString()
            nrFavorites++;
            return true;
        }else if(favoriteTwo==null){
            favoriteTwo = sensor;
            favoriteTwoText.setText("NO VAL "+favoriteTwo.type.toString()); //favoriteTwo.getMostRelevantData().toString()
            nrFavorites++;
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

    public int nrFavoriteSensors(){
        return nrFavorites;
    }

    public void unfavorizeSensor(Sensor sensor){
        if(favoriteOne.type == sensor.type){
            favoriteOne = null;
            favoriteOneText.setText("Unset");
            nrFavorites--;
        }else if(favoriteTwo.type == sensor.type){
            favoriteTwo = null;
            favoriteTwoText.setText("Unset");
            nrFavorites--;
        }
    }

    //Also create custom listener for the objects that is connected to this class
    //to be able to sync newly added data.

}
