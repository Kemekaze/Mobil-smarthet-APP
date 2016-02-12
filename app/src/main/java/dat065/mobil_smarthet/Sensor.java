package dat065.mobil_smarthet;

import android.text.format.Time;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by backevik on 16-02-11.
 */
public class Sensor {
    SensorTypes type;
    List<Date> sensorDataList;

    public void Sensor(SensorTypes type){
        this.type = type;
        sensorDataList = new ArrayList<Date>();
    }

    public List<Date> getSensorData(){
        return sensorDataList;
    }
    public Date getCurrentData(){
        for(Date time : sensorDataList){

        }
        //FIX
    }
}
