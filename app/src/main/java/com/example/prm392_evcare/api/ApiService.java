package com.example.prm392_evcare.api;

import com.example.prm392_evcare.models.ForgotPasswordRequest;
import com.example.prm392_evcare.models.ForgotPasswordResponse;
import com.example.prm392_evcare.models.LoginRequest;
import com.example.prm392_evcare.models.LoginResponse;
import com.example.prm392_evcare.models.RegisterRequest;
import com.example.prm392_evcare.models.ServiceCenter;
import com.example.prm392_evcare.models.NearbyServiceCentersResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiService {
    @POST("/api/auth/login")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);
    
    @POST("/api/auth/register")
    Call<LoginResponse> register(@Body RegisterRequest registerRequest);
    
    @POST("/api/auth/forgot-password")
    Call<ForgotPasswordResponse> forgotPassword(@Body ForgotPasswordRequest forgotPasswordRequest);
    
    @GET("/api/service-centers/nearby/search")
    Call<NearbyServiceCentersResponse> getNearbyServiceCenters(
        @Query("lat") double latitude,
        @Query("lng") double longitude,
        @Query("radius") int radius
    );
}
