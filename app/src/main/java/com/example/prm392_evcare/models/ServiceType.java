package com.example.prm392_evcare.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class ServiceType implements Serializable {
    @SerializedName("_id")
    private String id;
    
    @SerializedName("name")
    private String name;
    
    @SerializedName("description")
    private String description;
    
    @SerializedName("category")
    private String category;
    
    @SerializedName("status")
    private String status;
    
    @SerializedName("isPopular")
    private boolean isPopular;
    
    @SerializedName("pricing")
    private Pricing pricing;
    
    @SerializedName("serviceDetails")
    private ServiceDetails serviceDetails;

    // Inner classes
    public static class Pricing implements Serializable {
        @SerializedName("basePrice")
        private double basePrice;
        
        @SerializedName("priceType")
        private String priceType;
        
        @SerializedName("currency")
        private String currency;
        
        @SerializedName("isNegotiable")
        private boolean isNegotiable;

        public double getBasePrice() {
            return basePrice;
        }

        public String getPriceType() {
            return priceType;
        }

        public String getCurrency() {
            return currency;
        }

        public boolean isNegotiable() {
            return isNegotiable;
        }
    }

    public static class ServiceDetails implements Serializable {
        @SerializedName("minTechnicians")
        private int minTechnicians;
        
        @SerializedName("maxTechnicians")
        private int maxTechnicians;

        public int getMinTechnicians() {
            return minTechnicians;
        }

        public int getMaxTechnicians() {
            return maxTechnicians;
        }
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getCategory() {
        return category;
    }

    public String getStatus() {
        return status;
    }

    public boolean isPopular() {
        return isPopular;
    }

    public Pricing getPricing() {
        return pricing;
    }

    public ServiceDetails getServiceDetails() {
        return serviceDetails;
    }

    // Helper methods
    public double getBasePrice() {
        return pricing != null ? pricing.getBasePrice() : 0;
    }

    public String getFormattedPrice() {
        double price = getBasePrice();
        if (price > 0) {
            return String.format("%,.0f VNĐ", price);
        }
        return "Liên hệ";
    }

    public String getFormattedDuration() {
        // Since API doesn't have duration, return technician info instead
        if (serviceDetails != null) {
            return serviceDetails.getMinTechnicians() + "-" + serviceDetails.getMaxTechnicians() + " kỹ thuật viên";
        }
        return "";
    }
    
    public int getEstimatedDuration() {
        // Default to 60 minutes if not specified
        return 60;
    }

    public boolean isActive() {
        return "active".equals(status);
    }
}
