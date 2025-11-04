package com.example.prm392_evcare;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392_evcare.adapters.ServiceCenterAdapter;
import com.example.prm392_evcare.api.ApiClient;
import com.example.prm392_evcare.api.ApiService;
import com.example.prm392_evcare.models.NearbyServiceCentersResponse;
import com.example.prm392_evcare.models.ServiceCenter;
import com.example.prm392_evcare.models.Vehicle;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ServiceCentersActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView tvMessage;
    private ServiceCenterAdapter adapter;
    private ApiService apiService;
    private Vehicle vehicle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_centers);

        // Get vehicle from intent
        vehicle = (Vehicle) getIntent().getSerializableExtra("vehicle");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        toolbar.setTitle("");

        recyclerView = findViewById(R.id.recyclerViewAllCenters);
        progressBar = findViewById(R.id.progressBarAllCenters);
        tvMessage = findViewById(R.id.tvAllCentersMessage);

        adapter = new ServiceCenterAdapter(new ArrayList<>(), this, serviceCenter -> {
            // Navigate to SelectServiceActivity
            Intent intent = new Intent(ServiceCentersActivity.this, SelectServiceActivity.class);
            if (vehicle != null) {
                intent.putExtra("vehicle", vehicle);
            }
            intent.putExtra("service_center_id", serviceCenter.getId());
            intent.putExtra("service_center_name", serviceCenter.getName());
            startActivity(intent);
        });
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setAdapter(adapter);

        apiService = ApiClient.getClient().create(ApiService.class);
        fetchAllCenters();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void fetchAllCenters() {
        showLoading(true);
        tvMessage.setVisibility(View.GONE);
        apiService.getServiceCenters(1, 1000).enqueue(new Callback<NearbyServiceCentersResponse>() {
            @Override
            public void onResponse(@NonNull Call<NearbyServiceCentersResponse> call, @NonNull Response<NearbyServiceCentersResponse> response) {
                showLoading(false);
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    List<ServiceCenter> centers = response.body().getData().getServiceCenters();
                    if (centers != null && !centers.isEmpty()) {
                        adapter.updateServiceCenters(centers);
                        recyclerView.setVisibility(View.VISIBLE);
                        tvMessage.setVisibility(View.GONE);
                    } else {
                        showEmpty();
                    }
                } else {
                    showEmpty();
                }
            }

            @Override
            public void onFailure(@NonNull Call<NearbyServiceCentersResponse> call, @NonNull Throwable t) {
                showEmpty();
            }
        });
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    private void showEmpty() {
        adapter.updateServiceCenters(new ArrayList<>());
        recyclerView.setVisibility(View.GONE);
        tvMessage.setVisibility(View.VISIBLE);
        tvMessage.setText("Không có trung tâm nào để hiển thị.");
    }
}


