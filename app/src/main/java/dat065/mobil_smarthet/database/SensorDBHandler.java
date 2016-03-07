package dat065.mobil_smarthet.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.github.mikephil.charting.data.Entry;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.HashMap;

import dat065.mobil_smarthet.bluetooth.SerializableSensor;
import dat065.mobil_smarthet.constants.Sensors;

/**
 * Created by backevik on 16-02-16.
 */
public class SensorDBHandler extends DBHandler {

    public static final String TAG ="DB.Sensor";
    public SensorDBHandler(Context context, SQLiteDatabase.CursorFactory factory) {
        super(context, factory);
    }
    public long addData(SerializableSensor sensor){
        Sensors s = Sensors.match(sensor.getSensor());
        ContentValues values =  new ContentValues();
        SQLiteDatabase db = getWritableDatabase();
        HashMap<Long, Double> data = sensor.getData();
        long lastTime = -1;
        for (long time : data.keySet()){
            values.put("_date", time);
            values.put("value", data.get(time));
            //db.insert(s.getName(), null, values);
            db.insertWithOnConflict(s.getName(),null,values,SQLiteDatabase.CONFLICT_IGNORE);
            if(time>lastTime) lastTime = time;
        }
        db.close();
        Log.i(TAG, "Added " + data.size() + " rows into " + s.getName());
        return lastTime;
    }

    public HashMap<DateTime,Double> getData(Sensors sensor){
        return getData(sensor,"");
    }

    private HashMap<DateTime,Double> getData(Sensors sensor, String qPlus){
        HashMap<DateTime,Double> data = new HashMap<DateTime,Double>();

        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM " + sensor.getName() + ((qPlus.equals(""))?";":" "+qPlus+";");
        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();
        while(!c.isAfterLast()){
            data.put(new DateTime().withMillis(c.getLong(0)),c.getDouble(1));
            c.moveToNext();
        }
        c.close();
        db.close();
        Log.i(TAG, "Got " + data.size() + " rows from " + sensor.getName());
        return data;
    }
    private HashMap<Long,Double> getCompData(Sensors sensor, long time){
        HashMap<Long,Double> data = new HashMap<Long,Double>();

        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM " + sensor.getName() +" WHERE _date > "+time +";";
        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();
        while(!c.isAfterLast()){
            data.put(c.getLong(0),c.getDouble(1));
            c.moveToNext();
        }
        c.close();
        db.close();
        Log.i(TAG, "Got " + data.size() + " rows from " + sensor.getName());
        return data;
    }

    private Class<?> getSensorTable(Sensors sensor) throws ClassNotFoundException {
        return Class.forName("dat065.mobil_smarthet.constants."+sensor.getName().toUpperCase());
    }
    public HashMap<DateTime,Double> getData(int sensor){
        Sensors s = Sensors.match(sensor);
        return (s != null)? getData(s):null;
    }


    public void removeData(Sensors sensor, DateTime deleteTo){
        SQLiteDatabase db = getWritableDatabase();
        long millis = deleteTo.getMillis();
        int rows = db.delete(sensor.getName(),"_date <= ?",new String[]{(String.valueOf(millis))});
        db.close();
        Log.i(TAG, "Removed "+rows+" rows from " + sensor.getName());
    }
    public Double getMostRelevantData(Sensors sensor){
        HashMap<DateTime,Double> data = getData(sensor, "ORDER BY _date DESC LIMIT 1");
        for(DateTime d : data.keySet()){
            return data.get(d);
        }
        return 0.0;
    }
    public Double getMeanLastHour(Sensors sensor){
        long time = DateTime.now().minusHours(1).getMillis();
        HashMap<Long,Double> data = getCompData(sensor, time);
        Double rtn = 0.0;
        for(Long d : data.keySet()){
            rtn+=data.get(d);
        }
        return rtn/data.size();
    }

    public String labelFormat(DateTime date, int labelType){
        String rtn;
        switch (labelType){
            //hour
            case 0:
                rtn = date.getMinuteOfHour()+":"+date.getSecondOfMinute();
                break;
            //day
            case 1:
                rtn = date.getHourOfDay()+":"+date.getMinuteOfHour();
                break;
            //week
            case 2:
                rtn = date.getDayOfMonth()+"-"+date.getHourOfDay();
                break;
            //month
            case 3:
                rtn = date.getMonthOfYear()+"/"+date.getDayOfMonth();
                break;
            default:
                rtn = date.getDayOfMonth()+":"+date.getHourOfDay();
                break;

        }
        return rtn;
    }
    private ArrayList[] intToDateTime(HashMap<Long,Double> data, int labelType){
        ArrayList<Entry> entries = new ArrayList<Entry>();
        ArrayList<String> labels = new ArrayList<String>();
        int i = 0;
        for(long d:data.keySet()){
            entries.add(new Entry(data.get(d).floatValue(),i));
            DateTime date = new DateTime().withMillis(d);
            labels.add(labelFormat(date,labelType));
            i++;
        }
        return new ArrayList[]{entries,labels};
    }
    private ArrayList[] intToDateTime(HashMap<Long,Double> data, int labelType, int limit){
        if(limit<2) return intToDateTime(data,labelType);
        ArrayList<Entry> entries = new ArrayList<Entry>();
        ArrayList<String> labels = new ArrayList<String>();
        HashMap<Long,Double> meanData = new HashMap<Long,Double>();
        int counter = 0;
        for(long timeKey:data.keySet()){
            ++counter;
            meanData.put(timeKey, data.get(timeKey));
            if(counter%limit == 0){
                double val = 0;
                for (long meanKey : meanData.keySet()) {
                    val += meanData.get(meanKey);
                }
                entries.add(new Entry(data.get(timeKey).floatValue(),counter/limit));
                DateTime date = new DateTime().withMillis(timeKey);
                labels.add(labelFormat(date, labelType));
                meanData.clear();
            }
        }
        return new ArrayList[]{entries,labels};
    }

    private ArrayList[] limitPoints(Sensors sensor,long time, int type){
        HashMap<Long,Double> data = getCompData(sensor, time);
        //int limiter = 100;
        //int limit = (data.size()-(data.size())%limiter)/limiter;
        int limit = 1;
        return intToDateTime(data,type,limit);
    }
    public ArrayList[] getHourlyData(Sensors sensor){
        long time = DateTime.now().minusHours(1).getMillis();
        return limitPoints(sensor, time, 0);
    }
    public ArrayList[] getDailyData(Sensors sensor){
        long time = DateTime.now().minusDays(1).getMillis();
        return limitPoints(sensor, time, 1);
    }
    public ArrayList[] getWeeklyData(Sensors sensor){
        long time = DateTime.now().minusWeeks(1).getMillis();
        return limitPoints(sensor, time, 2);
    }
    public ArrayList[] getMonthlyData(Sensors sensor){
        long time = DateTime.now().minusMonths(1).getMillis();
        return limitPoints(sensor,time,3);
    }
    public ArrayList[] getXData(Sensors sensor,long time,int type){
        return limitPoints(sensor,time,type);
    }
    public ArrayList[] getXData(Sensors sensor,int type){
        long time;
        switch (type){
            case 0:
                time = DateTime.now().minusHours(1).getMillis();
                break;
            case 1:
                time = DateTime.now().minusDays(1).getMillis();
                break;
            case 2:
                time = DateTime.now().minusWeeks(1).getMillis();
                break;
            case 3:
                time = DateTime.now().minusMonths(1).getMillis();
                break;
            default:
                time = DateTime.now().minusWeeks(1).getMillis();
                break;

        }
        return limitPoints(sensor,time,type);
    }
}
