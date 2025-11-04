package com.example.prm392_evcare.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ServiceTypesResponse {
    @SerializedName("success")
    private boolean success;
    
    @SerializedName("message")
    private String message;
    
    @SerializedName("data")
    private List<ServiceType> data;

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public List<ServiceType> getData() {
        return data;
    }
    
    // Helper method for backward compatibility
    public List<ServiceType> getServiceTypes() {
        return data;
    }
}
