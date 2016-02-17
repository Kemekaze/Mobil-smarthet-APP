package dat065.mobil_smarthet;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.joda.time.DateTime;

import java.util.HashMap;
import java.util.List;

/**
 * Created by backevik on 16-02-16.
 */
public class SensorDBHandler extends DBHandler {
    public SensorDBHandler(Context context, SQLiteDatabase.CursorFactory factory) {
        super(context, factory);
    }
    public void addData(SensorTypes type, DateTime date, double d){
        ContentValues values = new ContentValues();
        SQLiteDatabase db = getWritableDatabase();
        switch (type.name()){
            case "TEMPERATURE":
                values.put(Database.TEMPERATURE.COLUMN_ID, date.toString());
                values.put(Database.TEMPERATURE.COLUMN_VALUE, d);
                db.insert(Database.TEMPERATURE.TABLE,null,values);
                break;
            case "SOUND":
                values.put(Database.SOUND.COLUMN_ID, date.toString());
                values.put(Database.SOUND.COLUMN_VALUE, d);
                db.insert(Database.SOUND.TABLE,null,values);
                break;
            case "LIGHT":
                values.put(Database.LIGHT.COLUMN_ID, date.toString());
                values.put(Database.LIGHT.COLUMN_VALUE, d);
                db.insert(Database.LIGHT.TABLE,null,values);
                break;
            case "ACCELEROMETER":
                values.put(Database.ACCELEROMETER.COLUMN_ID, date.toString());
                values.put(Database.ACCELEROMETER.COLUMN_VALUE, d);
                db.insert(Database.ACCELEROMETER.TABLE,null,values);
        }
        db.close();
    }

    public HashMap<DateTime,Double> getData(SensorTypes type){
        SQLiteDatabase db = getReadableDatabase();
        HashMap<DateTime,Double> data = new HashMap<>();
        Cursor c;
        String query = "";
        switch (type.name()){
            case "TEMPERATURE":
                query = "SELECT * FROM " + Database.TEMPERATURE.TABLE;
                c = db.rawQuery(query,null);
                while(!c.isAfterLast()){
                    String date = c.getString(c.getColumnIndex(Database.TEMPERATURE.COLUMN_ID));
                    DateTime dateTime = DateTime.parse(date);
                    double value = c.getDouble(c.getColumnIndex(Database.TEMPERATURE.COLUMN_VALUE));
                    data.put(dateTime,value);
                    c.moveToNext();
                }
                c.close();
                break;
            case "SOUND":
                query = "SELECT * FROM " + Database.SOUND.TABLE;
                c = db.rawQuery(query,null);
                while(!c.isAfterLast()){
                    String date = c.getString(c.getColumnIndex(Database.SOUND.COLUMN_ID));
                    DateTime dateTime = DateTime.parse(date);
                    double value = c.getDouble(c.getColumnIndex(Database.SOUND.COLUMN_VALUE));
                    data.put(dateTime,value);
                    c.moveToNext();
                }
                c.close();
                break;
            case "LIGHT":
                query = "SELECT * FROM " + Database.LIGHT.TABLE;
                c = db.rawQuery(query,null);
                while(!c.isAfterLast()){
                    String date = c.getString(c.getColumnIndex(Database.LIGHT.COLUMN_ID));
                    DateTime dateTime = DateTime.parse(date);
                    double value = c.getDouble(c.getColumnIndex(Database.LIGHT.COLUMN_VALUE));
                    data.put(dateTime,value);
                    c.moveToNext();
                }
                c.close();
                break;
            case "ACCELEROMETER":
                query = "SELECT * FROM " + Database.ACCELEROMETER.TABLE;
                c = db.rawQuery(query,null);
                while(!c.isAfterLast()){
                    String date = c.getString(c.getColumnIndex(Database.ACCELEROMETER.COLUMN_ID));
                    DateTime dateTime = DateTime.parse(date);
                    double value = c.getDouble(c.getColumnIndex(Database.ACCELEROMETER.COLUMN_VALUE));
                    data.put(dateTime,value);
                    c.moveToNext();
                }
                c.close();
        }
        db.close();
        return data;
    }

    public void removeData(SensorTypes type, DateTime deleteFrom){
        SQLiteDatabase db = getWritableDatabase();
        String deleteString = "DELETE * FROM "+type.name().toLowerCase();
        HashMap<DateTime,Double> tempMap = new HashMap<>(getData(type));
        for(DateTime d : tempMap.keySet()){
            if(d.isBefore(deleteFrom)){
                tempMap.remove(d);
            }
        }
        db.execSQL(deleteString);
        for(DateTime d : tempMap.keySet()){
            addData(type, d, tempMap.get(d));
        }
        db.close();

    }
}
