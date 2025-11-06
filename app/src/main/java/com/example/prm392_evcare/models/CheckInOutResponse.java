package com.example.prm392_evcare.models;

import com.google.gson.annotations.SerializedName;

public class CheckInOutResponse {
    @SerializedName("success")
    private boolean success;
    
    @SerializedName("message")
    private String message;
    
    @SerializedName("data")
    private TechnicianScheduleResponse.ScheduleData data;
    
    public boolean isSuccess() {
        return success;
    }
    
    public String getMessage() {
        return message;
    }
    
    public TechnicianScheduleResponse.ScheduleData getData() {
        return data;
    }
}

