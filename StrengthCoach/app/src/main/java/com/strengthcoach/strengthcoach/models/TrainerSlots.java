package com.strengthcoach.strengthcoach.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

/**
 * Created by Neeraja on 6/6/2015.
 */
@ParseClassName("TrainerSlots")
public class TrainerSlots extends ParseObject {
    /**
     * COLUMN NAMES:
     * String day          // Represents the day trainer is available
     * String start_time   // Start time of the slot
     * String end_time      // End time of the slot
     * String trainer_id      // ObjectId of the trainer
     * String status        // Availability of the slot
     * String booked_by     // ObjectId of teh user who booked the slot of the user
     */

// Accessors
    public String getDay() {
        return getString("day");
    }

    public String getStartTime() {
        return getString("start_time");
    }

    public String getEndTime() {
        return getString("end_time");
    }

    public String getTrainerId() {
        return getString("trainer_id");
    }

    public String getStatus() {
        return getString("status");
    }

    public String getBookedBy() {
        return getString("booked_by");
    }

    // Modifiers
    public void setDay(String day) {
        put("day", day);
    }

    public void setStartTime(String startTime) {
        put("start_time", startTime);
    }

    public void setEndTime(String endTime) {
        put("end_time", endTime);
    }

    public void setStatus (String status) {
        put("status", status);
    }

    public void setBookedBy (String bookedBy) {
        put("booked_by", bookedBy);
    }

    public void setTrainerId (ParseObject trainerId){put("trainer_id", trainerId);}

}
