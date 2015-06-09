package com.strengthcoach.strengthcoach.helpers;

import android.app.PendingIntent;
import android.content.Intent;
import android.telephony.SmsManager;
import android.util.Log;

import com.parse.FindCallback;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.strengthcoach.strengthcoach.Models.Trainer;
import com.strengthcoach.strengthcoach.Models.TrainerSlots;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Neeraja on 6/6/2015.
 */
public class Utils {
    static ArrayList<String> availableSlots = new ArrayList<String>();
    static ArrayList alDays;
    // Generate Random Code
    public static String generateRandomCode() {
        //generate a 4 digit integer
        int randomCode = (int) (Math.random() * 9000) + 1000;
        Log.v("Login", "generated code " + String.valueOf(randomCode));
        return String.valueOf(randomCode);
    }
}

