package com.example.prm392_evcare.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class VehicleResponse {
    @SerializedName("success")
    private boolean success;
    
    @SerializedName("message")
    private String message;
    
    @SerializedName("data")
    private List<Vehicle> data;  // API returns array directly, not nested object

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public List<Vehicle> getData() {
        return data;
    }

    // Helper method for backward compatibility
    public List<Vehicle> getVehicles() {
        return data;
    }

    // For single vehicle response (create/update)
    public Vehicle getVehicle() {
        return (data != null && !data.isEmpty()) ? data.get(0) : null;
    }
}
