package com.example.prm392_evcare.models;

import com.google.gson.annotations.SerializedName;

public class CreateVehicleRequest {
    @SerializedName("vehicleInfo")
    private VehicleInfo vehicleInfo;

    public CreateVehicleRequest(VehicleInfo vehicleInfo) {
        this.vehicleInfo = vehicleInfo;
    }

    public VehicleInfo getVehicleInfo() {
        return vehicleInfo;
    }

    public void setVehicleInfo(VehicleInfo vehicleInfo) {
        this.vehicleInfo = vehicleInfo;
    }

    public static class VehicleInfo {
        @SerializedName("brand")
        private String brand;

        @SerializedName("modelName")
        private String modelName;

        @SerializedName("year")
        private int year;

        @SerializedName("batteryType")
        private String batteryType;

        @SerializedName("licensePlate")
        private String licensePlate;

        @SerializedName("color")
        private String color;

        @SerializedName("batteryCapacity")
        private String batteryCapacity;

        public VehicleInfo(String brand, String modelName, int year, String batteryType,
                          String licensePlate, String color, String batteryCapacity) {
            this.brand = brand;
            this.modelName = modelName;
            this.year = year;
            this.batteryType = batteryType;
            this.licensePlate = licensePlate;
            this.color = color;
            this.batteryCapacity = batteryCapacity;
        }

        // Getters and Setters
        public String getBrand() {
            return brand;
        }

        public void setBrand(String brand) {
            this.brand = brand;
        }

        public String getModelName() {
            return modelName;
        }

        public void setModelName(String modelName) {
            this.modelName = modelName;
        }

        public int getYear() {
            return year;
        }

        public void setYear(int year) {
            this.year = year;
        }

        public String getBatteryType() {
            return batteryType;
        }

        public void setBatteryType(String batteryType) {
            this.batteryType = batteryType;
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

        public String getBatteryCapacity() {
            return batteryCapacity;
        }

        public void setBatteryCapacity(String batteryCapacity) {
            this.batteryCapacity = batteryCapacity;
        }
    }
}
