package com.example.prm392_evcare.models;

import com.google.gson.annotations.SerializedName;

public class UpdateProfileRequest {
    @SerializedName("username")
    private String username;
    
    @SerializedName("fullName")
    private String fullName;
    
    @SerializedName("phone")
    private String phone;
    
    @SerializedName("address")
    private String address;

    public UpdateProfileRequest(String username, String fullName, String phone, String address) {
        this.username = username;
        this.fullName = fullName;
        this.phone = phone;
        this.address = address;
    }

    // Getters and Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
