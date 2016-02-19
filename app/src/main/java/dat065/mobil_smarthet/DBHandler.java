package dat065.mobil_smarthet;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.SyncStateContract;
import android.util.Log;

import org.joda.time.DateTime;

/**
 * Created by backevik on 16-02-16.
 */
public class DBHandler  extends SQLiteOpenHelper {
    public DBHandler(Context context, SQLiteDatabase.CursorFactory factory) {
        super(context, Database.DB_NAME, factory, Database.VERSION);
    }

    String queryTemperatue = "CREATE TABLE "+Database.TEMPERATURE.TABLE + "(" +
            Database.TEMPERATURE.COLUMN_ID + " TEXT PRIMARY KEY, " +
            Database.TEMPERATURE.COLUMN_VALUE + " DOUBLE " +
            ");";

    String querySound = "CREATE TABLE "+Database.SOUND.TABLE + "(" +
            Database.SOUND.COLUMN_ID + " TEXT PRIMARY KEY, " +
            Database.SOUND.COLUMN_VALUE + " DOUBLE " +
            ");";

    String queryAccelerometer = "CREATE TABLE "+Database.ACCELEROMETER.TABLE + "(" +
            Database.ACCELEROMETER.COLUMN_ID + " TEXT PRIMARY KEY, " +
            Database.ACCELEROMETER.COLUMN_VALUE + " DOUBLE " +
            ");";

    String queryLight = "CREATE TABLE "+Database.LIGHT.TABLE + "(" +
            Database.LIGHT.COLUMN_ID + " TEXT PRIMARY KEY, " +
            Database.LIGHT.COLUMN_VALUE + " DOUBLE " +
            ");";

    String queryFavorites = "CREATE TABLE "+Database.FAVORITESENSORS.TABLE + "(" +
            Database.FAVORITESENSORS.COLUMN_ID + " INT PRIMARY KEY, " +
            Database.FAVORITESENSORS.COLUMN_SENSOR + " TEXT UNIQUE " +
            ");";

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(queryTemperatue);
        db.execSQL(querySound);
        db.execSQL(queryAccelerometer);
        db.execSQL(queryLight);
        db.execSQL(queryFavorites);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + Database.LIGHT.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + Database.SOUND.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + Database.ACCELEROMETER.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + Database.TEMPERATURE.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + Database.FAVORITESENSORS.TABLE);
        onCreate(db);
    }
}
