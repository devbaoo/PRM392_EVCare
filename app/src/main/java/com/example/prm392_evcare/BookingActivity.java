package com.example.prm392_evcare;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.prm392_evcare.api.ApiClient;
import com.example.prm392_evcare.api.ApiService;
import com.example.prm392_evcare.utils.SessionManager;
import com.google.android.material.button.MaterialButton;

public class BookingActivity extends AppCompatActivity {

    private ApiService apiService;
    private SessionManager sessionManager;
    private MaterialButton btnSelectVehicle;
    private MaterialButton btnSelectServiceCenter;
    private MaterialButton btnViewBookingHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        // Initialize API service and session manager
        apiService = ApiClient.getClient().create(ApiService.class);
        sessionManager = new SessionManager(this);

        // Setup toolbar
        setupToolbar();

        // Initialize views
        initViews();

        // Setup click listeners
        setupClickListeners();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Đặt lịch bảo dưỡng");
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void initViews() {
        btnSelectVehicle = findViewById(R.id.btnSelectVehicle);
        btnSelectServiceCenter = findViewById(R.id.btnSelectServiceCenter);
        btnViewBookingHistory = findViewById(R.id.btnViewBookingHistory);
    }

    private void setupClickListeners() {
        btnSelectVehicle.setOnClickListener(v -> {
            // Navigate to ManageVehiclesActivity
            Intent intent = new Intent(BookingActivity.this, ManageVehiclesActivity.class);
            startActivity(intent);
        });

        btnSelectServiceCenter.setOnClickListener(v -> {
            // Navigate to ServiceCentersActivity
            Intent intent = new Intent(BookingActivity.this, ServiceCentersActivity.class);
            startActivity(intent);
        });

        btnViewBookingHistory.setOnClickListener(v -> {
            // Navigate to BookingHistoryActivity
            Intent intent = new Intent(BookingActivity.this, BookingHistoryActivity.class);
            startActivity(intent);
        });
    }
}
