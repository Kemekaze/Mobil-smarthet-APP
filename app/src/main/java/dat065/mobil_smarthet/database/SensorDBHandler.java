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
        HashMap<DateTime,Double> data = new HashMap<DateTime,Double>();

        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM " + sensor.getName() + ";";
        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();
        while(!c.isAfterLast()){
            data.put(new DateTime().withMillis(c.getInt(1)*1000),c.getDouble(2));
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
    public int getLastRecievedTime(){
        return 0;
    }
}
