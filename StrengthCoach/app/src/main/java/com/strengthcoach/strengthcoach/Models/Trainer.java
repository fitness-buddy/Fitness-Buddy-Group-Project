package com.strengthcoach.strengthcoach.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import java.util.ArrayList;

@ParseClassName("Trainer")
public class Trainer extends ParseObject {
    /**
     * COLUMN NAMES: Multi-word names should be separated by '_' (underscore)
     * int id
     * String name
     * ArrayList<User> clients
     * String about_me
     * String phone_number
     * int rating
     * String profile_image         // Url of the profile image
     * ArrayList<String> images     // Collection of image url's
     * ArrayList<Review> reviews
     */

    // Accessors
    public int getId() {
        return getInt("id");
    }

    public String getName() {
        return getString("name");
    }

    public ArrayList<SimpleUser> getClients() {
        return (ArrayList<SimpleUser>) get("clients");
    }

    public String getAboutMe() {
        return getString("about_me");
    }

    public int getPrice() {
        return getInt("price");
    }

    // eg. $49
    public String getPriceFormatted() {
        return "$" +  getInt("price");
    }

    public String getPhoneNumber() {
        return getString("phone_number");
    }

    public int getRatings() {
        return getInt("rating");
    }

    public ArrayList<Review> getReviews() {
        return (ArrayList<Review>) get("reviews");
    }

    public String getProfileImageUrl() {
        return getString("profile_image");
    }

    public ArrayList<String> getImages() {
        return (ArrayList<String>) get("images");
    }

    // Modifiers
    public void setId(int id) {
        put("id", id);
    }

    public void setName(String name) {
        put("name", name);
    }

    public void setClients(ArrayList<SimpleUser> clients) {
        put("clients", clients);
    }

    public void setAboutMe(String aboutMe) {
        put("about_me", aboutMe);
    }

    public void setPrice(int price) {
        put("price", price);
    }

    public void setPhoneNumber(String phoneNumber) {
        put("phone_number", phoneNumber);
    }

    public void setRating(int rating) {
        put("rating", rating);
    }

    public void setReviews(ArrayList<Review> reviews) {
       put("reviews", reviews);
    }

    public void setProfileImageUrl(String url) {
        put("profile_image", url);
    }

    public void setImages(ArrayList<String> imageUrls) {
        put("images", imageUrls);
    }

    public void addImage(String imageUrl) {
        add("images", imageUrl);
    }

}
