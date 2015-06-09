package com.strengthcoach.strengthcoach.activities;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseObject;
import com.strengthcoach.strengthcoach.models.Address;
import com.strengthcoach.strengthcoach.models.Gym;
import com.strengthcoach.strengthcoach.models.Message;
import com.strengthcoach.strengthcoach.models.Review;
import com.strengthcoach.strengthcoach.models.SimpleUser;
import com.strengthcoach.strengthcoach.models.Trainer;
import com.strengthcoach.strengthcoach.models.TrainerSlots;

public class StrengthCoachApplication extends Application {
    public static final String APPLICATION_ID = "7eAbPO86MugTvAXhQjBz3ctRoO1LwWeqoL2hDX6V";
    public static final String CLIENT_KEY = "RtO4IgNoZBnS7ezw27fnvj05KCscflrNsFGXP89E";
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
    }
}