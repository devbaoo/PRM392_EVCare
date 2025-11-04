package com.example.prm392_evcare.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class BookingResponse {
    @SerializedName("success")
    private boolean success;
    
    @SerializedName("message")
    private String message;
    
    @SerializedName("data")
    private Data data;

    // Inner class for nested data structure
    public static class Data {
        @SerializedName("appointment")
        private Booking booking;
        
        @SerializedName("appointments")
        private List<Booking> bookings;
        
        @SerializedName("pagination")
        private Pagination pagination;

        public Booking getBooking() {
            return booking;
        }

        public List<Booking> getBookings() {
            return bookings;
        }

        public Pagination getPagination() {
            return pagination;
        }
    }

    // Pagination info
    public static class Pagination {
        @SerializedName("currentPage")
        private int currentPage;
        
        @SerializedName("totalPages")
        private int totalPages;
        
        @SerializedName("totalItems")
        private int totalItems;
        
        @SerializedName("itemsPerPage")
        private int itemsPerPage;

        public int getCurrentPage() {
            return currentPage;
        }

        public int getTotalPages() {
            return totalPages;
        }

        public int getTotalItems() {
            return totalItems;
        }

        public int getItemsPerPage() {
            return itemsPerPage;
        }
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public Data getData() {
        return data;
    }

    // Helper methods for backward compatibility
    public Booking getBooking() {
        return data != null ? data.getBooking() : null;
    }

    public List<Booking> getBookings() {
        return data != null ? data.getBookings() : null;
    }

    public Pagination getPagination() {
        return data != null ? data.getPagination() : null;
    }
}
