package com.example.prm392_evcare.models;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {
    @SerializedName("success")
    private boolean success;
    
    @SerializedName("message")
    private String message;
    
    @SerializedName("accessToken")
    private String accessToken;
    
    @SerializedName("refreshToken")
    private String refreshToken;
    
    @SerializedName("user")
    private User user;
    
    @SerializedName("needVerification")
    private boolean needVerification;

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public User getUser() {
        return user;
    }

    public boolean isNeedVerification() {
        return needVerification;
    }
}
