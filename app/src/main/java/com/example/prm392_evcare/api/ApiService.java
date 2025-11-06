package com.example.prm392_evcare.api;

import com.example.prm392_evcare.models.BookingResponse;
import com.example.prm392_evcare.models.BrandResponse;
import com.example.prm392_evcare.models.ChangePasswordRequest;
import com.example.prm392_evcare.models.CreateBookingRequest;
import com.example.prm392_evcare.models.CreateVehicleRequest;
import com.example.prm392_evcare.models.ForgotPasswordRequest;
import com.example.prm392_evcare.models.ForgotPasswordResponse;
import com.example.prm392_evcare.models.LoginRequest;
import com.example.prm392_evcare.models.LoginResponse;
import com.example.prm392_evcare.models.NearbyServiceCentersResponse;
import com.example.prm392_evcare.models.PaymentHistoryResponse;
import com.example.prm392_evcare.models.PaymentRequest;
import com.example.prm392_evcare.models.PaymentResponse;
import com.example.prm392_evcare.models.RefreshTokenRequest;
import com.example.prm392_evcare.models.RefreshTokenResponse;
import com.example.prm392_evcare.models.RegisterRequest;
import com.example.prm392_evcare.models.ServiceCenterDetailResponse;
import com.example.prm392_evcare.models.ServicePackagesResponse;
import com.example.prm392_evcare.models.ServiceTypesResponse;
import com.example.prm392_evcare.models.TimeSlotsResponse;
import com.example.prm392_evcare.models.UpdateProfileRequest;
import com.example.prm392_evcare.models.UserProfileResponse;
import com.example.prm392_evcare.models.VehicleModelsResponse;
import com.example.prm392_evcare.models.VehicleResponse;

import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    // ==================== Auth Endpoints ====================
    @POST("/api/auth/login")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);
    
    @POST("/api/auth/register")
    Call<LoginResponse> register(@Body RegisterRequest registerRequest);
    
    @POST("/api/auth/forgot-password")
    Call<ForgotPasswordResponse> forgotPassword(@Body ForgotPasswordRequest forgotPasswordRequest);
    
    @POST("/api/auth/change-password")
    Call<UserProfileResponse> changePassword(
        @Header("Authorization") String token,
        @Body ChangePasswordRequest request
    );
    
    @POST("/api/auth/refresh-token")
    Call<RefreshTokenResponse> refreshToken(@Body RefreshTokenRequest request);
    
    // ==================== User Endpoints ====================
    @GET("/api/user/profile")
    Call<UserProfileResponse> getUserProfile(@Header("Authorization") String token);
    
    @PUT("/api/user/profile")
    Call<UserProfileResponse> updateUserProfile(
        @Header("Authorization") String token,
        @Body UpdateProfileRequest request
    );
    
    @Multipart
    @POST("/api/user/upload-avatar")
    Call<UserProfileResponse> uploadAvatar(
        @Header("Authorization") String token,
        @Part MultipartBody.Part avatar
    );
    
    // ==================== Service Center Endpoints ====================
    @GET("/api/service-centers/nearby/search")
    Call<NearbyServiceCentersResponse> getNearbyServiceCenters(
        @Query("lat") double latitude,
        @Query("lng") double longitude,
        @Query("radius") int radius
    );

    @GET("/api/service-centers")
    Call<NearbyServiceCentersResponse> getServiceCenters(
        @Query("page") int page,
        @Query("limit") int limit
    );
    
    // ==================== Vehicle Endpoints ====================
    @GET("/api/vehicles")
    Call<VehicleResponse> getMyVehicles(@Header("Authorization") String token);
    
    @POST("/api/vehicles")
    Call<VehicleResponse> createVehicle(
        @Header("Authorization") String token,
        @Body CreateVehicleRequest request
    );
    
    @PUT("/api/vehicles/{vehicleId}")
    Call<VehicleResponse> updateVehicle(
        @Header("Authorization") String token,
        @Path("vehicleId") String vehicleId,
        @Body CreateVehicleRequest request
    );
    
    @DELETE("/api/vehicles/{vehicleId}")
    Call<VehicleResponse> deleteVehicle(
        @Header("Authorization") String token,
        @Path("vehicleId") String vehicleId
    );
    
    @GET("/api/vehicle-models")
    Call<VehicleModelsResponse> getVehicleModels();
    
    @GET("/api/vehicle-models/brands/list")
    Call<BrandResponse> getBrands();
    
    // ==================== Booking Endpoints ====================
    @POST("/api/booking")
    Call<BookingResponse> createBooking(
        @Header("Authorization") String token,
        @Body CreateBookingRequest request
    );
    
    @GET("/api/booking/my-bookings")
    Call<BookingResponse> getMyBookings(@Header("Authorization") String token);
    
    @GET("/api/booking/{bookingId}")
    Call<BookingResponse> getBookingDetail(
        @Header("Authorization") String token,
        @Path("bookingId") String bookingId
    );
    
    @PUT("/api/booking/{bookingId}/cancel")
    Call<BookingResponse> cancelBooking(
        @Header("Authorization") String token,
        @Path("bookingId") String bookingId
    );
    
    @PUT("/api/booking/{bookingId}/reschedule")
    Call<BookingResponse> rescheduleBooking(
        @Header("Authorization") String token,
        @Path("bookingId") String bookingId,
        @Body CreateBookingRequest request
    );
    
    @GET("/api/booking/service-centers")
    Call<NearbyServiceCentersResponse> getBookingServiceCenters(
        @Header("Authorization") String token
    );
    
    @GET("/api/booking/service-centers/{serviceCenterId}/slots")
    Call<TimeSlotsResponse> getTimeSlots(
        @Header("Authorization") String token,
        @Path("serviceCenterId") String serviceCenterId,
        @Query("date") String date
    );
    
    // ==================== Service Type Endpoints ====================
    @GET("/api/service-types/popular/list")
    Call<ServiceTypesResponse> getPopularServiceTypes();
    
    @GET("/api/booking/vehicles/{vehicleId}/services")
    Call<ServiceTypesResponse> getCompatibleServices(
        @Header("Authorization") String token,
        @Path("vehicleId") String vehicleId
    );
    
    @GET("/api/service-packages/vehicle/{vehicleId}/compatible")
    Call<ServicePackagesResponse> getCompatiblePackages(
        @Header("Authorization") String token,
        @Path("vehicleId") String vehicleId
    );
    
    // ==================== Service Center Details ====================
    @GET("/api/service-centers/{centerId}")
    Call<ServiceCenterDetailResponse> getServiceCenterDetail(
        @Path("centerId") String centerId
    );
    
    // ==================== Payment Endpoints ====================
    @POST("/api/payment/booking/{appointmentId}")
    Call<PaymentResponse> createPayment(
        @Header("Authorization") String token,
        @Path("appointmentId") String appointmentId,
        @Body PaymentRequest request
    );
    
    @GET("/api/payment/{paymentId}/status")
    Call<PaymentResponse> getPaymentStatus(
        @Header("Authorization") String token,
        @Path("paymentId") String paymentId
    );
    
    @GET("/api/payment/my-payments")
    Call<PaymentHistoryResponse> getMyPayments(
        @Header("Authorization") String token
    );
    
    @POST("/api/payment/{paymentId}/cancel")
    Call<PaymentResponse> cancelPayment(
        @Header("Authorization") String token,
        @Path("paymentId") String paymentId
    );
}
