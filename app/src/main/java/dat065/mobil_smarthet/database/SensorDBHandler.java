package dat065.mobil_smarthet.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.joda.time.DateTime;

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
        Log.d("QueryTest",query);
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
    private HashMap<DateTime,Double> intToDateTime(HashMap<Long,Double> data){
        HashMap<DateTime,Double> newData =  new HashMap<DateTime,Double>();
        for(long d:data.keySet()){
            newData.put(new DateTime().withMillis(d),data.get(d));
        }
        return newData;
    }
    private HashMap<DateTime,Double> intToDateTime(HashMap<Long,Double> data, int limit){
        if(limit<2) return intToDateTime(data);
        HashMap<DateTime,Double> newData =  new HashMap<DateTime,Double>();
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
                newData.put(new DateTime().withMillis(timeKey),val/limit);
                meanData.clear();
            }
        }
        return newData;
    }
    public HashMap<DateTime,Double> getWeeklyData(Sensors sensor){
        long time = DateTime.now().minusDays(7).getMillis();
        return intToDateTime(getCompData(sensor, time));
    }

    public HashMap<DateTime,Double> getMonthlyData(Sensors sensor){
        long time = DateTime.now().minusMonths(1).getMillis();
        HashMap<Long,Double> data = getCompData(sensor, (int) time);
        int limiter = 100;
        int limit = (data.size()-(data.size())%limiter)/limiter;
        Log.i("db","Size: "+data.size()+" limit: "+limit);
        return intToDateTime(getCompData(sensor, (int) time),limit);
    }
}
