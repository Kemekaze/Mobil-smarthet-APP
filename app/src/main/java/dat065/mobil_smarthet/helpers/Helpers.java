package dat065.mobil_smarthet.helpers;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Created by elias on 2016-02-23.
 */
public class Helpers {
    public static Double round(double value, int places){
        if (places < 0) throw new IllegalArgumentException();
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
    public static String co2Levels(double value){
        if(value< 450)
            return "Outdoor level";
        else if(value < 600)
            return "Acceptable";
        else if(value < 1000)
            return "Complaints of stiffness and odors";
        else if(value < 2500)
            return "general drowsiness";
        else if(value < 5000)
            return "Maximun allowed cocentration for 8 hour work period";
        //Extreme and Dangerous CO2 Levels
        else if(value < 30000)
            return "slightly intoxicating, breathing and pulse rate increase, nausea";
        else if(value < 50000)
            return "Above plus headaches and sight impairment";
        else
            return "Unconscious, further exposure death";

    }
}
