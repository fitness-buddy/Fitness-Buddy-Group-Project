package com.strengthcoach.strengthcoach.helpers;

import android.util.Log;

import com.parse.ParseException;
import com.parse.SaveCallback;
import com.strengthcoach.strengthcoach.Models.Address;
import com.strengthcoach.strengthcoach.Models.Gym;
import com.strengthcoach.strengthcoach.Models.Review;
import com.strengthcoach.strengthcoach.Models.SimpleUser;
import com.strengthcoach.strengthcoach.Models.Trainer;

import java.util.ArrayList;

public class DataLoader {
    Address address;
    Gym gym;
    Trainer trainer;
    SimpleUser user;

    public void populate() {
        user = new SimpleUser();
        user.setPhoneNumber("555-555-5555");
        user.setName("Mickey Mouse");
        user.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    // Saved successfully.
                    Log.d("DEBUG", "User update saved!");
                    String id = user.getObjectId();
                    Log.d("DEBUG", "The object id is: " + id);
                    instantiateAddress();
                    instantiateTrainer();
                    instantiateGym();
                } else {
                    // The save failed.
                    Log.d("DEBUG", "User update error: " + e);
                }
            }
        });
    }

    private void instantiateTrainer() {
        String profileImageUrl = "http://img05.deviantart.net/8d7f/i/2013/149/c/b/south_park_action_poses___kenny_18_by_megasupermoon-d670y5f.jpg";
        String image1 = "https://developer.cdn.mozilla.net/media/uploads/demos/d/a/daniel.moura/5518edae24034cecedeb89bf3c1db5c2/1370528531_screenshot_1.png";
        String image2 = "http://imgur.com/gallery/xfzQez6";
        trainer = new Trainer();
        trainer.setName("Mike Chang");
        trainer.setAboutMe("I am the best!!");
        trainer.setPhoneNumber("222-222-2222");
        ArrayList<SimpleUser> clients = new ArrayList<>();
        clients.add(user);
        trainer.setClients(clients);
        trainer.setRating(5);
        trainer.setProfileImageUrl(profileImageUrl);
        ArrayList<String> images = new ArrayList<>();
        images.add(image1);
        images.add(image2);
        trainer.setImages(images);
    }

    private void instantiateGym() {
        gym = new Gym();
        gym.setName("24 Hour Fitness");
        gym.setAddress(address);
        gym.setLocation(37.404324, -122.108046);
        ArrayList<Trainer> trainers = new ArrayList<>();
        trainers.add(trainer);
        gym.setTrainers(trainers);
        gym.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    e.printStackTrace();
                } else {
                    instantiateReview();
                }
            }
        });
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

    private void instantiateReview() {
        Review review = new Review();
        review.setReviewer(user.getObjectId());
        review.setReviewee(trainer.getObjectId());
        review.setRating(4);
        review.setReviewText("I had a great time with Mike  --Mickey");
        review.saveInBackground();
    }
}
