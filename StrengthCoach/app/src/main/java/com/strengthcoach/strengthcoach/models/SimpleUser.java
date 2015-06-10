package com.strengthcoach.strengthcoach.models;

import android.util.Log;
import android.widget.ArrayAdapter;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.strengthcoach.strengthcoach.activities.BlockSlotActivity;
import com.strengthcoach.strengthcoach.helpers.Constants;

import java.util.Arrays;
import java.util.List;

@ParseClassName("SimpleUser")
public class SimpleUser extends ParseObject {
static String obj;

    /* Column Names
            int objectId;
            String name;
            String phone_no;
            String token_id;
            String last4;
            String card_type;
            String exp_date;*/

    public static String currentUserObjectId;
    // Accessors

    public String getName() {
        return getString("name");
    }

    public String getId() {
        return getString("objectId");
    }

    public String getPhoneNumber() {
        return getString("phone_number");
    }

    public String getTokenId() {
        return getString("token_id");
    }

    public String getLast4() {
        return getString("last4");
    }

    public String getCardType() {
        return getString("card_type");
    }

    public String getExpDate() {
        return getString("exp_date");
    }


    // Modifiers
    public void setName(String name) {
        put("name", name);
    }

    public void setPhoneNumber(String phoneNumber) {
        put("phone_number", phoneNumber);
    }

    public void setId(String object) {
        put("objectId", object);
    }

    public void setTokenId(String tokenId) {
        put("token_id", tokenId);
    }

    public void setLast4(String last4) {
        put("last4", last4);
    }

    public void setCardType(String cardType) {
        put("card_type", cardType);
    }

    public void setExpDate(String expDate) {
        put("exp_date", expDate);
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

