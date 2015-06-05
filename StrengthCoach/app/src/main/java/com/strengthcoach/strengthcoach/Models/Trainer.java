package com.strengthcoach.strengthcoach.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

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
     */

    // Accessors
    public int getId() {
        return getInt("id");
    }

    public String getName() {
        return getString("name");
    }

    public ArrayList<ParseUser> getClients() {
        return (ArrayList<ParseUser>) get("clients");
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

    // Modifiers
    public void setId(int id) {
        put("id", id);
    }

    public void setName(String name) {
        put("name", name);
    }

    public void setClients(ArrayList<ParseUser> clients) {
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
}
