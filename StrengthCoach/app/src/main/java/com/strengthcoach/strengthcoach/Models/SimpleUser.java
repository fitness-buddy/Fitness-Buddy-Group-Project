package com.strengthcoach.strengthcoach.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

/**
 * Created by Neeraja on 6/5/2015.
 */

@ParseClassName("SimpleUser")
public class SimpleUser extends ParseObject {

    int id;
    String name;
    String phone_no;

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
