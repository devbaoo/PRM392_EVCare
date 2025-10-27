package com.example.prm392_evcare.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ServiceCenter {
    @SerializedName("_id")
    private String id;
    
    @SerializedName("name")
    private String name;
    
    @SerializedName("description")
    private String description;
    
    @SerializedName("status")
    private String status;
    
    @SerializedName("address")
    private Address address;
    
    @SerializedName("contact")
    private Contact contact;
    
    @SerializedName("operatingHours")
    private OperatingHours operatingHours;
    
    @SerializedName("capacity")
    private Capacity capacity;
    
    @SerializedName("rating")
    private Rating rating;
    
    @SerializedName("aiSettings")
    private AISettings aiSettings;
    
    @SerializedName("services")
    private List<Service> services;
    
    @SerializedName("staff")
    private List<Staff> staff;
    
    @SerializedName("images")
    private List<Image> images;
    
    @SerializedName("paymentMethods")
    private List<PaymentMethod> paymentMethods;
    
    @SerializedName("createdAt")
    private String createdAt;
    
    @SerializedName("updatedAt")
    private String updatedAt;
    
    private double distance;

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public Contact getContact() {
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }

    public OperatingHours getOperatingHours() {
        return operatingHours;
    }

    public void setOperatingHours(OperatingHours operatingHours) {
        this.operatingHours = operatingHours;
    }

    public Capacity getCapacity() {
        return capacity;
    }

    public void setCapacity(Capacity capacity) {
        this.capacity = capacity;
    }

    public Rating getRating() {
        return rating;
    }

    public void setRating(Rating rating) {
        this.rating = rating;
    }

    public AISettings getAiSettings() {
        return aiSettings;
    }

    public void setAiSettings(AISettings aiSettings) {
        this.aiSettings = aiSettings;
    }

    public List<Service> getServices() {
        return services;
    }

    public void setServices(List<Service> services) {
        this.services = services;
    }

    public List<Staff> getStaff() {
        return staff;
    }

    public void setStaff(List<Staff> staff) {
        this.staff = staff;
    }

    public List<Image> getImages() {
        return images;
    }

    public void setImages(List<Image> images) {
        this.images = images;
    }

    public List<PaymentMethod> getPaymentMethods() {
        return paymentMethods;
    }

    public void setPaymentMethods(List<PaymentMethod> paymentMethods) {
        this.paymentMethods = paymentMethods;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }
    
    // Helper methods
    public String getFullAddress() {
        if (address == null) return "";
        StringBuilder sb = new StringBuilder();
        if (address.getStreet() != null && !address.getStreet().isEmpty()) {
            sb.append(address.getStreet()).append(", ");
        }
        if (address.getWard() != null && !address.getWard().isEmpty()) {
            sb.append(address.getWard()).append(", ");
        }
        if (address.getDistrict() != null && !address.getDistrict().isEmpty()) {
            sb.append(address.getDistrict()).append(", ");
        }
        if (address.getCity() != null && !address.getCity().isEmpty()) {
            sb.append(address.getCity());
        }
        return sb.toString();
    }
    
    public String getPrimaryImageUrl() {
        if (images != null && !images.isEmpty()) {
            return images.get(0).getUrl();
        }
        return null;
    }

    // Inner Classes
    public static class Address {
        @SerializedName("coordinates")
        private Coordinates coordinates;
        
        @SerializedName("street")
        private String street;
        
        @SerializedName("ward")
        private String ward;
        
        @SerializedName("district")
        private String district;
        
        @SerializedName("city")
        private String city;

        public Coordinates getCoordinates() {
            return coordinates;
        }

        public void setCoordinates(Coordinates coordinates) {
            this.coordinates = coordinates;
        }

        public String getStreet() {
            return street;
        }

        public void setStreet(String street) {
            this.street = street;
        }

        public String getWard() {
            return ward;
        }

        public void setWard(String ward) {
            this.ward = ward;
        }

        public String getDistrict() {
            return district;
        }

        public void setDistrict(String district) {
            this.district = district;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public static class Coordinates {
            @SerializedName("lat")
            private double lat;
            
            @SerializedName("lng")
            private double lng;

            public double getLat() {
                return lat;
            }

            public void setLat(double lat) {
                this.lat = lat;
            }

            public double getLng() {
                return lng;
            }

            public void setLng(double lng) {
                this.lng = lng;
            }
        }
    }

    public static class Contact {
        @SerializedName("phone")
        private String phone;
        
        @SerializedName("email")
        private String email;
        
        @SerializedName("website")
        private String website;

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getWebsite() {
            return website;
        }

        public void setWebsite(String website) {
            this.website = website;
        }
    }

    public static class OperatingHours {
        @SerializedName("monday")
        private DaySchedule monday;
        
        @SerializedName("tuesday")
        private DaySchedule tuesday;
        
        @SerializedName("wednesday")
        private DaySchedule wednesday;
        
        @SerializedName("thursday")
        private DaySchedule thursday;
        
        @SerializedName("friday")
        private DaySchedule friday;
        
        @SerializedName("saturday")
        private DaySchedule saturday;
        
        @SerializedName("sunday")
        private DaySchedule sunday;

        public DaySchedule getMonday() {
            return monday;
        }

        public void setMonday(DaySchedule monday) {
            this.monday = monday;
        }

        public DaySchedule getTuesday() {
            return tuesday;
        }

        public void setTuesday(DaySchedule tuesday) {
            this.tuesday = tuesday;
        }

        public DaySchedule getWednesday() {
            return wednesday;
        }

        public void setWednesday(DaySchedule wednesday) {
            this.wednesday = wednesday;
        }

        public DaySchedule getThursday() {
            return thursday;
        }

        public void setThursday(DaySchedule thursday) {
            this.thursday = thursday;
        }

        public DaySchedule getFriday() {
            return friday;
        }

        public void setFriday(DaySchedule friday) {
            this.friday = friday;
        }

        public DaySchedule getSaturday() {
            return saturday;
        }

        public void setSaturday(DaySchedule saturday) {
            this.saturday = saturday;
        }

        public DaySchedule getSunday() {
            return sunday;
        }

        public void setSunday(DaySchedule sunday) {
            this.sunday = sunday;
        }

        public static class DaySchedule {
            @SerializedName("open")
            private String open;
            
            @SerializedName("close")
            private String close;
            
            @SerializedName("isOpen")
            private boolean isOpen;

            public String getOpen() {
                return open;
            }

            public void setOpen(String open) {
                this.open = open;
            }

            public String getClose() {
                return close;
            }

            public void setClose(String close) {
                this.close = close;
            }

            public boolean isOpen() {
                return isOpen;
            }

            public void setIsOpen(boolean open) {
                isOpen = open;
            }
            
            public String getHoursText() {
                if (!isOpen) return "Closed";
                if (open == null || close == null || open.isEmpty() || close.isEmpty()) {
                    return "24/7";
                }
                return open + " - " + close;
            }
        }
    }

    public static class Capacity {
        @SerializedName("maxConcurrentServices")
        private int maxConcurrentServices;
        
        @SerializedName("maxDailyAppointments")
        private int maxDailyAppointments;

        public int getMaxConcurrentServices() {
            return maxConcurrentServices;
        }

        public void setMaxConcurrentServices(int maxConcurrentServices) {
            this.maxConcurrentServices = maxConcurrentServices;
        }

        public int getMaxDailyAppointments() {
            return maxDailyAppointments;
        }

        public void setMaxDailyAppointments(int maxDailyAppointments) {
            this.maxDailyAppointments = maxDailyAppointments;
        }
    }

    public static class Rating {
        @SerializedName("average")
        private double average;
        
        @SerializedName("count")
        private int count;

        public double getAverage() {
            return average;
        }

        public void setAverage(double average) {
            this.average = average;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }
    }

    public static class AISettings {
        @SerializedName("enableInventoryPrediction")
        private boolean enableInventoryPrediction;
        
        @SerializedName("enableMaintenancePrediction")
        private boolean enableMaintenancePrediction;
        
        @SerializedName("enableDemandForecasting")
        private boolean enableDemandForecasting;

        public boolean isEnableInventoryPrediction() {
            return enableInventoryPrediction;
        }

        public void setEnableInventoryPrediction(boolean enableInventoryPrediction) {
            this.enableInventoryPrediction = enableInventoryPrediction;
        }

        public boolean isEnableMaintenancePrediction() {
            return enableMaintenancePrediction;
        }

        public void setEnableMaintenancePrediction(boolean enableMaintenancePrediction) {
            this.enableMaintenancePrediction = enableMaintenancePrediction;
        }

        public boolean isEnableDemandForecasting() {
            return enableDemandForecasting;
        }

        public void setEnableDemandForecasting(boolean enableDemandForecasting) {
            this.enableDemandForecasting = enableDemandForecasting;
        }
    }

    public static class Service {
        @SerializedName("_id")
        private String id;
        
        @SerializedName("name")
        private String name;
        
        @SerializedName("category")
        private String category;
        
        @SerializedName("pricing")
        private Pricing pricing;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public Pricing getPricing() {
            return pricing;
        }

        public void setPricing(Pricing pricing) {
            this.pricing = pricing;
        }

        public static class Pricing {
            @SerializedName("basePrice")
            private int basePrice;

            public int getBasePrice() {
                return basePrice;
            }

            public void setBasePrice(int basePrice) {
                this.basePrice = basePrice;
            }
        }
    }

    public static class Staff {
        @SerializedName("user")
        private com.example.prm392_evcare.models.User user;
        
        @SerializedName("role")
        private String role;
        
        @SerializedName("isActive")
        private boolean isActive;

        public com.example.prm392_evcare.models.User getUser() {
            return user;
        }

        public void setUser(com.example.prm392_evcare.models.User user) {
            this.user = user;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public boolean isActive() {
            return isActive;
        }

        public void setActive(boolean active) {
            isActive = active;
        }
    }

    public static class Image {
        @SerializedName("_id")
        private String id;
        
        @SerializedName("url")
        private String url;
        
        @SerializedName("caption")
        private String caption;
        
        @SerializedName("isPrimary")
        private boolean isPrimary;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getCaption() {
            return caption;
        }

        public void setCaption(String caption) {
            this.caption = caption;
        }

        public boolean isPrimary() {
            return isPrimary;
        }

        public void setPrimary(boolean primary) {
            isPrimary = primary;
        }
    }

    public static class PaymentMethod {
        @SerializedName("_id")
        private String id;
        
        @SerializedName("type")
        private String type;
        
        @SerializedName("isEnabled")
        private boolean isEnabled;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public boolean isEnabled() {
            return isEnabled;
        }

        public void setEnabled(boolean enabled) {
            isEnabled = enabled;
        }
    }
}
