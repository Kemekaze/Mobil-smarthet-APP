package dat065.mobil_smarthet.sensor;

import android.content.Context;
import android.util.Pair;

import org.greenrobot.eventbus.EventBus;

import dat065.mobil_smarthet.constants.Sensors;
import dat065.mobil_smarthet.constants.Settings;
import dat065.mobil_smarthet.database.SensorDBHandler;
import dat065.mobil_smarthet.database.SettingsDBHandler;
import dat065.mobil_smarthet.event.SensorEvent;

public class SensorService implements Runnable{

    private static SensorService sensorService;
    private SettingsDBHandler dbSettings;
    private SensorDBHandler dbSensor;
    private Settings[] favoriteSensors;
    private boolean isRunning = true;
    private Thread t;


    public SensorService(Context context) {

        dbSensor = new SensorDBHandler(context,null);
        dbSettings = new SettingsDBHandler(context,null);
        favoriteSensors= Settings.getfavourites();
        t = new Thread(this);

    }
    public void run() {
        while(true)
        {
            while (isRunning){
                try {
                    for(int i = 0; i < favoriteSensors.length;i++) {

                        Sensors s = Sensors.match(dbSettings.get(favoriteSensors[i]).second);
                        SensorEvent se;
                        if(s != null) {
                            Double val = dbSensor.getMostRelevantData(s);
                            se = new SensorEvent(s, favoriteSensors[i], val);
                        }else {
                            se = new SensorEvent(favoriteSensors[i], "Unset");
                        }
                        EventBus.getDefault().post(se);
                    }
                    Thread.sleep(5000);
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
        }

    }
    public void start(){
        t.start();
    }
    public void stop(){
        isRunning = false;
    }


    public int clickFavorizeSensor(Sensors sensor) {
        int isAvailable = -1;
        for (int i=0;i<favoriteSensors.length;i++){
            Sensors s = Sensors.match(dbSettings.get(favoriteSensors[i]).second);
            if(s == null){
                if(isAvailable == -1) isAvailable = i;
            }else if(s.equals(sensor)){
                dbSettings.add(new Pair<Settings, String>(favoriteSensors[i], ""));
                return -1;
            }
        }
        if(isAvailable == -1) return -2;
        dbSettings.add(new Pair<Settings, String>(favoriteSensors[isAvailable],sensor.getName()));
        return isAvailable;
    }

}
