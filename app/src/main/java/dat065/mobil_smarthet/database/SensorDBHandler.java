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
    public void addData(SerializableSensor sensor){
        Sensors s = Sensors.match(sensor.getSensor());
        ContentValues values =  new ContentValues();
        SQLiteDatabase db = getWritableDatabase();
        HashMap<Integer, Double> data = sensor.getData();
        for (int time : data.keySet()){
            values.put("_date",new DateTime(time*1000L).toDateTimeISO().toString());
            values.put("value",data.get(time));
            db.insert(s.getName(),null,values);
        }
        db.close();
        Log.i(TAG, "Added "+data.size()+" rows into " + s.getName());
    }

    public HashMap<DateTime,Double> getData(Sensors sensor){
        HashMap<DateTime,Double> data = new HashMap<DateTime,Double>();

        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM " + sensor.getName() + ";";
        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();
        while(!c.isAfterLast()){
            data.put(new DateTime(c.getString(1)),c.getDouble(2));
            c.moveToNext();
        }
        c.close();
        db.close();
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
        String deleteString = "DELETE * FROM "+sensor.getName()+ "WHERE _date <= "+deleteTo.toDateTimeISO().toString();
        db.execSQL(deleteString);
        db.close();
    }
}
