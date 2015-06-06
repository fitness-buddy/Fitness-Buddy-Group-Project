package com.strengthcoach.strengthcoach.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

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

    public Trainer getTrainer() {
        return (Trainer) get("trainer");
    }

    public ParseUser getUser() {
        return (ParseUser) get("user");
    }

    public String getText() {
        return getString("text");
    }

    // Modifiers
    public void setId(int id) {
        put("id", id);
    }

    public void setTrainer(Trainer trainer) {
        put("trainer", trainer);
    }

    public void setUser(ParseUser user) {
        put("user", user);
    }

    public void setText(String text) {
        put("text", text);
    }
}
