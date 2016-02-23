package dat065.mobil_smarthet.constants;

public enum Sensors{
    TEMPERATURE(1,"Temperature","\\u2103"),// Symobl för celsius
    LIGHT(2,"Light","lx"),
    AUDIO(3,"Audio","dB"),
    CO2(4,"C02","ppm"),
    MOTION(5,"Motion","N");

    private final int id;
    private final String name;
    private final String symbol;

    private Sensors(int id, String name, String symbol){
        this.id   = id;
        this.name = name;
        this.symbol = symbol;
    }
    public int getId(){
        return id;
    }
    public String getName(){
        return name;
    }
    public String getSymbol(){
        return symbol;
    }


    public static Sensors match(int id){
        for(Sensors s : Sensors.values())
            if(s.getId() == id) return s;
        return null;
    }
    public static Sensors match(String sensor){
        for(Sensors s : Sensors.values())
            if(s.equals(sensor)) return s;
        return null;
    }
}
