package com.example.prm392_evcare.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class CreateBookingRequest {
    @SerializedName("vehicleId")
    private String vehicleId;
    
    @SerializedName("serviceCenterId")
    private String serviceCenterId;
    
    @SerializedName("serviceTypeIds")
    private List<String> serviceTypeIds;
    
    @SerializedName("scheduledDate")
    private String scheduledDate;
    
    @SerializedName("timeSlot")
    private String timeSlot;
    
    @SerializedName("notes")
    private String notes;

    public CreateBookingRequest(String vehicleId, String serviceCenterId, 
                                List<String> serviceTypeIds, String scheduledDate, 
                                String timeSlot, String notes) {
        this.vehicleId = vehicleId;
        this.serviceCenterId = serviceCenterId;
        this.serviceTypeIds = serviceTypeIds;
        this.scheduledDate = scheduledDate;
        this.timeSlot = timeSlot;
        this.notes = notes;
    }

    // Getters and Setters
    public String getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getServiceCenterId() {
        return serviceCenterId;
    }

    public void setServiceCenterId(String serviceCenterId) {
        this.serviceCenterId = serviceCenterId;
    }

    public List<String> getServiceTypeIds() {
        return serviceTypeIds;
    }

    public void setServiceTypeIds(List<String> serviceTypeIds) {
        this.serviceTypeIds = serviceTypeIds;
    }

    public String getScheduledDate() {
        return scheduledDate;
    }

    public void setScheduledDate(String scheduledDate) {
        this.scheduledDate = scheduledDate;
    }

    public String getTimeSlot() {
        return timeSlot;
    }

    public void setTimeSlot(String timeSlot) {
        this.timeSlot = timeSlot;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
