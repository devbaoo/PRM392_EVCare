package com.example.prm392_evcare.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class PaymentHistoryResponse {
    @SerializedName("success")
    private boolean success;
    
    @SerializedName("message")
    private String message;
    
    @SerializedName("data")
    private Data data;

    public static class Data {
        @SerializedName("payments")
        private List<PaymentResponse.PaymentData> payments;
        
        @SerializedName("pagination")
        private Pagination pagination;

        public List<PaymentResponse.PaymentData> getPayments() {
            return payments;
        }

        public Pagination getPagination() {
            return pagination;
        }
    }

    public static class Pagination {
        @SerializedName("currentPage")
        private int currentPage;
        
        @SerializedName("totalPages")
        private int totalPages;
        
        @SerializedName("totalItems")
        private int totalItems;

        public int getCurrentPage() { return currentPage; }
        public int getTotalPages() { return totalPages; }
        public int getTotalItems() { return totalItems; }
    }

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public Data getData() { return data; }
    
    public List<PaymentResponse.PaymentData> getPayments() {
        return data != null ? data.getPayments() : null;
    }
}
