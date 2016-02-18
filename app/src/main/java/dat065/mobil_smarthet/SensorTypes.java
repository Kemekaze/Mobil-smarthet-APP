package dat065.mobil_smarthet;

/**
 * Created by backevik on 16-02-11.
 */
public enum SensorTypes {
    TEMPERATURE() {
        @Override
        public String toString() {
            return "Celsius";
        }
    },
    ACCELEROMETER {
        @Override
        public String toString() {
            return "Movement";
        }
    },
    LIGHT {
        @Override
        public String toString() {
            return "Lux";
        }
    },
    SOUND{
        @Override
    public String toString(){
            return "Decibel";
        }
    }
}