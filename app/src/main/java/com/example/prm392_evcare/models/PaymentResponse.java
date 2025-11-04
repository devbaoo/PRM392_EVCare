package com.example.prm392_evcare.models;

import com.google.gson.annotations.SerializedName;

public class PaymentResponse {
    @SerializedName("success")
    private boolean success;
    
    @SerializedName("message")
    private String message;
    
    @SerializedName("data")
    private PaymentData data;

    public static class PaymentData {
        @SerializedName("_id")
        private String id;
        
        @SerializedName("appointmentId")
        private String appointmentId;
        
        @SerializedName("amount")
        private double amount;
        
        @SerializedName("method")
        private String method;
        
        @SerializedName("status")
        private String status; // pending, completed, failed, cancelled
        
        @SerializedName("transactionId")
        private String transactionId;
        
        @SerializedName("paymentUrl")
        private String paymentUrl;
        
        @SerializedName("paidAt")
        private String paidAt;
        
        @SerializedName("createdAt")
        private String createdAt;

        public String getId() { return id; }
        public String getAppointmentId() { return appointmentId; }
        public double getAmount() { return amount; }
        public String getMethod() { return method; }
        public String getStatus() { return status; }
        public String getTransactionId() { return transactionId; }
        public String getPaymentUrl() { return paymentUrl; }
        public String getPaidAt() { return paidAt; }
        public String getCreatedAt() { return createdAt; }
        
        public String getFormattedAmount() {
            return String.format("%,.0f VND", amount);
        }
        
        public String getStatusDisplay() {
            switch (status.toLowerCase()) {
                case "pending": return "Chờ thanh toán";
                case "completed": return "Đã thanh toán";
                case "failed": return "Thất bại";
                case "cancelled": return "Đã hủy";
                default: return status;
            }
        }
    }

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public PaymentData getData() { return data; }
    public PaymentData getPayment() { return data; }
}
