package dat065.mobil_smarthet;

import android.text.format.Time;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.joda.time.DateTime;

/**
 * Created by backevik on 16-02-11.
 */
public class Sensor implements Serializable{
    SensorTypes type;
    HashMap<DateTime,Long> sensorData;

    public Sensor(SensorTypes type){
        this.type = type;
        sensorData = new HashMap<>();
    }

    public Map<DateTime,Long> getSensorData(){
        return sensorData;
    }

    public void addSensorData(long key, long value){
        sensorData.put(new DateTime(key*1000L),value);
    }

    public void addSensorData(DateTime date, long value){
        sensorData.put(date,value);
    }

    public Long getMostRelevantData(){
        DateTime currentTime = new DateTime();
        if(sensorData.isEmpty()){
            return null;
        }else if(sensorData.containsKey(currentTime)){
            return sensorData.get(currentTime);
        }else{
            ArrayList<DateTime> tempList = new ArrayList<>(sensorData.keySet());
            Collections.sort(tempList, Collections.reverseOrder());
            return sensorData.get(tempList.get(0));
        }
    }

    public HashMap<DateTime,Long> getWeeklyData(){
        DateTime lastWeek = new DateTime(DateTime.now()).minusDays(7);
        HashMap<DateTime,Long> tempMap = new HashMap<>(sensorData);
        for(DateTime date : sensorData.keySet()){
            if(date.isBefore(lastWeek)){
                tempMap.remove(date);
            }
        }
        return tempMap;
    }

    public HashMap<DateTime,Long> getMonthlyData(){
        DateTime lastMonth = new DateTime(DateTime.now()).minusMonths(1);
        HashMap<DateTime,Long> tempMap = new HashMap<>(sensorData);
        for(DateTime date : sensorData.keySet()){
            if(date.isBefore(lastMonth)){
                tempMap.remove(date);
            }
        }
        return tempMap;
    }

    //Add getWeeklyData
    //Add getMonthlyData
}