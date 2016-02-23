package dat065.mobil_smarthet.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.util.Pair;

import dat065.mobil_smarthet.constants.Database;
import dat065.mobil_smarthet.constants.Settings;

/**
 * Created by elias on 2016-02-18.
 */
public class SettingsDBHandler extends DBHandler {
    public static final String TAG ="DB.Settings";

    public SettingsDBHandler(Context context, SQLiteDatabase.CursorFactory factory) {
        super(context, factory);
    }

    public void add(Pair<Settings,String> pair){
        ContentValues values =  new ContentValues();
        values.put(Database.SETTINGS.COLUMN_KEY, pair.first.getKey());
        values.put(Database.SETTINGS.COLUMN_VALUE, pair.second);
        SQLiteDatabase db = getWritableDatabase();
        db.insertWithOnConflict(Database.SETTINGS.TABLE, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
        Log.i(TAG, "Added/Updated row in "+Database.SETTINGS.TABLE);
    }
    public Pair<Settings,String> get(Settings setting){
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM " + Database.SETTINGS.TABLE + " WHERE "+Database.SETTINGS.COLUMN_KEY+"= \""+setting.getKey()+"\";";
        Cursor c = db.rawQuery(query,null);
        c.moveToFirst();
        String key="";
        String value="";
        try {
            key = c.getString(c.getColumnIndex(Database.SETTINGS.COLUMN_KEY));
            value = c.getString(c.getColumnIndex(Database.SETTINGS.COLUMN_VALUE));
        }catch (Exception e){}
        c.close();
        db.close();
        Log.i(TAG, "Got {'"+key+"','"+value+"'} from "+Database.SETTINGS.TABLE);
        return new Pair(Settings.match(key),value);
    }
    public void remove(Settings setting){
        SQLiteDatabase db = getWritableDatabase();
        int rows = db.delete(Database.SETTINGS.TABLE, "WHERE _key <= ?", new String[]{setting.getKey()});
        db.close();
        Log.i(TAG, "Removed "+rows+" rows from " + Database.SETTINGS.TABLE);
    }

}
