package dat065.mobil_smarthet.event;

import dat065.mobil_smarthet.constants.Sensors;

/**
 * Created by elias on 2016-02-28.
 */
public class FavourizeEvent {

    private Sensors sensor;

    public FavourizeEvent(Sensors sensor) {
        this.sensor = sensor;
    }

    public Sensors getSensor() {
        return sensor;
    }
}
