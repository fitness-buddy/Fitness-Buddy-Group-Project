package com.strengthcoach.strengthcoach.models;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Hack to pass Trainer quickly across activities
 */
public class LocalTrainer implements Serializable {
    String id;
    String name;
    String aboutMe;
    String price;
    double rating;
    String profileImageUrl;
    ArrayList<String> images;
    ArrayList<String> educationAndCertifications;
    ArrayList<String> interestsAndAchievements;
    boolean isFavorite;

    public LocalTrainer(Trainer trainer) {
        this.id = trainer.getObjectId();
        this.name = trainer.getName();
        this.aboutMe = trainer.getAboutMe();
        this.isFavorite = trainer.isFavorite();
        this.price = trainer.getPriceFormatted();
        this.rating = trainer.getRatings();
        this.profileImageUrl = trainer.getProfileImageUrl();
        this.images = trainer.getImages();
        this.educationAndCertifications = trainer.getEducationAndCertifications();
        this.interestsAndAchievements = trainer.getInterestsAndAchievements();
    }

    // Accessors
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAboutMe() {
        return aboutMe;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public String getPriceFormatted() {
        return price;
    }

    public double getRatings() {
        return rating;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public ArrayList<String> getImages() {
        return (ArrayList<String>) images;
    }

    public ArrayList<String> getEducationAndCertifications() {
        return (ArrayList<String>) educationAndCertifications;
    }

    public ArrayList<String> getInterestsAndAchievements() {
        return (ArrayList<String>) interestsAndAchievements;
    }
}
