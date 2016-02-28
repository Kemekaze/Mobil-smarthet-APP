package dat065.mobil_smarthet.constants;

/**
 * Created by elias on 2016-02-27.
 */
public enum Presets {
    FAVOURITES(0,"Favourites", new Sensors[]{}, new double[]{}),
    WORK(1,"Work", new Sensors[]{Sensors.CO2,Sensors.AUDIO}, new double[]{}),
    SLEEP(2,"Sleep", new Sensors[]{Sensors.LIGHT,Sensors.AUDIO}, new double[]{});

    private int id;
    private final String key;
    private Sensors[] sensors;
    private double[] limits;

    private Presets(int id, String key, Sensors[] sensors, double limits[]) {
        this.id = id;
        this.key = key;
        this.sensors = sensors;
        this.limits = limits;
    }

    public int getId() {
        return id;
    }

    public double[] getLimits() {
        return limits;
    }

    public Sensors[] getSensors() {
        return sensors;
    }

    public String getKey() {
        return key;
    }

    public static Presets match(String preset) {
        for (Presets s : Presets.values())
            if (s.getKey().equals(preset)) return s;
        return null;
    }
    public static Presets match(int id){
        for (Presets s : Presets.values())
            if (s.getId() == id) return s;
        return null;
    }

}

