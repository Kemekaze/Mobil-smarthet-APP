package dat065.mobil_smarthet;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by backevik on 16-02-11.
 */
public class FavoriteSensors extends Application{
    private Activity activity;

    private LinearLayout favoriteOneLayout;
    private TextView favoriteOneText;

    private LinearLayout favoriteTwoLayout;
    private TextView favoriteTwoText;

    private Sensor favoriteOne;
    private Sensor favoriteTwo;

    public void FavoriteSensors(Activity activity){
        this.activity = activity;
        favoriteOneLayout = (LinearLayout) this.activity.findViewById(R.id.sensorLayoutOne);
        favoriteOneText = (TextView) favoriteOneLayout.findViewById(R.id.sensorTextOne);
        favoriteTwoLayout = (LinearLayout) this.activity.findViewById(R.id.sensorLayoutTwo);
        favoriteTwoText = (TextView) favoriteTwoLayout.findViewById(R.id.sensorTextTwo);
    }

    public void favorizeSensor(Sensor sensor){
        if(favoriteOne==null){
            favoriteOne = sensor;
            favoriteOneText.setText("");
        }else if(favoriteTwo==null){
            favoriteTwo = sensor;
        }
    }
    public void unfavorizeSensor(Sensor sensor){

    }

}
