package com.strengthcoach.strengthcoach.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("Message")
public class Message extends ParseObject {
    int id;
    Trainer trainer;
    User user;
    String text;

    public int getId() {
        return id;
    }

    public Trainer getTrainer() {
        return trainer;
    }

    public User getUser() {
        return user;
    }

    public String getText() {
        return text;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTrainer(Trainer trainer) {
        this.trainer = trainer;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setText(String text) {
        this.text = text;
    }
}
