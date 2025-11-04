package com.example.prm392_evcare.models;

import com.google.gson.annotations.SerializedName;

public class PaymentRequest {
    @SerializedName("method")
    private String method; // "online" or "offline"
    
    @SerializedName("amount")
    private double amount;
    
    @SerializedName("returnUrl")
    private String returnUrl;
    
    @SerializedName("cancelUrl")
    private String cancelUrl;

    public PaymentRequest(String method, double amount) {
        this.method = method;
        this.amount = amount;
    }

    public String getMethod() { return method; }
    public void setMethod(String method) { this.method = method; }
    
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    
    public String getReturnUrl() { return returnUrl; }
    public void setReturnUrl(String returnUrl) { this.returnUrl = returnUrl; }
    
    public String getCancelUrl() { return cancelUrl; }
    public void setCancelUrl(String cancelUrl) { this.cancelUrl = cancelUrl; }
}
