package dat065.mobil_smarthet.event;

import dat065.mobil_smarthet.constants.Sensors;
import dat065.mobil_smarthet.constants.Settings;

/**
 * Created by elias on 2016-02-26.
 */
public class SensorEvent {
    private Sensors sensor;
    private Settings setting;
    private double value;
    private String altText = "";

    public SensorEvent(Sensors sensor, Settings setting, double value) {
        this.sensor = sensor;
        this.setting = setting;
        this.value = value;
    }
    public SensorEvent(Settings setting, String text){
        this.setting = setting;
        this.altText = text;
    }
    public String getAltText(){
        return altText;
    }
    public Sensors getSensor() {
        return sensor;
    }

    public Settings getSetting() {
        return setting;
    }

    public double getValue() {
        return value;
    }
}
