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
    private HashMap<DateTime,Double> sensorData;

    public Sensor(SensorTypes type){
        this.type = type;
        sensorData = new HashMap<>();
    }

    public Map<DateTime,Double> getSensorData(){
        return sensorData;
    }

    public void addSensorData(long key, double value){
        sensorData.put(new DateTime(key*1000),value);
    }

    public void addSensorData(DateTime date, double value){
        sensorData.put(date,value);
    }

    public Double getMostRelevantData(){
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

    public HashMap<DateTime,Double> getWeeklyData(){
        HashMap<DateTime,Double> weeklyData = new HashMap<>();
        ArrayList<ArrayList<Double>> almightyList = new ArrayList<>();
        for(int i = 0;i<7;i++){
            almightyList.add(new ArrayList<Double>());
        }
        DateTime currentTime = new DateTime(DateTime.now());
        for(DateTime d : sensorData.keySet()){
            int divider = 0;
            double sum = 0;
            for(int i = 0; i<7;i++){
                if(d.dayOfYear().get()==currentTime.minusDays(i).dayOfYear().get()){
                    almightyList.get(i).add(sensorData.get(d));
                }
            }
        }
        for(int i = 0; i<almightyList.size();i++){
            if(almightyList.get(i).isEmpty()){
                weeklyData.put(currentTime.minusDays(i),0D);
            }else{
                double sum = 0;
                for(int k = 0; k<almightyList.get(i).size();k++){
                   sum = sum + almightyList.get(i).get(k);
                }
                sum = sum / almightyList.get(i).size();
                weeklyData.put(currentTime.minusDays(i),sum);
            }
        }
        return weeklyData;
    }

    public HashMap<DateTime,Double> getMonthlyData(){
        HashMap<DateTime,Double> monthlyData = new HashMap<>();
        ArrayList<ArrayList<Double>> almightyList = new ArrayList<>();
        for(int i = 0;i<30;i++){
            almightyList.add(new ArrayList<Double>());
        }
        DateTime currentTime = new DateTime(DateTime.now());
        for(DateTime d : sensorData.keySet()){
            int divider = 0;
            double sum = 0;
            for(int i = 0; i<30;i++){
                if(d.dayOfYear().get()==currentTime.minusDays(i).dayOfYear().get()){
                    almightyList.get(i).add(sensorData.get(d));
                }
            }
        }
        for(int i = 0; i<almightyList.size();i++){
            if(almightyList.get(i).isEmpty()){
                monthlyData.put(currentTime.minusDays(i),0D);
            }else{
                double sum = 0;
                for(int k = 0; k<almightyList.get(i).size();k++){
                    sum = sum + almightyList.get(i).get(k);
                }
                sum = sum / almightyList.get(i).size();
                monthlyData.put(currentTime.minusDays(i),sum);
            }
        }
        return monthlyData;
    }

    //Add getWeeklyData
    //Add getMonthlyData
}
