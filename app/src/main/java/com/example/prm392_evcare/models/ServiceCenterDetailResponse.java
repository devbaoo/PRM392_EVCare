package com.example.prm392_evcare.models;

import com.google.gson.annotations.SerializedName;

public class ServiceCenterDetailResponse {
    @SerializedName("success")
    private boolean success;
    
    @SerializedName("message")
    private String message;
    
    @SerializedName("data")
    private ServiceCenter data;

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public ServiceCenter getData() { return data; }
    public ServiceCenter getServiceCenter() { return data; }
}
