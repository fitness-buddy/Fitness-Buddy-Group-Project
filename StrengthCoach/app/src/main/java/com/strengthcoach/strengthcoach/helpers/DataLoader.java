package com.strengthcoach.strengthcoach.helpers;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;
import com.strengthcoach.strengthcoach.models.Address;
import com.strengthcoach.strengthcoach.models.Gym;
import com.strengthcoach.strengthcoach.models.Trainer;

import java.util.ArrayList;

public class DataLoader {
    Address address;
    Gym gym;
    Trainer trainer;
    ParseUser user;
    public void populate() {
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser == null) {
            // Create a new user and signup
            instantiateUser();
        } else {
            user = currentUser;
        }
        instantiateTrainer();
        instantiateAddress();
        instantiateGym();
//        instantiateMessage();
    }

    private void instantiateUser() {
        user = new ParseUser();
        user.setUsername("donaldDuck");
        user.setPassword("QuackQuack");
        user.setEmail("donaldduck@gmail.com");
        user.put("phone_number", "444-444-4444");
        user.put("name", "Donald Duck");

        // You cannot this object without doing a signup first
        user.signUpInBackground(new SignUpCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    // Hooray! Let them use the app now.
                } else {
                    // Sign up didn't succeed. Look at the ParseException
                    // to figure out what went wrong
                }
            }
        });
    }

    private void instantiateTrainer() {
        trainer = new Trainer();
        trainer.setName("Mike Chang");
        trainer.setAboutMe("I am the best!!");
        trainer.setPhoneNumber("222-222-2222");
        ArrayList<ParseUser> clients = new ArrayList<>();
        clients.add(user);
        trainer.setClients(clients);
    }

    private void instantiateGym() {
        gym = new Gym();
        gym.setName("24 Hour Fitness");
        gym.setAddress(address);
        gym.setLocation(37.404324, -122.108046);
        ArrayList<Trainer> trainers = new ArrayList<>();
        trainers.add(trainer);
        gym.setTrainers(trainers);
        gym.saveInBackground();
    }

    // Creates an address object and saves in Parse cloud
    private void instantiateAddress() {
        address = new Address();
        address.setAddressLine1("2550 W El Camino Real");
        address.setAddressLine2("");
        address.setCity("Mountain View");
        address.setState("CA");
        address.setZip("94040");
    }
}
