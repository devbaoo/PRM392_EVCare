package com.example.prm392_evcare.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Vehicle implements Serializable {
    @SerializedName("_id")
    private String id;
    
    @SerializedName("owner")
    private String owner;
    
    @SerializedName("vehicleInfo")
    private VehicleInfo vehicleInfo;
    
    @SerializedName("currentStatus")
    private CurrentStatus currentStatus;
    
    @SerializedName("status")
    private String status;
    
    @SerializedName("createdAt")
    private String createdAt;
    
    @SerializedName("updatedAt")
    private String updatedAt;

    // Inner class for VehicleInfo
    public static class VehicleInfo implements Serializable {
        @SerializedName("vehicleModel")
        private VehicleModel vehicleModel;
        
        @SerializedName("year")
        private int year;
        
        @SerializedName("color")
        private String color;
        
        @SerializedName("licensePlate")
        private String licensePlate;
        
        @SerializedName("brand")
        private String brand;
        
        @SerializedName("modelName")
        private String modelName;

        public VehicleModel getVehicleModel() {
            return vehicleModel;
        }

        public int getYear() {
            return year;
        }

        public String getColor() {
            return color;
        }

        public String getLicensePlate() {
            return licensePlate;
        }

        public String getBrand() {
            return brand;
        }

        public String getModelName() {
            return modelName;
        }

        public void setVehicleModel(VehicleModel vehicleModel) {
            this.vehicleModel = vehicleModel;
        }

        public void setYear(int year) {
            this.year = year;
        }

        public void setColor(String color) {
            this.color = color;
        }

        public void setLicensePlate(String licensePlate) {
            this.licensePlate = licensePlate;
        }
    }

    // Inner class for CurrentStatus
    public static class CurrentStatus implements Serializable {
        @SerializedName("mileage")
        private int mileage;
        
        @SerializedName("batteryHealth")
        private int batteryHealth;
        
        @SerializedName("isActive")
        private boolean isActive;

        public int getMileage() {
            return mileage;
        }

        public int getBatteryHealth() {
            return batteryHealth;
        }

        public boolean isActive() {
            return isActive;
        }

        public void setMileage(int mileage) {
            this.mileage = mileage;
        }

        public void setBatteryHealth(int batteryHealth) {
            this.batteryHealth = batteryHealth;
        }

        public void setActive(boolean active) {
            isActive = active;
        }
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getOwner() {
        return owner;
    }

    public VehicleInfo getVehicleInfo() {
        return vehicleInfo;
    }

    public CurrentStatus getCurrentStatus() {
        return currentStatus;
    }

    public String getStatus() {
        return status;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    // Setters
    public void setId(String id) {
        this.id = id;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public void setVehicleInfo(VehicleInfo vehicleInfo) {
        this.vehicleInfo = vehicleInfo;
    }

    public void setCurrentStatus(CurrentStatus currentStatus) {
        this.currentStatus = currentStatus;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    // Helper methods for backward compatibility and display
    public String getLicensePlate() {
        return vehicleInfo != null ? vehicleInfo.getLicensePlate() : "";
    }

    public String getColor() {
        return vehicleInfo != null ? vehicleInfo.getColor() : "";
    }

    public int getYear() {
        return vehicleInfo != null ? vehicleInfo.getYear() : 0;
    }

    public VehicleModel getVehicleModel() {
        return vehicleInfo != null ? vehicleInfo.getVehicleModel() : null;
    }

    public int getMileage() {
        return currentStatus != null ? currentStatus.getMileage() : 0;
    }

    // Helper method to get full vehicle name
    public String getFullName() {
        if (vehicleInfo != null) {
            VehicleModel model = vehicleInfo.getVehicleModel();
            String licensePlate = vehicleInfo.getLicensePlate();
            
            if (model != null) {
                return model.getName() + " (" + licensePlate + ")";
            }
            return licensePlate != null ? licensePlate : "Unknown Vehicle";
        }
        return "Unknown Vehicle";
    }

    // Helper method to get vehicle display name with year
    public String getDisplayName() {
        if (vehicleInfo != null) {
            VehicleModel model = vehicleInfo.getVehicleModel();
            int year = vehicleInfo.getYear();
            
            if (model != null) {
                return year + " " + model.getName();
            }
            return year + " Vehicle";
        }
        return "Vehicle";
    }
}
