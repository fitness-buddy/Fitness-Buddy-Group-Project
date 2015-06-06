package com.strengthcoach.strengthcoach.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("Review")
public class Review extends ParseObject {

    /**
     * COLUMN NAMES:
     * String body          // Represents the text in a review
     * int rating           // Int should work fine; if required we can change to Double later to represent 0.5 (half stars)
     * String reviewer      // ObjectId of user that composed the review
     * String reviewee      // ObjectId of the trainer from whom the review was composed
     */
    // Accessors
    public String getReviewBody() {
        return getString("body");
    }

    public int getRating() {
        return getInt("rating");
    }

    public String getReviewer() {
        return getString("reviewer");
    }

    public String getReviewee() {
        return getString("reviewee");
    }

    // Modifiers
    public void setReviewText(String body) {
        put("body", body);
    }

    public void setRating(int rating) {
        put("rating", rating);
    }

    public void setReviewer(String reviewer) {
        put("reviewer", reviewer);
    }

    public void setReviewee(String reviewee) {
        put("reviewee", reviewee);
    }
}
