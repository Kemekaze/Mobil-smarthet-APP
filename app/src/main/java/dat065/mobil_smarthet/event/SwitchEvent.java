package dat065.mobil_smarthet.event;

import dat065.mobil_smarthet.constants.Sensors;

/**
 * Created by elias on 2016-02-28.
 */
public class SwitchEvent {
    boolean state;
    Sensors sensors;

    public SwitchEvent(Sensors sensors, boolean state) {
        this.sensors = sensors;
        this.state = state;
    }

    public Sensors getSensor() {
        return sensors;
    }

    public boolean getState() {
        return state;
    }
}
