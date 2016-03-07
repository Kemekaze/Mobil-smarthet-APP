package dat065.mobil_smarthet.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.util.Pair;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import dat065.mobil_smarthet.constants.Presets;
import dat065.mobil_smarthet.constants.Sensors;
import dat065.mobil_smarthet.constants.Settings;
import dat065.mobil_smarthet.database.SensorDBHandler;
import dat065.mobil_smarthet.database.SettingsDBHandler;
import dat065.mobil_smarthet.event.FavourizeEvent;
import dat065.mobil_smarthet.event.GraphEvent;
import dat065.mobil_smarthet.event.SensorEvent;
import dat065.mobil_smarthet.event.SnackbarEvent;
import dat065.mobil_smarthet.event.SwitchEvent;
import dat065.mobil_smarthet.event.UpdateGUIEvent;
import dat065.mobil_smarthet.event.UpdateGraphEvent;
import dat065.mobil_smarthet.helpers.Helpers;

public class SensorService extends Service{

    private static SensorService sensorService;
    private SettingsDBHandler dbSettings;
    private SensorDBHandler dbSensor;
    private Settings[] favoriteSensors;
    private boolean isRunning = true;


    public SensorService() {

        dbSensor = new SensorDBHandler(this,null);
        dbSettings = new SettingsDBHandler(this,null);
        favoriteSensors= Settings.getfavourites();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void favorizeSensor(FavourizeEvent ev) {
        int[] status = checkFavorizeSensor(ev.getSensor());
        Log.i("status", "" + status[0] + ":" + status[1]);
        switch (status[0]){
            case 1:
                EventBus.getDefault().post(new SnackbarEvent("Favourized!"));
                EventBus.getDefault().post(new SwitchEvent(ev.getSensor(), true));
                EventBus.getDefault().post(new SensorEvent(
                            ev.getSensor(),
                            favoriteSensors[status[1]],
                            dbSensor.getMostRelevantData(ev.getSensor()))
                 );
                break;
            case 0:
                EventBus.getDefault().post(new SensorEvent(favoriteSensors[status[1]], "Unset"));
                EventBus.getDefault().post(new SnackbarEvent("Unfavorized!"));
                EventBus.getDefault().post(new SwitchEvent(ev.getSensor(), false));
                break;
            case -1:
                EventBus.getDefault().post(new SnackbarEvent("Limit reached!"));
                break;
            default:
                break;

        }
    }
    public int[] checkFavorizeSensor(Sensors sensor) {
        int isAvailable = -1;
        for (int i=0;i<favoriteSensors.length;i++){
            Sensors s = Sensors.match(dbSettings.get(favoriteSensors[i]).second);
            if(s == null){
                if(isAvailable == -1) isAvailable = i;
            }else if(s.equals(sensor)){
                dbSettings.add(new Pair<Settings, String>(favoriteSensors[i], ""));
                return new int[]{0,i};
            }
        }
        if(isAvailable == -1) return new int[]{-1,-1};;
        dbSettings.add(new Pair<Settings, String>(favoriteSensors[isAvailable], sensor.getName()));
        return new int[]{1,isAvailable};
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.BACKGROUND)
    public void updateGUI(UpdateGUIEvent ev){
        switch (ev.getType()){
            case 1:
                updateFavSensors();
                break;
            case 2:
                changeOfPreset(ev);
                break;
            default:
                break;
        }

    }
    private void updateFavSensors(){
        Presets preset = Presets.match(dbSettings.get(Settings.PRESET).second);

        for (int i = 0; i < favoriteSensors.length; i++) {
            Sensors s = null;
            if(preset == null || preset.equals(Presets.FAVOURITES))
                s = Sensors.match(dbSettings.get(favoriteSensors[i]).second);
            else s = preset.getSensors()[i];
            SensorEvent se;
            if (s != null) {

                Double val = Helpers.round(dbSensor.getMostRelevantData(s),2);
                Log.i("SS",s.getName()+" "+val);
                se = new SensorEvent(s, favoriteSensors[i], val);
            } else {
                se = new SensorEvent(favoriteSensors[i], "Unset");
            }
            EventBus.getDefault().post(se);
        }

    }
    private void changeOfPreset(UpdateGUIEvent ev){
        Log.i("changePreset","Data "+(int)ev.getData());
        dbSettings.add(new Pair<Settings, String>(Settings.PRESET, Presets.match((int)ev.getData()).getKey()));
        updateFavSensors();
    }
    @Subscribe(sticky = true, threadMode = ThreadMode.BACKGROUND)
    private void updateGraphData(UpdateGraphEvent ev){
        ArrayList[] data;
        if(ev.hasTime())
            data = dbSensor.getXData(ev.getSensors(),ev.getTime(),ev.getGraphType());
        else
            data = dbSensor.getXData(ev.getSensors(),ev.getGraphType());

        EventBus.getDefault().post(new GraphEvent(data,ev.getGraphType()));

    }







}
