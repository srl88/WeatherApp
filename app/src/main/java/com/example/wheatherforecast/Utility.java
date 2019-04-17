package com.example.wheatherforecast;

/**
 * Static class for util methods
 */
public class Utility {

    /**
     * Converts from F to C. Requires a Double to avoid errors
     * @param F
     * @return
     */
    public static int fromFahrenheitToCelsious(Double F){
        Double init = (F - 32.0);
        Double constant = 0.55555555;
        return (int)(init*constant);
    }

    /**
     * Converts from C to F. Requires a Double for precission.
     * @param C
     * @return
     */
    public static int fromCelsiousToFahrenheit(Double C){
        Double constant = 9.0/5.0;
        Double init = constant * C;
        return  (int)(init + 32);
    }
}
