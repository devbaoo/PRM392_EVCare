package com.example.prm392_evcare;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.prm392_evcare.api.ApiClient;
import com.example.prm392_evcare.api.ApiService;
import com.example.prm392_evcare.models.BookingResponse;
import com.example.prm392_evcare.utils.SessionManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentResultActivity extends AppCompatActivity {

    private ApiService apiService;
    private SessionManager sessionManager;
    private String bookingId;
    private String paymentStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Initialize
        apiService = ApiClient.getClient().create(ApiService.class);
        sessionManager = new SessionManager(this);

        // Get data from deep link
        Intent intent = getIntent();
        Uri data = intent.getData();
        
        if (data != null) {
            // Parse query parameters from PayOS callback
            // Expected format: evcare://payment/result?bookingId=xxx&status=success
            bookingId = data.getQueryParameter("bookingId");
            paymentStatus = data.getQueryParameter("status");
            String orderCode = data.getQueryParameter("orderCode");
            
            android.util.Log.d("PaymentResult", "Booking ID: " + bookingId);
            android.util.Log.d("PaymentResult", "Payment Status: " + paymentStatus);
            android.util.Log.d("PaymentResult", "Order Code: " + orderCode);
            
            if (paymentStatus != null) {
                handlePaymentResult(paymentStatus);
            } else {
                // No status, fallback to outcome failed
                handlePaymentResult("failed");
            }
        } else {
            // No deep link data, fallback to outcome failed
            handlePaymentResult("failed");
        }
    }

    private void handlePaymentResult(String status) {
        // Always go straight to booking history regardless of status
        Intent i = new Intent(this, BookingHistoryActivity.class);
        i.putExtra("fromPaymentFlow", true);
        startActivity(i);
        finish();
    }
}
