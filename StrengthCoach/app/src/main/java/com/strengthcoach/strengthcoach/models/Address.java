package com.strengthcoach.strengthcoach.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("Address")
public class Address extends ParseObject {
    // Column names; Not used anywhere, they are kept here for quick reference
    int id;
    String addressLine1;
    String addressLine2;
    String city;
    String state;
    String zip;

    // Accessors
    public int getId() {
        return getInt("id");
    }

    public String getAddressLine1() {
        return getString("addressLine1");
    }

    public String getAddressLine2() {
        return getString("addressLine2");
    }

    public String getCity() {
        return getString("city");
    }

    public String getState() {
        return getString("state");
    }

    public String getZip() {
        return getString("zip");
    }

    public String toString() {
        String address = "";
        address += getAddressLine1() + ", ";
        if (!getAddressLine2().equals("")) {
            address += getAddressLine2() + ", ";
        }

        address += getCity() + ", " + getState() + " " + getZip();
        return address;
    }

    // Modifiers
    public void setAddressLine1(String addressLine1) {
        put("addressLine1", addressLine1);
    }

    public void setAddressLine2(String addressLine2) {
        put("addressLine2", addressLine2);
    }

    public void setCity(String city) {
        put("city", city);
    }

    public void setState(String state) {
        put("state", state);
    }

    public void setZip(String zip) {
        put("zip", zip);
    }
}
