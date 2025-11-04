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
import com.example.prm392_evcare.models.CreateVehicleRequest;
import com.example.prm392_evcare.models.Vehicle;
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

public class EditVehicleActivity extends AppCompatActivity {

    private Spinner spinnerVehicleModel;
    private TextInputLayout tilLicensePlate, tilColor, tilYear, tilMileage;
    private TextInputEditText etLicensePlate, etColor, etYear, etMileage;
    private MaterialButton btnUpdateVehicle;
    private View loadingView;

    private ApiService apiService;
    private SessionManager sessionManager;
    private List<VehicleModel> vehicleModels;
    private Vehicle vehicle;
    private Dialog progressDialog;
    private Dialog statusDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_vehicle);

        // Get vehicle from intent
        vehicle = (Vehicle) getIntent().getSerializableExtra("vehicle");
        if (vehicle == null) {
            Toast.makeText(this, "Lỗi: Không tìm thấy thông tin xe", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize API service and session manager
        apiService = ApiClient.getClient().create(ApiService.class);
        sessionManager = new SessionManager(this);

        // Setup toolbar
        setupToolbar();

        // Initialize views
        initViews();

        // Load vehicle models
        loadVehicleModels();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Chỉnh sửa xe");
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void initViews() {
        spinnerVehicleModel = findViewById(R.id.spinnerVehicleModel);
        tilLicensePlate = findViewById(R.id.tilLicensePlate);
        tilColor = findViewById(R.id.tilColor);
        tilYear = findViewById(R.id.tilYear);
        tilMileage = findViewById(R.id.tilMileage);

        etLicensePlate = findViewById(R.id.etLicensePlate);
        etColor = findViewById(R.id.etColor);
        etYear = findViewById(R.id.etYear);
        etMileage = findViewById(R.id.etMileage);

        btnUpdateVehicle = findViewById(R.id.btnUpdateVehicle);
        loadingView = findViewById(R.id.loadingView);

        vehicleModels = new ArrayList<>();

        // Pre-fill data
        etLicensePlate.setText(vehicle.getLicensePlate());
        etColor.setText(vehicle.getColor());
        etYear.setText(String.valueOf(vehicle.getYear()));
        etMileage.setText(String.valueOf(vehicle.getMileage()));

        btnUpdateVehicle.setOnClickListener(v -> attemptUpdateVehicle());
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
                        vehicleModels = modelsResponse.getVehicleModels();
                        setupVehicleModelSpinner();
                    } else {
                        Toast.makeText(EditVehicleActivity.this,
                                "Không thể tải danh sách xe", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(EditVehicleActivity.this,
                            "Lỗi khi tải danh sách xe", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<VehicleModelsResponse> call, Throwable t) {
                loadingView.setVisibility(View.GONE);
                Toast.makeText(EditVehicleActivity.this,
                        "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupVehicleModelSpinner() {
        List<String> modelNames = new ArrayList<>();
        int selectedPosition = 0;

        for (int i = 0; i < vehicleModels.size(); i++) {
            VehicleModel model = vehicleModels.get(i);
            modelNames.add(model.getFullName());

            // Find current vehicle model
            if (vehicle.getVehicleModel() != null &&
                    model.getId().equals(vehicle.getVehicleModel().getId())) {
                selectedPosition = i;
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                modelNames
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerVehicleModel.setAdapter(adapter);
        spinnerVehicleModel.setSelection(selectedPosition);
    }

    private void attemptUpdateVehicle() {
        // Reset errors
        resetErrors();

        // Get values
        int modelPosition = spinnerVehicleModel.getSelectedItemPosition();
        String licensePlate = etLicensePlate.getText().toString().trim();
        String color = etColor.getText().toString().trim();
        String yearStr = etYear.getText().toString().trim();
        String mileageStr = etMileage.getText().toString().trim();

        // Validate inputs
        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(mileageStr)) {
            tilMileage.setError("Vui lòng nhập số km đã đi");
            focusView = etMileage;
            cancel = true;
        }

        if (TextUtils.isEmpty(yearStr)) {
            tilYear.setError("Vui lòng nhập năm sản xuất");
            focusView = etYear;
            cancel = true;
        } else {
            try {
                int year = Integer.parseInt(yearStr);
                if (year < 2000 || year > 2025) {
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
            VehicleModel selectedModel = vehicleModels.get(modelPosition);
            int year = Integer.parseInt(yearStr);
            int mileage = Integer.parseInt(mileageStr);

            showProgress(true);
            performUpdateVehicle(selectedModel.getId(), licensePlate, color, year, mileage);
        }
    }

    private void performUpdateVehicle(String vehicleModelId, String licensePlate, String color,
                                       int year, int mileage) {
        CreateVehicleRequest request = new CreateVehicleRequest(
                vehicleModelId, licensePlate, color, year, mileage
        );

        String token = "Bearer " + sessionManager.getAuthToken();

        Call<VehicleResponse> call = apiService.updateVehicle(token, vehicle.getId(), request);
        call.enqueue(new Callback<VehicleResponse>() {
            @Override
            public void onResponse(Call<VehicleResponse> call, Response<VehicleResponse> response) {
                showProgress(false);

                if (response.isSuccessful() && response.body() != null) {
                    VehicleResponse vehicleResponse = response.body();

                    if (vehicleResponse.isSuccess()) {
                        showStatusDialog(true, "Thành công",
                                "Đã cập nhật thông tin xe thành công!");
                    } else {
                        showStatusDialog(false, "Thất bại",
                                vehicleResponse.getMessage() != null ?
                                        vehicleResponse.getMessage() : "Không thể cập nhật xe");
                    }
                } else {
                    showStatusDialog(false, "Thất bại",
                            "Không thể cập nhật xe. Vui lòng thử lại.");
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
        tilMileage.setError(null);
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
        btnUpdateVehicle.setEnabled(!show);
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
