package com.strengthcoach.strengthcoach.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("Trainer")
public class Trainer extends ParseObject {
    int id;
    String name;
    Gym gym;
    String aboutMe;
    int price;
    String phoneNumber;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Gym getGym() {
        return gym;
    }

    public String getAboutMe() {
        return aboutMe;
    }

    public int getPrice() {
        return price;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setGym(Gym gym) {
        this.gym = gym;
    }

    public void setAboutMe(String aboutMe) {
        this.aboutMe = aboutMe;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
