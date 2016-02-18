package dat065.mobil_smarthet;

import android.provider.BaseColumns;

/**
 * Created by backevik on 16-02-16.
 */
public class Database {
    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    public Database() {}

    public static final int VERSION = 13;
    public static final String DB_NAME = "sensor.db";
    public class TEMPERATURE implements BaseColumns{
        public static final String TABLE = "temperature";
        public static final String COLUMN_ID = "_date";
        public static final String COLUMN_VALUE = "value";
    }
    public class LIGHT implements BaseColumns{
        public static final String TABLE = "light";
        public static final String COLUMN_ID = "_date";
        public static final String COLUMN_VALUE = "value";
    }
    public class SOUND implements BaseColumns{
        public static final String TABLE = "sound";
        public static final String COLUMN_ID = "_date";
        public static final String COLUMN_VALUE = "value";
    }
    public class ACCELEROMETER implements BaseColumns{
        public static final String TABLE = "accelerometer";
        public static final String COLUMN_ID = "_date";
        public static final String COLUMN_VALUE = "value";
    }
    public class FAVORITESENSORS implements BaseColumns{
        public static final String TABLE = "favoritesensors";
        public static final String COLUMN_ID = "_id";
        public static final String COLUMN_SENSOR = "type";
    }
}
