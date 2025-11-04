package com.example.prm392_evcare;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392_evcare.adapters.FeatureAdapter;
import com.example.prm392_evcare.adapters.ServiceCenterAdapter;
import com.example.prm392_evcare.api.ApiClient;
import com.example.prm392_evcare.api.ApiService;
import com.example.prm392_evcare.models.ServiceCenter;
import com.example.prm392_evcare.models.NearbyServiceCentersResponse;
import com.example.prm392_evcare.models.User;
import com.example.prm392_evcare.utils.SessionManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity {
    
    private static final String TAG = "HomeActivity";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    
    private SessionManager sessionManager;
    private ApiService apiService;
    
    // Location
    private double currentLatitude = 10.762622; // Ho Chi Minh City default
    private double currentLongitude = 106.660172;
    private int searchRadius = 10; // km
    
    // Views
    private TextView tvServiceCenterLocation;
    private Spinner spinnerRadius;
    private RecyclerView recyclerViewServiceCenters;
    private RecyclerView recyclerViewFeatures;
    private ProgressBar progressBarServiceCenters;
    private TextView tvServiceCenterMessage;
    private MaterialButton btnBookService;
    
    // Adapters
    private ServiceCenterAdapter serviceCenterAdapter;
    private FeatureAdapter featureAdapter;
    
    private List<ServiceCenter> serviceCenters = new ArrayList<>();
    private Dialog errorDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Initialize session manager
        sessionManager = new SessionManager(this);
        apiService = ApiClient.getClient().create(ApiService.class);
        
        // Check if user is logged in
        if (!sessionManager.isLoggedIn()) {
            navigateToLogin();
            return;
        }
        
        // Set up toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        toolbar.setTitle("");
        toolbar.setSubtitle(null);
        
        // Initialize views
        initializeViews();
        
        // Set up UI
        setupAdapters();
        
        // Request location permission and get location
        if (checkLocationPermission()) {
            getCurrentLocation();
        } else {
            requestLocationPermission();
        }
        
        // Set up click listeners
        setupClickListeners();
        
        // Load features
        loadFeatures();

        // Setup bottom navigation
        setupBottomNavigation();
    }
    
    private void initializeViews() {
        tvServiceCenterLocation = findViewById(R.id.tvServiceCenterLocation);
        spinnerRadius = findViewById(R.id.spinnerRadius);
        recyclerViewServiceCenters = findViewById(R.id.recyclerViewServiceCenters);
        recyclerViewFeatures = findViewById(R.id.recyclerViewFeatures);
        progressBarServiceCenters = findViewById(R.id.progressBarServiceCenters);
        tvServiceCenterMessage = findViewById(R.id.tvServiceCenterMessage);
        btnBookService = findViewById(R.id.btnBookService);
        
        // Setup spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
            this,
            R.array.search_radius,
            android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRadius.setAdapter(adapter);
        // Default to 50km to surface more nearby results from API
        int defaultIndex = adapter.getCount() - 1; // assumes last item is largest radius
        spinnerRadius.setSelection(defaultIndex);
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        if (bottomNavigationView == null) {
            return;
        }
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                return true; // already here
            } else if (itemId == R.id.nav_nearby) {
                Toast.makeText(this, "Tìm gần tôi (sắp ra mắt)", Toast.LENGTH_SHORT).show();
                return true;
            } else if (itemId == R.id.nav_bookings) {
                Intent intent = new Intent(HomeActivity.this, BookingHistoryActivity.class);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.nav_profile) {
                navigateToProfile();
                return true;
            }
            return false;
        });
        bottomNavigationView.setSelectedItemId(R.id.nav_home);
    }
    
    private void setupAdapters() {
        // Service Centers Adapter
        serviceCenterAdapter = new ServiceCenterAdapter(
            serviceCenters,
            this,
            serviceCenter -> {
                Toast.makeText(this, "Selected: " + serviceCenter.getName(), Toast.LENGTH_SHORT).show();
                // Navigate to service center details
            }
        );
        
        recyclerViewServiceCenters.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewServiceCenters.setAdapter(serviceCenterAdapter);
        
        // Features Adapter
        featureAdapter = new FeatureAdapter();
        recyclerViewFeatures.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewFeatures.setAdapter(featureAdapter);
    }
    
    private void setupClickListeners() {
        btnBookService.setOnClickListener(v -> {
            // Navigate to BookingActivity
            Intent intent = new Intent(HomeActivity.this, BookingActivity.class);
            startActivity(intent);
        });
        
        spinnerRadius.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                String[] radii = getResources().getStringArray(R.array.search_radius);
                String selected = radii[position];
                searchRadius = Integer.parseInt(selected.replace(" km", ""));
                fetchNearbyServiceCenters();
            }
            
            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
            }
        });
        
        findViewById(R.id.tvViewAll).setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, ServiceCentersActivity.class);
            startActivity(intent);
        });
    }
    
    private boolean checkLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == 
                   PackageManager.PERMISSION_GRANTED;
        }
        return true;
    }
    
    private void requestLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                LOCATION_PERMISSION_REQUEST_CODE
            );
        }
    }
    
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            } else {
                showLocationPermissionDenied();
            }
        }
    }
    
    @SuppressLint("MissingPermission")
    private void getCurrentLocation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && 
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        
        android.location.LocationManager locationManager = 
            (android.location.LocationManager) getSystemService(android.content.Context.LOCATION_SERVICE);
        
        Location location = null;
        if (locationManager != null) {
            if (locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
                location = locationManager.getLastKnownLocation(android.location.LocationManager.GPS_PROVIDER);
            }
            if (location == null) {
                location = locationManager.getLastKnownLocation(android.location.LocationManager.NETWORK_PROVIDER);
            }
        }
        
        if (location != null) {
            currentLatitude = location.getLatitude();
            currentLongitude = location.getLongitude();
            updateLocationDisplay();
            fetchNearbyServiceCenters();
        } else {
            showDefaultLocation();
        }
    }
    
    private void updateLocationDisplay() {
        tvServiceCenterLocation.setText(
            String.format("Near: %.4f, %.4f", currentLatitude, currentLongitude)
        );
    }
    
    private void showDefaultLocation() {
        tvServiceCenterLocation.setText("Using default location (Ho Chi Minh City)");
        fetchNearbyServiceCenters();
    }
    
    private void showLocationPermissionDenied() {
        tvServiceCenterLocation.setText("Location permission denied. Using default location (Ho Chi Minh City)");
        Snackbar.make(findViewById(R.id.home_container), 
                     "Location permission is needed to show nearby service centers", 
                     Snackbar.LENGTH_LONG)
                .show();
    }
    
    private void fetchNearbyServiceCenters() {
        showLoading(true);
        tvServiceCenterMessage.setVisibility(View.GONE);
        // Clear current items to avoid showing stale placeholders
        if (serviceCenterAdapter != null) {
            serviceCenterAdapter.updateServiceCenters(new ArrayList<>());
        }
        
        Log.d(TAG, "Fetching centers lat=" + currentLatitude + ", lng=" + currentLongitude + ", radius=" + searchRadius);
        apiService.getNearbyServiceCenters(currentLatitude, currentLongitude, searchRadius)
                .enqueue(new Callback<NearbyServiceCentersResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<NearbyServiceCentersResponse> call, @NonNull Response<NearbyServiceCentersResponse> response) {
                        showLoading(false);
                        
                        if (response.isSuccessful() && response.body() != null) {
                            NearbyServiceCentersResponse apiResponse = response.body();
                            Log.d(TAG, "API success=" + apiResponse.isSuccess());
                            if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                                List<ServiceCenter> centers = apiResponse.getData().getServiceCenters();
                                Log.d(TAG, "Received centers count=" + (centers == null ? 0 : centers.size()));
                                
                                if (centers != null && !centers.isEmpty()) {
                                    // Calculate distances
                                    for (ServiceCenter center : centers) {
                                        if (center.getAddress() != null && center.getAddress().getCoordinates() != null) {
                                            double distance = calculateDistance(
                                                currentLatitude, currentLongitude,
                                                center.getAddress().getCoordinates().getLat(),
                                                center.getAddress().getCoordinates().getLng()
                                            );
                                            center.setDistance(distance);
                                        }
                                    }
                                    
                                    // Limit to 4 service centers for homepage
                                    serviceCenters = centers.size() > 4 ? centers.subList(0, 4) : centers;
                                    serviceCenterAdapter.updateServiceCenters(serviceCenters);
                                    
                                    if (serviceCenters.isEmpty()) {
                                        showEmptyState();
                                    }
                                } else {
                                    showEmptyState();
                                }
                            } else {
                                showError("Không thể tải danh sách trung tâm dịch vụ.");
                            }
                        } else {
                            String errorMsg = "Không thể tải danh sách trung tâm dịch vụ.";
                            if (response.errorBody() != null) {
                                try {
                                    errorMsg = response.errorBody().string();
                                } catch (Exception e) {
                                    Log.e(TAG, "Error parsing error body", e);
                                }
                            }
                            showError(errorMsg);
                        }
                    }
                    
                    @Override
                    public void onFailure(@NonNull Call<NearbyServiceCentersResponse> call, @NonNull Throwable t) {
                        showLoading(false);
                        Log.e(TAG, "Error fetching service centers", t);
                        showError("Không thể tải danh sách trung tâm. Vui lòng kiểm tra kết nối mạng.");
                        // Show error dialog
                        runOnUiThread(() -> showErrorDialog("Lỗi kết nối", 
                            "Không thể kết nối đến máy chủ. Vui lòng thử lại sau."));
                    }
                });
    }
    
    private double calculateDistance(double lat1, double lng1, double lat2, double lng2) {
        double earthRadius = 6371; // km
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                   Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                   Math.sin(dLng / 2) * Math.sin(dLng / 2);
        
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return earthRadius * c;
    }
    
    private void showLoading(boolean show) {
        progressBarServiceCenters.setVisibility(show ? View.VISIBLE : View.GONE);
        recyclerViewServiceCenters.setVisibility(show ? View.GONE : View.VISIBLE);
    }
    
    private void showError(String message) {
        // Only show the message in TextView
        tvServiceCenterMessage.setText(message);
        tvServiceCenterMessage.setVisibility(View.VISIBLE);
        recyclerViewServiceCenters.setVisibility(View.GONE);
        if (serviceCenterAdapter != null) {
            serviceCenterAdapter.updateServiceCenters(new ArrayList<>());
        }
    }
    
    private void showErrorDialog(String title, String message) {
        if (errorDialog != null && errorDialog.isShowing()) {
            errorDialog.dismiss();
        }
        
        errorDialog = new Dialog(this);
        errorDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        errorDialog.setContentView(R.layout.dialog_login_status);
        errorDialog.setCancelable(true);
        errorDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        
        ImageView ivStatus = errorDialog.findViewById(R.id.ivStatus);
        TextView tvStatus = errorDialog.findViewById(R.id.tvStatus);
        TextView tvMessage = errorDialog.findViewById(R.id.tvMessage);
        MaterialButton btnOk = errorDialog.findViewById(R.id.btnOk);
        
        ivStatus.setImageResource(R.drawable.ic_error);
        ivStatus.setColorFilter(getResources().getColor(R.color.error));
        
        tvStatus.setText(title);
        tvMessage.setText(message);
        
        btnOk.setOnClickListener(v -> {
            errorDialog.dismiss();
        });
        
        errorDialog.show();
    }
    
    private void showEmptyState() {
        String message = String.format("Không có trung tâm nào gần bạn trong bán kính %d km. Hãy thử tăng bán kính hoặc chọn vị trí khác.", searchRadius);
        tvServiceCenterMessage.setText(message);
        tvServiceCenterMessage.setVisibility(View.VISIBLE);
        recyclerViewServiceCenters.setVisibility(View.GONE);
        if (serviceCenterAdapter != null) {
            serviceCenterAdapter.updateServiceCenters(new ArrayList<>());
        }
    }
    
    private void loadFeatures() {
        // Features are loaded automatically by the adapter
        featureAdapter.notifyDataSetChanged();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    // Logout method removed - use Profile screen for logout
    
    private void navigateToLogin() {
        Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
    
    private void navigateToProfile() {
        Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
        startActivity(intent);
    }
}
