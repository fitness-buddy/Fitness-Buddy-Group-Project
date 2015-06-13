package com.strengthcoach.strengthcoach.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("Message")
public class Message extends ParseObject {
    /**
     * COLUMN NAMES:
     * int id;
     * Trainer trainer;
     * SimpleUser user;
     * String text;
     */

    // Accessors
    public int getId() {
        return getInt("id");
    }

    public String getText() {
        return getString("text");
    }

    public String getFromObjectId() {
        return getString("fromObjectId");
    }

    public String getToObjectId() {
        return getString("toObjectId");
    }

    // Modifiers
    public void setId(int id) { put("id", id); }

    public void setText(String text) {
        put("text", text);
    }

    public void setFromObjectId(String objectId) {
        put("fromObjectId", objectId);
    }

    public void setToObjectId(String objectId) {
        put("toObjectId", objectId);
    }
}
