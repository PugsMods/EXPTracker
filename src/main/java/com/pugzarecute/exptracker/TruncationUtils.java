package com.pugzarecute.exptracker;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class TruncationUtils {
    public static String floatTruncate(float input){
        if (input < 1000) return String.valueOf(round(input));
        else if(input/1000 < 1000)return round(input / 1000) + "K";
        else if (input/10000 < 1000) return round(input / 10000) + "M";
        return String.valueOf(round(input));
    }
    private static float round(Float input){
        BigDecimal bigDecimal = new BigDecimal(input);
        return bigDecimal.setScale(2, RoundingMode.HALF_EVEN).floatValue();
    }
}
