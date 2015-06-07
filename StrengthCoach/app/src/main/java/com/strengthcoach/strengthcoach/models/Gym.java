package com.strengthcoach.strengthcoach.models;

import com.parse.ParseClassName;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;

import java.util.ArrayList;

@ParseClassName("Gym")
public class Gym extends ParseObject {
    /**
     * COLUMN NAMES:
     * int id;
     * String name;
     * ArrayList<Trainer> trainers
     * Address address;
     * ParseGeoPoint location;
     */

    // Accessors
    public int getId() {
        return getInt("id");
    }

    public String getName() {
        return getString("name");
    }

    public ArrayList<Trainer> getTrainers() {
        return (ArrayList<Trainer>) get("trainers");
    }

    public Address getAddress() {
        return (Address) get("address");
    }

    public ParseGeoPoint point() { return getParseGeoPoint("location"); }

    // Modifiers
    public void setId(int id) {
        put("id", id);
    }

    public void setName(String name) {
        put("name", name);
    }

    // Sets up a relation one-one relation between Address & Gym
    public void setAddress(Address address) {
        put("address", address);
    }

    public void setLocation(Double latitude, Double longitude) {
        ParseGeoPoint point = new ParseGeoPoint(latitude, longitude);
        put("location", point);
    }

    public void setTrainers(ArrayList<Trainer> trainers) {
        put("trainers", trainers);
    }
}
