package com.example.prm392_evcare.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Booking implements Serializable {
    @SerializedName("_id")
    private String id;
    
    @SerializedName("appointmentTime")
    private AppointmentTime appointmentTime;
    
    @SerializedName("serviceType")
    private ServiceTypeInfo serviceType;
    
    @SerializedName("serviceCenter")
    private ServiceCenterInfo serviceCenter;
    
    @SerializedName("vehicle")
    private VehicleInfo vehicle;
    
    @SerializedName("status")
    private String status;
    
    @SerializedName("serviceDetails")
    private ServiceDetails serviceDetails;
    
    @SerializedName("payment")
    private Payment payment;
    
    @SerializedName("createdAt")
    private String createdAt;
    
    @SerializedName("updatedAt")
    private String updatedAt;

    // Inner classes
    public static class AppointmentTime implements Serializable {
        @SerializedName("date")
        private String date;
        
        @SerializedName("startTime")
        private String startTime;
        
        @SerializedName("endTime")
        private String endTime;
        
        @SerializedName("duration")
        private Integer duration;

        public String getDate() { return date; }
        public String getStartTime() { return startTime; }
        public String getEndTime() { return endTime; }
        public Integer getDuration() { return duration; }
    }

    public static class ServiceTypeInfo implements Serializable {
        @SerializedName("_id")
        private String id;
        
        @SerializedName("name")
        private String name;
        
        @SerializedName("category")
        private String category;
        
        @SerializedName("pricing")
        private Pricing pricing;

        public String getId() { return id; }
        public String getName() { return name; }
        public String getCategory() { return category; }
        public Pricing getPricing() { return pricing; }
    }

    public static class Pricing implements Serializable {
        @SerializedName("basePrice")
        private double basePrice;
        
        @SerializedName("priceType")
        private String priceType;
        
        @SerializedName("currency")
        private String currency;

        public double getBasePrice() { return basePrice; }
        public String getPriceType() { return priceType; }
        public String getCurrency() { return currency; }
    }

    public static class ServiceCenterInfo implements Serializable {
        @SerializedName("_id")
        private String id;
        
        @SerializedName("name")
        private String name;
        
        @SerializedName("address")
        private Address address;
        
        @SerializedName("contact")
        private Contact contact;

        public String getId() { return id; }
        public String getName() { return name; }
        public Address getAddress() { return address; }
        public Contact getContact() { return contact; }
    }

    public static class Address implements Serializable {
        @SerializedName("street")
        private String street;
        
        @SerializedName("ward")
        private String ward;
        
        @SerializedName("district")
        private String district;
        
        @SerializedName("city")
        private String city;

        public String getStreet() { return street; }
        public String getWard() { return ward; }
        public String getDistrict() { return district; }
        public String getCity() { return city; }
        
        public String getFullAddress() {
            return street + ", " + ward + ", " + district + ", " + city;
        }
    }

    public static class Contact implements Serializable {
        @SerializedName("phone")
        private String phone;
        
        @SerializedName("email")
        private String email;

        public String getPhone() { return phone; }
        public String getEmail() { return email; }
    }

    public static class VehicleInfo implements Serializable {
        @SerializedName("_id")
        private String id;
        
        @SerializedName("vehicleInfo")
        private VehicleDetails vehicleInfo;

        public String getId() { return id; }
        public VehicleDetails getVehicleInfo() { return vehicleInfo; }
    }

    public static class VehicleDetails implements Serializable {
        @SerializedName("vehicleModel")
        private String vehicleModel;
        
        @SerializedName("year")
        private int year;
        
        @SerializedName("color")
        private String color;
        
        @SerializedName("licensePlate")
        private String licensePlate;

        public String getVehicleModel() { return vehicleModel; }
        public int getYear() { return year; }
        public String getColor() { return color; }
        public String getLicensePlate() { return licensePlate; }
        
        public String getFullName() {
            return year + " " + vehicleModel + " (" + licensePlate + ")";
        }
    }

    public static class ServiceDetails implements Serializable {
        @SerializedName("description")
        private String description;
        
        @SerializedName("estimatedCost")
        private Double estimatedCost;
        
        @SerializedName("isInspectionOnly")
        private Boolean isInspectionOnly;

        public String getDescription() { return description; }
        public Double getEstimatedCost() { return estimatedCost; }
        public Boolean getInspectionOnly() { return isInspectionOnly; }
    }

    public static class Payment implements Serializable {
        @SerializedName("method")
        private String method;
        
        @SerializedName("status")
        private String status;
        
        @SerializedName("amount")
        private double amount;
        
        @SerializedName("paidAt")
        private String paidAt;

        public String getMethod() { return method; }
        public String getStatus() { return status; }
        public double getAmount() { return amount; }
        public String getPaidAt() { return paidAt; }
    }

    // Main getters
    public String getId() { return id; }
    public AppointmentTime getAppointmentTime() { return appointmentTime; }
    public ServiceTypeInfo getServiceType() { return serviceType; }
    public ServiceCenterInfo getServiceCenter() { return serviceCenter; }
    public VehicleInfo getVehicle() { return vehicle; }
    public String getStatus() { return status; }
    public ServiceDetails getServiceDetails() { return serviceDetails; }
    public Payment getPayment() { return payment; }
    public String getCreatedAt() { return createdAt; }
    public String getUpdatedAt() { return updatedAt; }

    // Setters
    public void setId(String id) { this.id = id; }
    public void setAppointmentTime(AppointmentTime appointmentTime) { this.appointmentTime = appointmentTime; }
    public void setServiceType(ServiceTypeInfo serviceType) { this.serviceType = serviceType; }
    public void setServiceCenter(ServiceCenterInfo serviceCenter) { this.serviceCenter = serviceCenter; }
    public void setVehicle(VehicleInfo vehicle) { this.vehicle = vehicle; }
    public void setStatus(String status) { this.status = status; }
    public void setServiceDetails(ServiceDetails serviceDetails) { this.serviceDetails = serviceDetails; }
    public void setPayment(Payment payment) { this.payment = payment; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }

    // Helper methods
    public String getStatusDisplay() {
        if (status == null) return "Unknown";
        switch (status.toLowerCase()) {
            case "pending": return "Chờ xác nhận";
            case "confirmed": return "Đã xác nhận";
            case "in_progress": return "Đang thực hiện";
            case "completed": return "Hoàn thành";
            case "cancelled": return "Đã hủy";
            default: return status;
        }
    }

    public String getFormattedTotalPrice() {
        double amount = payment != null ? payment.getAmount() : 
                       (serviceDetails != null && serviceDetails.getEstimatedCost() != null) ? 
                       serviceDetails.getEstimatedCost() : 0;
        return String.format("%,.0f VND", amount);
    }

    public double getTotalPrice() {
        return payment != null ? payment.getAmount() : 
               (serviceDetails != null && serviceDetails.getEstimatedCost() != null) ? 
               serviceDetails.getEstimatedCost() : 0;
    }

    public String getBookingDate() {
        return appointmentTime != null ? appointmentTime.getDate() : createdAt;
    }
}
