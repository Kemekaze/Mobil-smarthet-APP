package dat065.mobil_smarthet.constants;

import android.provider.BaseColumns;

/**
 * Created by backevik on 16-02-16.
 */
public class Database {

    public static final int VERSION = 4;
    public static final String DB_NAME = "mobil_smarthet.db";

    public class TEMPERATURE extends Database implements BaseColumns{
        public static final String TABLE = "Temperature";
        public static final String COLUMN_ID = "_date";
        public static final String COLUMN_VALUE = "value";
    }

    public class LIGHT implements BaseColumns{
        public static final String TABLE = "Light";
        public static final String COLUMN_ID = "_date";
        public static final String COLUMN_VALUE = "value";
    }

    public class AUDIO implements BaseColumns{
        public static final String TABLE = "Audio";
        public static final String COLUMN_ID = "_date";
        public static final String COLUMN_VALUE = "value";
    }

    public class CO2 implements BaseColumns{
        public static final String TABLE = "C02";
        public static final String COLUMN_ID = "_date";
        public static final String COLUMN_VALUE = "value";
    }

    public class MOTION implements BaseColumns{
        public static final String TABLE = "Motion";
        public static final String COLUMN_ID = "_date";
        public static final String COLUMN_VALUE = "value";
    }

    public class SETTINGS implements BaseColumns{
        public static final String TABLE = "Settings";
        public static final String COLUMN_KEY = "_key";
        public static final String COLUMN_VALUE = "value";
    }


    public class FAVORITESENSORS implements BaseColumns{
        public static final String TABLE = "favoritesensors";
        public static final String COLUMN_ID = "_id";
        public static final String COLUMN_SENSOR = "type";
    }

}
