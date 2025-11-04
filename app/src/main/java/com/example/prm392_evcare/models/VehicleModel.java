package com.example.prm392_evcare.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.Map;

public class VehicleModel implements Serializable {
    @SerializedName("_id")
    private String id;
    
    @SerializedName("brand")
    private String brand;
    
    @SerializedName("modelName")
    private String modelName;
    
    @SerializedName("yearFrom")
    private int yearFrom;
    
    @SerializedName("yearTo")
    private int yearTo;
    
    @SerializedName("batteryType")
    private String batteryType;
    
    @SerializedName("batteryCapacity")
    private Double batteryCapacity;
    
    @SerializedName("maintenanceIntervals")
    private Map<String, String> maintenanceIntervals;

    // Getters
    public String getId() {
        return id;
    }

    public String getBrand() {
        return brand;
    }

    public String getModelName() {
        return modelName;
    }

    public int getYearFrom() {
        return yearFrom;
    }

    public int getYearTo() {
        return yearTo;
    }

    public String getBatteryType() {
        return batteryType;
    }

    public Double getBatteryCapacity() {
        return batteryCapacity;
    }

    public Map<String, String> getMaintenanceIntervals() {
        return maintenanceIntervals;
    }

    // Setters
    public void setId(String id) {
        this.id = id;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public void setYearFrom(int yearFrom) {
        this.yearFrom = yearFrom;
    }

    public void setYearTo(int yearTo) {
        this.yearTo = yearTo;
    }

    public void setBatteryType(String batteryType) {
        this.batteryType = batteryType;
    }

    public void setBatteryCapacity(Double batteryCapacity) {
        this.batteryCapacity = batteryCapacity;
    }

    public void setMaintenanceIntervals(Map<String, String> maintenanceIntervals) {
        this.maintenanceIntervals = maintenanceIntervals;
    }
    
    // Helper method to display full name
    public String getFullName() {
        return brand + " " + modelName + " (" + yearFrom + "-" + yearTo + ")";
    }

    // Helper method to get name (brand + model)
    public String getName() {
        return brand + " " + modelName;
    }
}
