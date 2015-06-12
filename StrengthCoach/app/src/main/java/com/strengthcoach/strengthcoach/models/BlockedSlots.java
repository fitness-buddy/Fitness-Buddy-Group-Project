package com.strengthcoach.strengthcoach.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

/**
 * Created by Neeraja on 6/12/2015.
 */
@ParseClassName("BlockedSlots")
public class BlockedSlots extends ParseObject {
    /**
     * COLUMN NAMES:
     * String slot_date          // Already Booked Slot/ Booking in progress Date
     * String slot_time   // Already Booked time of a day/ Booking in progress Time slot
     * String trainer_id      // ObjectId of the trainer
     * String user_id     //ObjectId of the user who booked the slot of the user
     * String status // status of the booking process stage
     */

// Accessors
    public String getTrainerId() {
        return getString("trainer_id");
    }

    public String getSlotDate() {
        return getString("slot_date");
    }

    public String getBookedByUserId() {return getString("user_id");}

    public String getSlotTime() {
        return getString("slot_time");
    }

    public String getStatus() {
        return getString("status");
    }


    // Modifiers
    public void setTrainerId(ParseObject trainerId) {
        put("trainer_id", trainerId);
    }

    public void setSlotDate(String slotDate) {
        put("slot_date", slotDate);
    }

    public void setBookedByUserId (ParseObject userId) {
        put("user_id", userId);
    }

    public void setSlotTime (String slotTime) {
        put("slot_time", slotTime);
    }

    public void setStatus (String status) {
        put("status", status);
    }
}
