package com.example.prm392_evcare;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392_evcare.adapters.ServiceTypeAdapter;
import com.example.prm392_evcare.api.ApiClient;
import com.example.prm392_evcare.api.ApiService;
import com.example.prm392_evcare.models.ServiceCenter;
import com.example.prm392_evcare.models.ServiceType;
import com.example.prm392_evcare.models.ServiceTypesResponse;
import com.example.prm392_evcare.models.Vehicle;
import com.example.prm392_evcare.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SelectServiceActivity extends AppCompatActivity {

    private RecyclerView rvServiceTypes;
    private ProgressBar progressBar;
    private TextView tvEmpty;
    private ServiceTypeAdapter adapter;
    
    private ApiService apiService;
    private SessionManager sessionManager;
    
    private Vehicle vehicle;
    private String serviceCenterId;
    private String serviceCenterName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_service);

        // Get data from intent
        vehicle = (Vehicle) getIntent().getSerializableExtra("vehicle");
        serviceCenterId = getIntent().getStringExtra("service_center_id");
        serviceCenterName = getIntent().getStringExtra("service_center_name");

        if (vehicle == null || serviceCenterId == null) {
            Toast.makeText(this, "Thiếu thông tin xe hoặc trung tâm", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize
        apiService = ApiClient.getClient().create(ApiService.class);
        sessionManager = new SessionManager(this);

        // Setup toolbar
        setupToolbar();

        // Initialize views
        initViews();

        // Load service types
        loadServiceTypes();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Chọn dịch vụ bảo dưỡng");
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void initViews() {
        rvServiceTypes = findViewById(R.id.rvServiceTypes);
        progressBar = findViewById(R.id.progressBar);
        tvEmpty = findViewById(R.id.tvEmpty);

        adapter = new ServiceTypeAdapter(new ArrayList<>(), serviceType -> {
            // Navigate to booking confirmation
            Intent intent = new Intent(SelectServiceActivity.this, ConfirmBookingActivity.class);
            intent.putExtra("vehicle", vehicle);
            intent.putExtra("service_center_id", serviceCenterId);
            intent.putExtra("service_center_name", serviceCenterName);
            intent.putExtra("service_type", serviceType);
            intent.putExtra("is_inspection_only", false);
            startActivity(intent);
        });

        rvServiceTypes.setLayoutManager(new LinearLayoutManager(this));
        rvServiceTypes.setAdapter(adapter);
        
        // Add "Inspection Only" option at the top
        addInspectionOnlyOption();
    }
    
    private void addInspectionOnlyOption() {
        // Create a card view for inspection only option
        View inspectionCard = getLayoutInflater().inflate(R.layout.item_inspection_only, 
                (android.view.ViewGroup) rvServiceTypes.getParent(), false);
        
        LinearLayout container = findViewById(R.id.serviceContainer);
        if (container != null) {
            container.addView(inspectionCard, 0);
            
            inspectionCard.setOnClickListener(v -> {
                Intent intent = new Intent(SelectServiceActivity.this, ConfirmBookingActivity.class);
                intent.putExtra("vehicle", vehicle);
                intent.putExtra("service_center_id", serviceCenterId);
                intent.putExtra("service_center_name", serviceCenterName);
                intent.putExtra("is_inspection_only", true);
                startActivity(intent);
            });
        }
    }

    private void loadServiceTypes() {
        showLoading(true);
        
        Call<ServiceTypesResponse> call = apiService.getPopularServiceTypes();
        
        call.enqueue(new Callback<ServiceTypesResponse>() {
            @Override
            public void onResponse(Call<ServiceTypesResponse> call, Response<ServiceTypesResponse> response) {
                showLoading(false);
                
                if (response.isSuccessful() && response.body() != null) {
                    ServiceTypesResponse serviceTypesResponse = response.body();
                    
                    if (serviceTypesResponse.isSuccess() && serviceTypesResponse.getServiceTypes() != null) {
                        List<ServiceType> serviceTypes = serviceTypesResponse.getServiceTypes();
                        
                        if (serviceTypes != null && !serviceTypes.isEmpty()) {
                            adapter.updateServiceTypes(serviceTypes);
                            showEmpty(false);
                        } else {
                            showEmpty(true);
                        }
                    } else {
                        Toast.makeText(SelectServiceActivity.this,
                                "Không thể tải danh sách dịch vụ", Toast.LENGTH_SHORT).show();
                        showEmpty(true);
                    }
                } else {
                    Toast.makeText(SelectServiceActivity.this,
                            "Lỗi khi tải dịch vụ", Toast.LENGTH_SHORT).show();
                    showEmpty(true);
                }
            }

            @Override
            public void onFailure(Call<ServiceTypesResponse> call, Throwable t) {
                showLoading(false);
                Toast.makeText(SelectServiceActivity.this,
                        "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                showEmpty(true);
            }
        });
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        rvServiceTypes.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    private void showEmpty(boolean show) {
        tvEmpty.setVisibility(show ? View.VISIBLE : View.GONE);
        rvServiceTypes.setVisibility(show ? View.GONE : View.VISIBLE);
    }
}
