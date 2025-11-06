package com.example.prm392_evcare;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.prm392_evcare.api.ApiClient;
import com.example.prm392_evcare.api.ApiService;
import com.example.prm392_evcare.models.BrandResponse;
import com.example.prm392_evcare.models.CreateVehicleRequest;
import com.example.prm392_evcare.models.VehicleModel;
import com.example.prm392_evcare.models.VehicleModelsResponse;
import com.example.prm392_evcare.models.VehicleResponse;
import com.example.prm392_evcare.utils.SessionManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddVehicleActivity extends AppCompatActivity {

    private Spinner spinnerBrand;
    private Spinner spinnerVehicleModel;
    private TextInputLayout tilLicensePlate, tilColor, tilYear, tilBatteryCapacity;
    private TextInputEditText etLicensePlate, etColor, etYear, etBatteryCapacity;
    private MaterialButton btnAddVehicle;
    private View loadingView;

    private ApiService apiService;
    private SessionManager sessionManager;
    private List<String> brands;
    private List<VehicleModel> allVehicleModels;
    private List<VehicleModel> filteredVehicleModels;
    private Dialog progressDialog;
    private Dialog statusDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_vehicle);

        // Initialize API service and session manager
        apiService = ApiClient.getClient().create(ApiService.class);
        sessionManager = new SessionManager(this);

        // Setup toolbar
        setupToolbar();

        // Initialize views
        initViews();

        // Load brands first, then vehicle models
        loadBrands();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Thêm xe mới");
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void initViews() {
        spinnerBrand = findViewById(R.id.spinnerBrand);
        spinnerVehicleModel = findViewById(R.id.spinnerVehicleModel);
        tilLicensePlate = findViewById(R.id.tilLicensePlate);
        tilColor = findViewById(R.id.tilColor);
        tilYear = findViewById(R.id.tilYear);
        tilBatteryCapacity = findViewById(R.id.tilBatteryCapacity);

        etLicensePlate = findViewById(R.id.etLicensePlate);
        etColor = findViewById(R.id.etColor);
        etYear = findViewById(R.id.etYear);
        etBatteryCapacity = findViewById(R.id.etBatteryCapacity);

        btnAddVehicle = findViewById(R.id.btnAddVehicle);
        loadingView = findViewById(R.id.loadingView);

        brands = new ArrayList<>();
        allVehicleModels = new ArrayList<>();
        filteredVehicleModels = new ArrayList<>();

        btnAddVehicle.setOnClickListener(v -> attemptAddVehicle());
    }

    private void loadBrands() {
        loadingView.setVisibility(View.VISIBLE);

        Call<BrandResponse> call = apiService.getBrands();
        call.enqueue(new Callback<BrandResponse>() {
            @Override
            public void onResponse(Call<BrandResponse> call, Response<BrandResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    BrandResponse brandResponse = response.body();

                    if (brandResponse.isSuccess() && brandResponse.getData() != null) {
                        brands = brandResponse.getData();
                        setupBrandSpinner();
                        // Load vehicle models after brands are loaded
                        loadVehicleModels();
                    } else {
                        loadingView.setVisibility(View.GONE);
                        Toast.makeText(AddVehicleActivity.this,
                                "Không thể tải danh sách hãng xe", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    loadingView.setVisibility(View.GONE);
                    Toast.makeText(AddVehicleActivity.this,
                            "Lỗi khi tải danh sách hãng xe", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BrandResponse> call, Throwable t) {
                loadingView.setVisibility(View.GONE);
                Toast.makeText(AddVehicleActivity.this,
                        "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupBrandSpinner() {
        // Add "Tất cả" option at the beginning
        List<String> brandOptions = new ArrayList<>();
        brandOptions.add("Tất cả hãng xe");
        brandOptions.addAll(brands);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                brandOptions
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBrand.setAdapter(adapter);

        // Set listener to filter vehicle models when brand is selected
        spinnerBrand.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                filterVehicleModelsByBrand(position);
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
                // Do nothing
            }
        });
    }

    private void filterVehicleModelsByBrand(int brandPosition) {
        if (allVehicleModels.isEmpty()) {
            return;
        }

        filteredVehicleModels.clear();

        if (brandPosition == 0) {
            // "Tất cả" selected - show all models
            filteredVehicleModels.addAll(allVehicleModels);
        } else {
            // Filter by selected brand
            String selectedBrand = brands.get(brandPosition - 1); // -1 because of "Tất cả" at index 0
            for (VehicleModel model : allVehicleModels) {
                if (model.getBrand().equals(selectedBrand)) {
                    filteredVehicleModels.add(model);
                }
            }
        }

        setupVehicleModelSpinner();
    }

    private void loadVehicleModels() {
        loadingView.setVisibility(View.VISIBLE);

        Call<VehicleModelsResponse> call = apiService.getVehicleModels();
        call.enqueue(new Callback<VehicleModelsResponse>() {
            @Override
            public void onResponse(Call<VehicleModelsResponse> call, Response<VehicleModelsResponse> response) {
                loadingView.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    VehicleModelsResponse modelsResponse = response.body();

                    if (modelsResponse.isSuccess() && modelsResponse.getVehicleModels() != null) {
                        allVehicleModels = modelsResponse.getVehicleModels();
                        filteredVehicleModels.addAll(allVehicleModels); // Initially show all
                        setupVehicleModelSpinner();
                    } else {
                        Toast.makeText(AddVehicleActivity.this,
                                "Không thể tải danh sách xe", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(AddVehicleActivity.this,
                            "Lỗi khi tải danh sách xe", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<VehicleModelsResponse> call, Throwable t) {
                loadingView.setVisibility(View.GONE);
                Toast.makeText(AddVehicleActivity.this,
                        "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupVehicleModelSpinner() {
        List<String> modelNames = new ArrayList<>();
        for (VehicleModel model : filteredVehicleModels) {
            modelNames.add(model.getFullName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                modelNames
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerVehicleModel.setAdapter(adapter);
    }

    private void attemptAddVehicle() {
        // Reset errors
        resetErrors();

        // Get values
        int modelPosition = spinnerVehicleModel.getSelectedItemPosition();
        String licensePlate = etLicensePlate.getText().toString().trim();
        String color = etColor.getText().toString().trim();
        String yearStr = etYear.getText().toString().trim();
        String batteryCapacityStr = etBatteryCapacity.getText().toString().trim();

        // Validate inputs
        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(batteryCapacityStr)) {
            tilBatteryCapacity.setError("Vui lòng nhập dung lượng pin");
            focusView = etBatteryCapacity;
            cancel = true;
        }

        if (TextUtils.isEmpty(yearStr)) {
            tilYear.setError("Vui lòng nhập năm sản xuất");
            focusView = etYear;
            cancel = true;
        } else {
            try {
                int year = Integer.parseInt(yearStr);
                if (year < 2000 || year > 2030) {
                    tilYear.setError("Năm sản xuất không hợp lệ");
                    focusView = etYear;
                    cancel = true;
                }
            } catch (NumberFormatException e) {
                tilYear.setError("Năm sản xuất phải là số");
                focusView = etYear;
                cancel = true;
            }
        }

        if (TextUtils.isEmpty(color)) {
            tilColor.setError("Vui lòng nhập màu xe");
            focusView = etColor;
            cancel = true;
        }

        if (TextUtils.isEmpty(licensePlate)) {
            tilLicensePlate.setError("Vui lòng nhập biển số xe");
            focusView = etLicensePlate;
            cancel = true;
        }

        if (modelPosition < 0) {
            Toast.makeText(this, "Vui lòng chọn dòng xe", Toast.LENGTH_SHORT).show();
            cancel = true;
        }

        if (cancel) {
            if (focusView != null) {
                focusView.requestFocus();
            }
        } else {
            VehicleModel selectedModel = filteredVehicleModels.get(modelPosition);
            int year = Integer.parseInt(yearStr);

            showProgress(true);
            performAddVehicle(selectedModel, licensePlate, color, year, batteryCapacityStr);
        }
    }

    private void performAddVehicle(VehicleModel model, String licensePlate, String color,
                                    int year, String batteryCapacity) {
        // Create VehicleInfo with all required fields from selected model
        CreateVehicleRequest.VehicleInfo vehicleInfo = new CreateVehicleRequest.VehicleInfo(
                model.getBrand(),
                model.getModelName(),
                year,
                model.getBatteryType(),
                licensePlate,
                color,
                batteryCapacity
        );

        CreateVehicleRequest request = new CreateVehicleRequest(vehicleInfo);

        String token = "Bearer " + sessionManager.getAuthToken();

        Call<VehicleResponse> call = apiService.createVehicle(token, request);
        call.enqueue(new Callback<VehicleResponse>() {
            @Override
            public void onResponse(Call<VehicleResponse> call, Response<VehicleResponse> response) {
                showProgress(false);

                if (response.isSuccessful() && response.body() != null) {
                    VehicleResponse vehicleResponse = response.body();

                    if (vehicleResponse.isSuccess()) {
                        showStatusDialog(true, "Thành công",
                                "Đã thêm xe mới thành công!");
                    } else {
                        showStatusDialog(false, "Thất bại",
                                vehicleResponse.getMessage() != null ?
                                        vehicleResponse.getMessage() : "Không thể thêm xe");
                    }
                } else {
                    showStatusDialog(false, "Thất bại",
                            "Không thể thêm xe. Vui lòng thử lại.");
                }
            }

            @Override
            public void onFailure(Call<VehicleResponse> call, Throwable t) {
                showProgress(false);
                showStatusDialog(false, "Lỗi kết nối",
                        "Không thể kết nối đến máy chủ: " + t.getMessage());
            }
        });
    }

    private void resetErrors() {
        tilLicensePlate.setError(null);
        tilColor.setError(null);
        tilYear.setError(null);
        tilBatteryCapacity.setError(null);
    }

    private void showStatusDialog(boolean isSuccess, String status, String message) {
        if (statusDialog != null && statusDialog.isShowing()) {
            statusDialog.dismiss();
        }

        statusDialog = new Dialog(this);
        statusDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        statusDialog.setContentView(R.layout.dialog_login_status);
        statusDialog.setCancelable(false);
        statusDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        ImageView ivStatus = statusDialog.findViewById(R.id.ivStatus);
        TextView tvStatus = statusDialog.findViewById(R.id.tvStatus);
        TextView tvMessage = statusDialog.findViewById(R.id.tvMessage);
        MaterialButton btnOk = statusDialog.findViewById(R.id.btnOk);

        if (isSuccess) {
            ivStatus.setImageResource(R.drawable.ic_check_circle);
            ivStatus.setColorFilter(getResources().getColor(R.color.success_color));
            tvStatus.setTextColor(getResources().getColor(R.color.success_color));
        } else {
            ivStatus.setImageResource(R.drawable.ic_error);
            ivStatus.setColorFilter(getResources().getColor(R.color.error_color));
            tvStatus.setTextColor(getResources().getColor(R.color.error_color));
        }

        tvStatus.setText(status);
        tvMessage.setText(message);

        btnOk.setOnClickListener(v -> {
            statusDialog.dismiss();
            if (isSuccess) {
                finish();
            }
        });

        statusDialog.show();

        if (isSuccess) {
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                if (statusDialog != null && statusDialog.isShowing()) {
                    statusDialog.dismiss();
                    finish();
                }
            }, 2000);
        }
    }

    private void showProgress(boolean show) {
        if (show) {
            showProgressDialog();
        } else {
            hideProgressDialog();
        }
        btnAddVehicle.setEnabled(!show);
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
}
