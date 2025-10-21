package com.example.prm392_evcare.api;

import com.example.prm392_evcare.models.LoginRequest;
import com.example.prm392_evcare.models.LoginResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {
    @POST("/api/auth/login")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);
}
