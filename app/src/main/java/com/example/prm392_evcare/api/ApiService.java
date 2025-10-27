package com.example.prm392_evcare.api;

import com.example.prm392_evcare.models.ForgotPasswordRequest;
import com.example.prm392_evcare.models.ForgotPasswordResponse;
import com.example.prm392_evcare.models.LoginRequest;
import com.example.prm392_evcare.models.LoginResponse;
import com.example.prm392_evcare.models.RegisterRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {
    @POST("/api/auth/login")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);
    
    @POST("/api/auth/register")
    Call<LoginResponse> register(@Body RegisterRequest registerRequest);
    
    @POST("/api/auth/forgot-password")
    Call<ForgotPasswordResponse> forgotPassword(@Body ForgotPasswordRequest forgotPasswordRequest);
}
