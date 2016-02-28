package dat065.mobil_smarthet.constants;

import java.util.ArrayList;

/**
 * Created by elias on 2016-02-18.
 */
public enum Settings{
    LAST_SENSOR_TIME("time"),
    PRESET("preset"),
    FAV_SENSOR_1("fav_1"),
    FAV_SENSOR_2("fav_2");

    private final String key;

    private Settings(String key){
        this.key = key;
    }
    public String getKey(){
        return key;
    }

    public static Settings match(String setting){
        for(Settings s : Settings.values())
            if(s.getKey().equals(setting)) return s;
        return null;
    }
    public static Settings[] getfavourites(){
        ArrayList<Settings> sArr = new ArrayList<Settings>();
        for(Settings s : Settings.values())
            if(s.getKey().contains("fav")) sArr.add(s);
        return sArr.toArray(new Settings[sArr.size()]);
    }
}
