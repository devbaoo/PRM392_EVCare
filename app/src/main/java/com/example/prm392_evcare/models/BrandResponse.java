package com.example.prm392_evcare.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class BrandResponse {
    @SerializedName("success")
    private boolean success;
    
    @SerializedName("message")
    private String message;
    
    @SerializedName("data")
    private List<String> data;

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public List<String> getData() {
        return data;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setData(List<String> data) {
        this.data = data;
    }
}
