package dat065.mobil_smarthet.event;

import dat065.mobil_smarthet.constants.Sensors;

/**
 * Created by elias on 2016-03-07.
 */
public class UpdateGraphEvent {
    Sensors sensors;
    int graphType;
    long time;
    boolean hasTime;

    public UpdateGraphEvent(int graphType, Sensors sensors, long time) {
        this.graphType = graphType;
        this.sensors = sensors;
        this.time = time;
        hasTime = true;
    }

    public UpdateGraphEvent(int graphType, Sensors sensors) {
        this.graphType = graphType;
        this.sensors = sensors;
        hasTime = false;
    }

    public boolean hasTime() {
        return hasTime;
    }

    public int getGraphType() {
        return graphType;
    }

    public Sensors getSensors() {
        return sensors;
    }

    public long getTime() {
        return time;
    }
}
