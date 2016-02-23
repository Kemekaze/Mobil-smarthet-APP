package dat065.mobil_smarthet.sensor;

import org.joda.time.DateTime;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import dat065.mobil_smarthet.constants.Sensors;

/**
 * Created by backevik on 16-02-11.
 */
public class Sensor implements Serializable{
    private Sensors type;
    private HashMap<DateTime,Double> sensorData;

    public Sensor(Sensors type){
        this.type = type;
        sensorData = new HashMap<>();
    }

    public Map<DateTime,Double> getSensorData(){
        return sensorData;
    }

    public Sensors getType(){
        return type;
    }

}
