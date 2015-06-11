package com.strengthcoach.strengthcoach.activities;

import android.app.Application;
import android.util.Log;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.SaveCallback;
import com.strengthcoach.strengthcoach.helpers.Constants;
import com.strengthcoach.strengthcoach.models.Address;
import com.strengthcoach.strengthcoach.models.Gym;
import com.strengthcoach.strengthcoach.models.Message;
import com.strengthcoach.strengthcoach.models.Review;
import com.strengthcoach.strengthcoach.models.SimpleUser;
import com.strengthcoach.strengthcoach.models.Trainer;
import com.strengthcoach.strengthcoach.models.TrainerSlots;

public class StrengthCoachApplication extends Application {
    public static final String APPLICATION_ID = Constants.PARSE_APPLICATION_KEY;
    public static final String CLIENT_KEY = Constants.PARSE_CLIENT_KEY;
    @Override
    public void onCreate() {
        super.onCreate();
        // Register your parse models here
        ParseObject.registerSubclass(Address.class);
        ParseObject.registerSubclass(Gym.class);
        ParseObject.registerSubclass(Message.class);
        ParseObject.registerSubclass(SimpleUser.class);
        ParseObject.registerSubclass(Trainer.class);
        ParseObject.registerSubclass(Review.class);
        ParseObject.registerSubclass(TrainerSlots.class);
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, APPLICATION_ID, CLIENT_KEY);

        ParsePush.subscribeInBackground("", new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.d("com.parse.push", "successfully subscribed to the broadcast channel.");
                } else {
                    Log.e("com.parse.push", "failed to subscribe for push", e);
                }
            }
        });
    }
}