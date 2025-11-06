package com.example.prm392_evcare.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class TechnicianScheduleResponse {
    @SerializedName("success")
    private boolean success;
    
    @SerializedName("message")
    private String message;
    
    @SerializedName("data")
    private List<ScheduleData> data;
    
    public boolean isSuccess() {
        return success;
    }
    
    public String getMessage() {
        return message;
    }
    
    public List<ScheduleData> getData() {
        return data;
    }
    
    public static class ScheduleData {
        @SerializedName("_id")
        private String id;
        
        @SerializedName("appointment")
        private AppointmentInfo appointment;
        
        @SerializedName("date")
        private String date; // Format: "2024-11-05T00:00:00.000Z"
        
        @SerializedName("workDate")
        private String workDate; // Alternative field name from API
        
        @SerializedName("startTime")
        private String startTime; // Format: "08:00"
        
        @SerializedName("shiftStart")
        private String shiftStart; // Alternative field name from API
        
        @SerializedName("endTime")
        private String endTime; // Format: "17:00"
        
        @SerializedName("shiftEnd")
        private String shiftEnd; // Alternative field name from API
        
        @SerializedName("status")
        private String status; // "pending", "in_progress", "completed", "cancelled", "scheduled", "working"
        
        @SerializedName("notes")
        private String notes;
        
        @SerializedName("technician")
        private String technicianId;
        
        @SerializedName("createdAt")
        private String createdAt;
        
        @SerializedName("updatedAt")
        private String updatedAt;
        
        @SerializedName("checkInTime")
        private String checkInTime;
        
        @SerializedName("checkOutTime")
        private String checkOutTime;
        
        // Getters
        public String getId() {
            return id;
        }
        
        public AppointmentInfo getAppointment() {
            return appointment;
        }
        
        public String getDate() {
            // Fallback: try workDate if date is null
            return date != null ? date : workDate;
        }
        
        public String getStartTime() {
            // Fallback: try shiftStart if startTime is null
            return startTime != null ? startTime : shiftStart;
        }
        
        public String getEndTime() {
            // Fallback: try shiftEnd if endTime is null
            return endTime != null ? endTime : shiftEnd;
        }
        
        public String getStatus() {
            return status;
        }
        
        public String getNotes() {
            return notes;
        }
        
        public String getTechnicianId() {
            return technicianId;
        }
        
        public String getCreatedAt() {
            return createdAt;
        }
        
        public String getUpdatedAt() {
            return updatedAt;
        }
        
        public String getCheckInTime() {
            return checkInTime;
        }
        
        public String getCheckOutTime() {
            return checkOutTime;
        }
        
        public boolean isCheckedIn() {
            return checkInTime != null && !checkInTime.isEmpty();
        }
        
        public boolean isCheckedOut() {
            return checkOutTime != null && !checkOutTime.isEmpty();
        }
    }
    
    public static class AppointmentInfo {
        @SerializedName("_id")
        private String id;
        
        @SerializedName("customer")
        private CustomerInfo customer;
        
        @SerializedName("vehicle")
        private VehicleInfo vehicle;
        
        @SerializedName("serviceCenter")
        private ServiceCenterInfo serviceCenter;
        
        @SerializedName("services")
        private List<ServiceInfo> services;
        
        @SerializedName("appointmentDate")
        private String appointmentDate;
        
        @SerializedName("timeSlot")
        private String timeSlot;
        
        @SerializedName("status")
        private String status;
        
        @SerializedName("totalPrice")
        private double totalPrice;
        
        // Getters
        public String getId() {
            return id;
        }
        
        public CustomerInfo getCustomer() {
            return customer;
        }
        
        public VehicleInfo getVehicle() {
            return vehicle;
        }
        
        public ServiceCenterInfo getServiceCenter() {
            return serviceCenter;
        }
        
        public List<ServiceInfo> getServices() {
            return services;
        }
        
        public String getAppointmentDate() {
            return appointmentDate;
        }
        
        public String getTimeSlot() {
            return timeSlot;
        }
        
        public String getStatus() {
            return status;
        }
        
        public double getTotalPrice() {
            return totalPrice;
        }
    }
    
    public static class CustomerInfo {
        @SerializedName("_id")
        private String id;
        
        @SerializedName("name")
        private String name;
        
        @SerializedName("email")
        private String email;
        
        @SerializedName("phone")
        private String phone;
        
        // Getters
        public String getId() {
            return id;
        }
        
        public String getName() {
            return name;
        }
        
        public String getEmail() {
            return email;
        }
        
        public String getPhone() {
            return phone;
        }
    }
    
    public static class VehicleInfo {
        @SerializedName("_id")
        private String id;
        
        @SerializedName("brand")
        private String brand;
        
        @SerializedName("model")
        private String model;
        
        @SerializedName("year")
        private int year;
        
        @SerializedName("licensePlate")
        private String licensePlate;
        
        // Getters
        public String getId() {
            return id;
        }
        
        public String getBrand() {
            return brand;
        }
        
        public String getModel() {
            return model;
        }
        
        public int getYear() {
            return year;
        }
        
        public String getLicensePlate() {
            return licensePlate;
        }
    }
    
    public static class ServiceCenterInfo {
        @SerializedName("_id")
        private String id;
        
        @SerializedName("name")
        private String name;
        
        @SerializedName("address")
        private String address;
        
        // Getters
        public String getId() {
            return id;
        }
        
        public String getName() {
            return name;
        }
        
        public String getAddress() {
            return address;
        }
    }
    
    public static class ServiceInfo {
        @SerializedName("_id")
        private String id;
        
        @SerializedName("name")
        private String name;
        
        @SerializedName("description")
        private String description;
        
        @SerializedName("price")
        private double price;
        
        @SerializedName("duration")
        private int duration;
        
        // Getters
        public String getId() {
            return id;
        }
        
        public String getName() {
            return name;
        }
        
        public String getDescription() {
            return description;
        }
        
        public double getPrice() {
            return price;
        }
        
        public int getDuration() {
            return duration;
        }
    }
}

