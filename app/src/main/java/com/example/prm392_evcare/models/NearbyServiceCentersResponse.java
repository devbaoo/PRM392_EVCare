package com.example.prm392_evcare.models;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class NearbyServiceCentersResponse {
    @SerializedName("success")
    private boolean success;
    
    @SerializedName("message")
    private String message;
    
    @SerializedName("data")
    @JsonAdapter(DataDeserializer.class)
    private Data data;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    // Custom deserializer to handle both object and array formats
    static class DataDeserializer implements JsonDeserializer<Data> {
        @Override
        public Data deserialize(JsonElement json, Type typeOfT, com.google.gson.JsonDeserializationContext context) throws JsonParseException {
            Data data = new Data();
            
            if (json.isJsonArray()) {
                // If data is an array, set it as serviceCenters
                List<ServiceCenter> serviceCenters = new ArrayList<>();
                for (JsonElement element : json.getAsJsonArray()) {
                    serviceCenters.add(context.deserialize(element, ServiceCenter.class));
                }
                data.setServiceCenters(serviceCenters);
            } else if (json.isJsonObject()) {
                // If data is an object, parse normally
                if (json.getAsJsonObject().has("serviceCenters")) {
                    List<ServiceCenter> serviceCenters = new ArrayList<>();
                    for (JsonElement element : json.getAsJsonObject().get("serviceCenters").getAsJsonArray()) {
                        serviceCenters.add(context.deserialize(element, ServiceCenter.class));
                    }
                    data.setServiceCenters(serviceCenters);
                }
                
                if (json.getAsJsonObject().has("pagination")) {
                    Data.Pagination pagination = context.deserialize(
                        json.getAsJsonObject().get("pagination"),
                        Data.Pagination.class
                    );
                    data.setPagination(pagination);
                }
            }
            
            return data;
        }
    }

    public static class Data {
        private List<ServiceCenter> serviceCenters;
        private Pagination pagination;

        public List<ServiceCenter> getServiceCenters() {
            return serviceCenters;
        }

        public void setServiceCenters(List<ServiceCenter> serviceCenters) {
            this.serviceCenters = serviceCenters;
        }

        public Data.Pagination getPagination() {
            return pagination;
        }

        public void setPagination(Data.Pagination pagination) {
            this.pagination = pagination;
        }

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

            public void setCurrentPage(int currentPage) {
                this.currentPage = currentPage;
            }

            public int getTotalPages() {
                return totalPages;
            }

            public void setTotalPages(int totalPages) {
                this.totalPages = totalPages;
            }

            public int getTotalItems() {
                return totalItems;
            }

            public void setTotalItems(int totalItems) {
                this.totalItems = totalItems;
            }

            public int getItemsPerPage() {
                return itemsPerPage;
            }

            public void setItemsPerPage(int itemsPerPage) {
                this.itemsPerPage = itemsPerPage;
            }
        }
    }
}

