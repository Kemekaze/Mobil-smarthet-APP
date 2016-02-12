package dat065.mobil_smarthet;

import android.text.format.Time;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by backevik on 16-02-11.
 */
public class Sensor{
    SensorTypes type;
    Map<Date,Long> sensorData;

    public Sensor(SensorTypes type){
        this.type = type;
        sensorData = new HashMap<>();
    }

    public Map<Date,Long> getSensorData(){
        return sensorData;
    }

    public void addSensorData(long key, long value){
        sensorData.put(new Date(key*1000L),value);
    }

    public Long getMostRelevantData(){
        Calendar calendar = Calendar.getInstance();
        Date currentTime = new Date(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DATE),calendar.get(Calendar.HOUR),calendar.get(Calendar.MINUTE),calendar.get(Calendar.SECOND));
        if(sensorData.containsKey(currentTime)){
            return sensorData.get(currentTime);
        }else{
            ArrayList<Date> tempList = new ArrayList<>(sensorData.keySet());
            Collections.sort(tempList, Collections.reverseOrder());
            return sensorData.get(tempList.get(0));
        }
    }

    //Add getWeeklyData
    //Add getMonthlyData
}
