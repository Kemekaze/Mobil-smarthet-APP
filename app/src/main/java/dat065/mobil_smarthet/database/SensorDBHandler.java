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
    public int addData(SerializableSensor sensor){
        Sensors s = Sensors.match(sensor.getSensor());
        ContentValues values =  new ContentValues();
        SQLiteDatabase db = getWritableDatabase();
        HashMap<Integer, Double> data = sensor.getData();
        int lastTime = -1;
        for (int time : data.keySet()){
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
            data.put(new DateTime().withMillis(c.getInt(0)*1000),c.getDouble(1));
            c.moveToNext();
        }
        c.close();
        db.close();
        Log.i(TAG, "Got " + data.size() + " rows from " + sensor.getName());
        return data;
    }
    private HashMap<Integer,Double> getCompData(Sensors sensor, int time){
        HashMap<Integer,Double> data = new HashMap<Integer,Double>();

        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM " + sensor.getName() +" WHERE _date > "+time +";";
        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();
        while(!c.isAfterLast()){
            data.put(c.getInt(0),c.getDouble(1));
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
        long millis = deleteTo.getMillis()/1000;
        int rows = db.delete(sensor.getName(),"_date <= ?",new String[]{(String.valueOf((int)millis))});
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
    private HashMap<DateTime,Double> intToDateTime(HashMap<Integer,Double> data){
        HashMap<DateTime,Double> newData =  new HashMap<DateTime,Double>();
        for(int d:data.keySet()){
            newData.put(new DateTime().withMillis(d*1000L),data.get(d));
        }
        return newData;
    }
    public HashMap<DateTime,Double> getWeeklyData(Sensors sensor){
        long time = DateTime.now().minusDays(7).getMillis()/1000;
        return intToDateTime(getCompData(sensor, (int) time));
    }

    public HashMap<DateTime,Double> getMonthlyData(Sensors sensor){
        long time = DateTime.now().minusMonths(1).getMillis()/1000;
        return intToDateTime(getCompData(sensor, (int) time));
    }
}
