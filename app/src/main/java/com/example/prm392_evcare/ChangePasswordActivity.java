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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.prm392_evcare.api.ApiClient;
import com.example.prm392_evcare.api.ApiService;
import com.example.prm392_evcare.models.ChangePasswordRequest;
import com.example.prm392_evcare.models.UserProfileResponse;
import com.example.prm392_evcare.utils.SessionManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangePasswordActivity extends AppCompatActivity {
    
    private TextInputLayout tilOldPassword, tilNewPassword, tilConfirmPassword;
    private TextInputEditText etOldPassword, etNewPassword, etConfirmPassword;
    private MaterialButton btnChangePassword;
    private ProgressBar progressBar;
    
    private ApiService apiService;
    private SessionManager sessionManager;
    
    private Dialog progressDialog;
    private Dialog statusDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        
        // Initialize API service and session manager
        apiService = ApiClient.getClient().create(ApiService.class);
        sessionManager = new SessionManager(this);
        
        // Setup toolbar
        setupToolbar();
        
        // Initialize views
        initViews();
        
        // Set click listeners
        btnChangePassword.setOnClickListener(v -> attemptChangePassword());
    }
    
    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Đổi mật khẩu");
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }
    
    private void initViews() {
        tilOldPassword = findViewById(R.id.tilOldPassword);
        tilNewPassword = findViewById(R.id.tilNewPassword);
        tilConfirmPassword = findViewById(R.id.tilConfirmPassword);
        
        etOldPassword = findViewById(R.id.etOldPassword);
        etNewPassword = findViewById(R.id.etNewPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        
        btnChangePassword = findViewById(R.id.btnChangePassword);
        progressBar = findViewById(R.id.progressBar);
    }
    
    private void attemptChangePassword() {
        // Reset errors
        resetErrors();
        
        // Get values
        String oldPassword = etOldPassword.getText().toString().trim();
        String newPassword = etNewPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();
        
        // Validate inputs
        boolean cancel = false;
        View focusView = null;
        
        // Check confirm password
        if (TextUtils.isEmpty(confirmPassword)) {
            tilConfirmPassword.setError("Vui lòng xác nhận mật khẩu mới");
            focusView = etConfirmPassword;
            cancel = true;
        } else if (!confirmPassword.equals(newPassword)) {
            tilConfirmPassword.setError("Mật khẩu xác nhận không khớp");
            focusView = etConfirmPassword;
            cancel = true;
        }
        
        // Check new password
        if (TextUtils.isEmpty(newPassword)) {
            tilNewPassword.setError("Vui lòng nhập mật khẩu mới");
            focusView = etNewPassword;
            cancel = true;
        } else if (newPassword.length() < 6) {
            tilNewPassword.setError("Mật khẩu phải có ít nhất 6 ký tự");
            focusView = etNewPassword;
            cancel = true;
        } else if (newPassword.equals(oldPassword)) {
            tilNewPassword.setError("Mật khẩu mới phải khác mật khẩu cũ");
            focusView = etNewPassword;
            cancel = true;
        }
        
        // Check old password
        if (TextUtils.isEmpty(oldPassword)) {
            tilOldPassword.setError("Vui lòng nhập mật khẩu hiện tại");
            focusView = etOldPassword;
            cancel = true;
        }
        
        if (cancel) {
            if (focusView != null) {
                focusView.requestFocus();
            }
        } else {
            showProgress(true);
            performChangePassword(oldPassword, newPassword);
        }
    }
    
    private void performChangePassword(String oldPassword, String newPassword) {
        ChangePasswordRequest request = new ChangePasswordRequest(oldPassword, newPassword);
        String token = "Bearer " + sessionManager.getAuthToken();
        
        Call<UserProfileResponse> call = apiService.changePassword(token, request);
        call.enqueue(new Callback<UserProfileResponse>() {
            @Override
            public void onResponse(Call<UserProfileResponse> call, Response<UserProfileResponse> response) {
                showProgress(false);
                
                if (response.isSuccessful() && response.body() != null) {
                    UserProfileResponse profileResponse = response.body();
                    
                    if (profileResponse.isSuccess()) {
                        // Show success dialog
                        showStatusDialog(true, "Thành công", 
                                "Mật khẩu đã được thay đổi thành công!");
                    } else {
                        // Show error dialog
                        showStatusDialog(false, "Thất bại", 
                                profileResponse.getMessage() != null ? 
                                profileResponse.getMessage() : "Không thể đổi mật khẩu");
                    }
                } else {
                    // Parse error response
                    String errorMessage = "Mật khẩu hiện tại không đúng";
                    if (response.code() == 401) {
                        errorMessage = "Mật khẩu hiện tại không đúng";
                    } else if (response.code() == 400) {
                        errorMessage = "Thông tin không hợp lệ";
                    }
                    showStatusDialog(false, "Thất bại", errorMessage);
                }
            }

            @Override
            public void onFailure(Call<UserProfileResponse> call, Throwable t) {
                showProgress(false);
                showStatusDialog(false, "Lỗi kết nối", 
                        "Không thể kết nối đến máy chủ: " + t.getMessage());
            }
        });
    }
    
    private void resetErrors() {
        tilOldPassword.setError(null);
        tilNewPassword.setError(null);
        tilConfirmPassword.setError(null);
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
                // Clear input fields
                etOldPassword.setText("");
                etNewPassword.setText("");
                etConfirmPassword.setText("");
                // Close activity
                finish();
            }
        });
        
        statusDialog.show();
        
        // Auto dismiss after 3 seconds if success
        if (isSuccess) {
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                if (statusDialog != null && statusDialog.isShowing()) {
                    statusDialog.dismiss();
                    finish();
                }
            }, 3000);
        }
    }
    
    private void showProgress(boolean show) {
        if (show) {
            showProgressDialog();
        } else {
            hideProgressDialog();
        }
        btnChangePassword.setEnabled(!show);
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
