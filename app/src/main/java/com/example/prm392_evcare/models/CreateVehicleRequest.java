package com.example.prm392_evcare.models;

import com.google.gson.annotations.SerializedName;

public class CreateVehicleRequest {
    @SerializedName("vehicleModelId")
    private String vehicleModelId;
    
    @SerializedName("licensePlate")
    private String licensePlate;
    
    @SerializedName("color")
    private String color;
    
    @SerializedName("year")
    private int year;
    
    @SerializedName("mileage")
    private int mileage;

    // Constructor without vinNumber (matching API)
    public CreateVehicleRequest(String vehicleModelId, String licensePlate, String color, 
                                int year, int mileage) {
        this.vehicleModelId = vehicleModelId;
        this.licensePlate = licensePlate;
        this.color = color;
        this.year = year;
        this.mileage = mileage;
    }

    // Getters and Setters
    public String getVehicleModelId() {
        return vehicleModelId;
    }

    public void setVehicleModelId(String vehicleModelId) {
        this.vehicleModelId = vehicleModelId;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMileage() {
        return mileage;
    }

    public void setMileage(int mileage) {
        this.mileage = mileage;
    }
}
