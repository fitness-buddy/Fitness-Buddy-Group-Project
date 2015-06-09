package com.strengthcoach.strengthcoach.Models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

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

    // Formats phone numbers like 555-555-5555 => 5555555555
    public static String formatString(String string) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < string.length(); i++) {
            char c = string.charAt(i);
            if (Character.isDigit(c)) {
                builder.append(c);
            }
        }
        return builder.toString();
    }
}
