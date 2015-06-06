package com.strengthcoach.strengthcoach.helpers;

import android.util.Log;

/**
 * Created by Neeraja on 6/6/2015.
 */
public class Utils {

    // Generate Random Code
    public static String generateRandomCode() {
        //generate a 4 digit integer
        int randomCode = (int) (Math.random() * 9000) + 1000;
        Log.v("Login", "generated code " + String.valueOf(randomCode));
        return String.valueOf(randomCode);
    }

}
