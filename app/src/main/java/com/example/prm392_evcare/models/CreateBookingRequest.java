package com.example.prm392_evcare.models;

import com.google.gson.annotations.SerializedName;

public class CreateBookingRequest {
    @SerializedName("customerId")
    private String customerId;
    
    @SerializedName("vehicleId")
    private String vehicleId;
    
    @SerializedName("serviceCenterId")
    private String serviceCenterId;
    
    @SerializedName("serviceTypeId")
    private String serviceTypeId;
    
    @SerializedName("appointmentDate")
    private String appointmentDate;
    
    @SerializedName("appointmentTime")
    private String appointmentTime;
    
    @SerializedName("serviceDescription")
    private String serviceDescription;
    
    @SerializedName("paymentPreference")
    private String paymentPreference;
    
    @SerializedName("isInspectionOnly")
    private boolean isInspectionOnly;

    public CreateBookingRequest(String customerId, String vehicleId, String serviceCenterId,
                                String serviceTypeId, String appointmentDate, String appointmentTime, 
                                String serviceDescription, String paymentPreference, 
                                boolean isInspectionOnly) {
        this.customerId = customerId;
        this.vehicleId = vehicleId;
        this.serviceCenterId = serviceCenterId;
        this.serviceTypeId = serviceTypeId;
        this.appointmentDate = appointmentDate;
        this.appointmentTime = appointmentTime;
        this.serviceDescription = serviceDescription;
        this.paymentPreference = paymentPreference;
        this.isInspectionOnly = isInspectionOnly;
    }

    // Getters and Setters
    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

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

    public String getServiceTypeId() {
        return serviceTypeId;
    }

    public void setServiceTypeId(String serviceTypeId) {
        this.serviceTypeId = serviceTypeId;
    }

    public String getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(String appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public String getAppointmentTime() {
        return appointmentTime;
    }

    public void setAppointmentTime(String appointmentTime) {
        this.appointmentTime = appointmentTime;
    }

    public String getServiceDescription() {
        return serviceDescription;
    }

    public void setServiceDescription(String serviceDescription) {
        this.serviceDescription = serviceDescription;
    }

    public String getPaymentPreference() {
        return paymentPreference;
    }

    public void setPaymentPreference(String paymentPreference) {
        this.paymentPreference = paymentPreference;
    }

    public boolean isInspectionOnly() {
        return isInspectionOnly;
    }

    public void setInspectionOnly(boolean inspectionOnly) {
        isInspectionOnly = inspectionOnly;
    }
}
