package com.example.prm392_evcare.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class VehicleModelsResponse {
    @SerializedName("success")
    private boolean success;
    
    @SerializedName("message")
    private String message;
    
    @SerializedName("vehicleModels")
    private List<VehicleModel> vehicleModels;

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public List<VehicleModel> getVehicleModels() {
        return vehicleModels;
    }
}
