package com.example.prm392_evcare;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.prm392_evcare.adapters.VehicleAdapter;
import com.example.prm392_evcare.api.ApiClient;
import com.example.prm392_evcare.api.ApiService;
import com.example.prm392_evcare.models.Vehicle;
import com.example.prm392_evcare.models.VehicleResponse;
import com.example.prm392_evcare.utils.SessionManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ManageVehiclesActivity extends AppCompatActivity implements VehicleAdapter.OnVehicleClickListener {

    private RecyclerView rvVehicles;
    private VehicleAdapter vehicleAdapter;
    private List<Vehicle> vehicleList;
    private FloatingActionButton fabAddVehicle;
    private View emptyStateView;
    private View loadingView;

    private ApiService apiService;
    private SessionManager sessionManager;
    private Dialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_vehicles);

        // Initialize API service and session manager
        apiService = ApiClient.getClient().create(ApiService.class);
        sessionManager = new SessionManager(this);

        // Setup toolbar
        setupToolbar();

        // Initialize views
        initViews();

        // Load vehicles
        loadVehicles();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Quản lý xe");
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void initViews() {
        rvVehicles = findViewById(R.id.rvVehicles);
        fabAddVehicle = findViewById(R.id.fabAddVehicle);
        emptyStateView = findViewById(R.id.emptyStateView);
        loadingView = findViewById(R.id.loadingView);

        // Setup RecyclerView
        vehicleList = new ArrayList<>();
        vehicleAdapter = new VehicleAdapter(this, vehicleList, this);
        rvVehicles.setLayoutManager(new LinearLayoutManager(this));
        rvVehicles.setAdapter(vehicleAdapter);

        // FAB click listener
        fabAddVehicle.setOnClickListener(v -> {
            Intent intent = new Intent(ManageVehiclesActivity.this, AddVehicleActivity.class);
            startActivity(intent);
        });
    }

    private void loadVehicles() {
        showLoading(true);
        String token = "Bearer " + sessionManager.getAuthToken();

        Call<VehicleResponse> call = apiService.getMyVehicles(token);
        call.enqueue(new Callback<VehicleResponse>() {
            @Override
            public void onResponse(Call<VehicleResponse> call, Response<VehicleResponse> response) {
                showLoading(false);

                if (response.isSuccessful() && response.body() != null) {
                    VehicleResponse vehicleResponse = response.body();

                    if (vehicleResponse.isSuccess() && vehicleResponse.getVehicles() != null) {
                        vehicleList.clear();
                        vehicleList.addAll(vehicleResponse.getVehicles());
                        vehicleAdapter.notifyDataSetChanged();

                        // Show/hide empty state
                        if (vehicleList.isEmpty()) {
                            showEmptyState(true);
                        } else {
                            showEmptyState(false);
                        }
                    } else {
                        showEmptyState(true);
                    }
                } else {
                    Toast.makeText(ManageVehiclesActivity.this,
                            "Không thể tải danh sách xe", Toast.LENGTH_SHORT).show();
                    showEmptyState(true);
                }
            }

            @Override
            public void onFailure(Call<VehicleResponse> call, Throwable t) {
                showLoading(false);
                Toast.makeText(ManageVehiclesActivity.this,
                        "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                showEmptyState(true);
            }
        });
    }

    @Override
    public void onVehicleClick(Vehicle vehicle) {
        // Navigate to service centers to book maintenance
        Intent intent = new Intent(this, ServiceCentersActivity.class);
        intent.putExtra("vehicle", vehicle);
        intent.putExtra("vehicle_id", vehicle.getId());
        startActivity(intent);
    }

    @Override
    public void onVehicleEdit(Vehicle vehicle) {
        Intent intent = new Intent(this, EditVehicleActivity.class);
        intent.putExtra("vehicle", vehicle);
        startActivity(intent);
    }

    @Override
    public void onVehicleDelete(Vehicle vehicle) {
        new AlertDialog.Builder(this)
                .setTitle("Xóa xe")
                .setMessage("Bạn có chắc chắn muốn xóa xe " + vehicle.getLicensePlate() + "?")
                .setPositiveButton("Xóa", (dialog, which) -> deleteVehicle(vehicle))
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void deleteVehicle(Vehicle vehicle) {
        showProgressDialog();
        String token = "Bearer " + sessionManager.getAuthToken();

        Call<VehicleResponse> call = apiService.deleteVehicle(token, vehicle.getId());
        call.enqueue(new Callback<VehicleResponse>() {
            @Override
            public void onResponse(Call<VehicleResponse> call, Response<VehicleResponse> response) {
                hideProgressDialog();

                if (response.isSuccessful() && response.body() != null) {
                    VehicleResponse vehicleResponse = response.body();

                    if (vehicleResponse.isSuccess()) {
                        Toast.makeText(ManageVehiclesActivity.this,
                                "Đã xóa xe thành công", Toast.LENGTH_SHORT).show();
                        // Reload vehicles
                        loadVehicles();
                    } else {
                        Toast.makeText(ManageVehiclesActivity.this,
                                "Không thể xóa xe: " + vehicleResponse.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ManageVehiclesActivity.this,
                            "Không thể xóa xe", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<VehicleResponse> call, Throwable t) {
                hideProgressDialog();
                Toast.makeText(ManageVehiclesActivity.this,
                        "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showLoading(boolean show) {
        loadingView.setVisibility(show ? View.VISIBLE : View.GONE);
        rvVehicles.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    private void showEmptyState(boolean show) {
        emptyStateView.setVisibility(show ? View.VISIBLE : View.GONE);
        rvVehicles.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new Dialog(this);
            progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            progressDialog.setContentView(R.layout.dialog_login_progress);
            progressDialog.setCancelable(false);
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        progressDialog.show();
    }

    private void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload vehicles when returning to this activity
        loadVehicles();
    }
}
