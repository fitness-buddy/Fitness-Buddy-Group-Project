package com.strengthcoach.strengthcoach.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import java.util.Date;

@ParseClassName("Review")
public class Review extends ParseObject {

    /**
     * COLUMN NAMES:
     * String body          // Represents the text in a review
     * int rating           // Int should work fine; if required we can change to Double later to represent 0.5 (half stars)
     * SimpleUser reviewer      // ObjectId of user that composed the review
     * Trainer reviewee      // ObjectId of the trainer from whom the review was composed
     */
    // Accessors
    public String getReviewBody() {
        return getString("body");
    }

    public int getRating() {
        return getInt("rating");
    }

    public SimpleUser getReviewer() {
        return (SimpleUser) getParseObject("reviewer");
    }

    public Trainer getReviewee() {
        return (Trainer) getParseObject("reviewee");
    }

    // Modifiers
    public void setReviewText(String body) {
        put("body", body);
    }

    public void setRating(int rating) {
        put("rating", rating);
    }

    public void setReviewer(SimpleUser reviewer) {
        put("reviewer", reviewer);
    }

    public void setReviewee(Trainer reviewee) {
        put("reviewee", reviewee);
    }

    public void setDate(Date date) {
        put("date", date);
    }
}
