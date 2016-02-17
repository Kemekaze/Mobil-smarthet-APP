package dat065.mobil_smarthet.constants;

public enum Sensors{
    TEMPERATURE(1,"Temperature"),
    LIGHT(2,"Light"),
    AUDIO(3,"Audio"),
    CO2(4,"C02"),
    MOTION(5,"Motion");

    private final int id;
    private final String name;

    private Sensors(int id, String name){
        this.id   = id;
        this.name = name;
    }
    public String getName(){
        return name;
    }
    public int getId(){
        return id;
    }
    public static Sensors match(int id){
        for(Sensors s : Sensors.values())
            if(s.getId() == id) return s;
        return null;
    }
}
