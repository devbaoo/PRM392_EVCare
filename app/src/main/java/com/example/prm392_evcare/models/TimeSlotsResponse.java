package com.example.prm392_evcare.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class TimeSlotsResponse {
    @SerializedName("success")
    private boolean success;
    
    @SerializedName("message")
    private String message;
    
    @SerializedName("data")
    private Data data;

    public static class Data {
        @SerializedName("availableSlots")
        private List<TimeSlot> availableSlots;

        public List<TimeSlot> getAvailableSlots() {
            return availableSlots;
        }
    }

    public static class TimeSlot {
        @SerializedName("startTime")
        private String startTime;
        
        @SerializedName("endTime")
        private String endTime;
        
        @SerializedName("isAvailable")
        private boolean isAvailable;
        
        @SerializedName("maxCapacity")
        private int maxCapacity;
        
        @SerializedName("currentBookings")
        private int currentBookings;

        public String getStartTime() { return startTime; }
        public String getEndTime() { return endTime; }
        public boolean isAvailable() { return isAvailable; }
        public int getMaxCapacity() { return maxCapacity; }
        public int getCurrentBookings() { return currentBookings; }
    }

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public Data getData() { return data; }
    
    public List<TimeSlot> getAvailableSlots() {
        return data != null ? data.getAvailableSlots() : null;
    }
}
