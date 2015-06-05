package com.strengthcoach.strengthcoach.models;

import com.parse.ParseClassName;
import com.parse.ParseUser;

// This class extends the ParseUser and adds custom fields to _User table

@ParseClassName("CurrentUser")
public class CurrentUser extends ParseUser {
    /**
     * COLUMN NAMES:
     * String name;
     * String phone_number;
     */

    // Accessors
    public String getName() {
        return getString("name");
    }

    public String getPhoneNumber() {
        return getString("phone_number");
    }

    // Modifiers
    public void setName(String name) {
        put("name", name);
    }

    public void setPhoneNumber(String phoneNumber) {
        put("phone_number", phoneNumber);
    }
}
