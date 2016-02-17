package dat065.mobil_smarthet;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by backevik on 16-02-17.
 */
public class FavoriteSensorsDBHandler extends DBHandler {
    public FavoriteSensorsDBHandler(Context context, SQLiteDatabase.CursorFactory factory) {
        super(context, factory);
    }
    public void addFavorite(SensorTypes type, int spot) {
        ContentValues values = new ContentValues();
        SQLiteDatabase db = getWritableDatabase();
        if(spot<1 || spot>2){
            return;
        }
        values.put(Database.FAVORITESENSORS.COLUMN_ID,spot);
        values.put(Database.FAVORITESENSORS.COLUMN_SENSOR,type.name().toLowerCase());
        db.insert(Database.FAVORITESENSORS.TABLE,null,values);
        db.close();
    }

    public void removeFavorite(int spot){
        SQLiteDatabase db = getWritableDatabase();
        if(spot<1 || spot>2){
            return;
        }
        String deleteString = "DELETE FROM " + Database.FAVORITESENSORS.TABLE + " WHERE " + Database.FAVORITESENSORS.COLUMN_ID
                + "= \"" + spot + "\";";
        db.execSQL(deleteString);
        db.close();

    }

    public HashMap<Integer,String> getFavorites(){
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM "+Database.FAVORITESENSORS.TABLE;
        Cursor c = db.rawQuery(query,null);
        HashMap<Integer,String> sensors = new HashMap<>();
        c.moveToFirst();
        while(!c.isAfterLast()){
            int spot = c.getInt(c.getColumnIndex(Database.FAVORITESENSORS.COLUMN_ID));
            String sensor = c.getString(c.getColumnIndex(Database.FAVORITESENSORS.COLUMN_SENSOR));
            sensors.put(spot,sensor);
            c.moveToNext();
        }
        c.close();
        db.close();
        return sensors;
    }
}
