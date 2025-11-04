package com.example.prm392_evcare.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ServicePackagesResponse {
    @SerializedName("success")
    private boolean success;
    
    @SerializedName("message")
    private String message;
    
    @SerializedName("data")
    private List<ServicePackage> data;

    public static class ServicePackage {
        @SerializedName("_id")
        private String id;
        
        @SerializedName("packageName")
        private String packageName;
        
        @SerializedName("description")
        private String description;
        
        @SerializedName("durationMonths")
        private int durationMonths;
        
        @SerializedName("price")
        private double price;
        
        @SerializedName("includedServices")
        private List<IncludedService> includedServices;
        
        @SerializedName("maxServicesPerMonth")
        private int maxServicesPerMonth;

        public static class IncludedService {
            @SerializedName("_id")
            private String id;
            
            @SerializedName("name")
            private String name;
            
            @SerializedName("category")
            private String category;

            public String getId() { return id; }
            public String getName() { return name; }
            public String getCategory() { return category; }
        }

        public String getId() { return id; }
        public String getPackageName() { return packageName; }
        public String getDescription() { return description; }
        public int getDurationMonths() { return durationMonths; }
        public double getPrice() { return price; }
        public List<IncludedService> getIncludedServices() { return includedServices; }
        public int getMaxServicesPerMonth() { return maxServicesPerMonth; }
        
        public String getFormattedPrice() {
            return String.format("%,.0f VND", price);
        }
    }

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public List<ServicePackage> getData() { return data; }
}
