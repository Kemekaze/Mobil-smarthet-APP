package dat065.mobil_smarthet.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import dat065.mobil_smarthet.constants.Database;

/**
 * Created by backevik on 16-02-16.
 */
public class DBHandler  extends SQLiteOpenHelper {

    public static final String TAG ="DB";
    /*BEGIN_SENSORS*/
    String queryTemperatue = "CREATE TABLE "+ Database.TEMPERATURE.TABLE + "(" +
            Database.TEMPERATURE.COLUMN_ID + " INTEGER PRIMARY KEY, " +
            Database.TEMPERATURE.COLUMN_VALUE + " DOUBLE " +
            ");";

    String queryLight = "CREATE TABLE "+ Database.LIGHT.TABLE + "(" +
            Database.LIGHT.COLUMN_ID + " INTEGER PRIMARY KEY, " +
            Database.LIGHT.COLUMN_VALUE + " DOUBLE " +
            ");";

    String queryAudio = "CREATE TABLE "+ Database.AUDIO.TABLE + "(" +
            Database.AUDIO.COLUMN_ID + " INTEGER PRIMARY KEY, " +
            Database.AUDIO.COLUMN_VALUE + " DOUBLE " +
            ");";

    String queryCo2 = "CREATE TABLE "+ Database.CO2.TABLE + "(" +
            Database.CO2.COLUMN_ID + " INTEGER PRIMARY KEY, " +
            Database.CO2.COLUMN_VALUE + " DOUBLE " +
            ");";

    String queryMotion = "CREATE TABLE "+ Database.MOTION.TABLE + "(" +
            Database.MOTION.COLUMN_ID + " INTEGER PRIMARY KEY, " +
            Database.MOTION.COLUMN_VALUE + " DOUBLE " +
            ");";
    /*END_SENSORS*/

    String querySettings = "CREATE TABLE "+ Database.SETTINGS.TABLE + "(" +
            Database.SETTINGS.COLUMN_KEY + " TEXT PRIMARY KEY, " +
            Database.SETTINGS.COLUMN_VALUE + " TEXT UNIQUE " +
            ");";

    public DBHandler(Context context, SQLiteDatabase.CursorFactory factory) {
        super(context, Database.DB_NAME, factory, Database.VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        /*BEGIN_SENSORS*/
        db.execSQL(queryTemperatue);
        db.execSQL(queryLight);
        db.execSQL(queryAudio);
        db.execSQL(queryCo2);
        db.execSQL(queryMotion);
        /*END_SENSORS*/
        db.execSQL(querySettings);
        Log.d(TAG, "Tables created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        /*BEGIN_SENSORS*/
        db.execSQL("DROP TABLE IF EXISTS " + Database.TEMPERATURE.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + Database.LIGHT.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + Database.AUDIO.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + Database.CO2.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + Database.MOTION.TABLE);
        /*END_SENSORS*/
        db.execSQL("DROP TABLE IF EXISTS " + Database.SETTINGS.TABLE);
        onCreate(db);
    }
}
