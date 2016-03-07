package dat065.mobil_smarthet.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import dat065.mobil_smarthet.database.SensorDBHandler;
import dat065.mobil_smarthet.database.SettingsDBHandler;
import dat065.mobil_smarthet.event.GraphEvent;
import dat065.mobil_smarthet.event.UpdateGraphEvent;

public class GraphService extends Service{

    private SettingsDBHandler dbSettings;
    private SensorDBHandler dbSensor;

    public GraphService() {

        dbSensor = new SensorDBHandler(this,null);
        dbSettings = new SettingsDBHandler(this,null);
        /*new Thread(new Runnable() {
            @Override
            public void run() {


                while(true){

                    try {
                        Random rand = new Random();
                        HashMap<Long,Double> t = new HashMap<>();

                        double value = 21+rand.nextDouble()*4;
                        Long time = DateTime.now().getMillis();
                        t.put(time, value);

                        dbSensor.addData(new SerializableSensor(t,1));
                        Thread.sleep(10);
                        EventBus.getDefault().post(new UpdateGUIEvent(1, time-10));

                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();*/
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
    @Subscribe(sticky = true, threadMode = ThreadMode.BACKGROUND)
    public void updateGraphData(UpdateGraphEvent ev){
        ArrayList[] data;
        if(ev.hasTime())
            data = dbSensor.getXData(ev.getSensors(),ev.getTime(),ev.getGraphType());
        else
            data = dbSensor.getXData(ev.getSensors(),ev.getGraphType());

        EventBus.getDefault().post(new GraphEvent(data,ev.getGraphType()));

    }







}
