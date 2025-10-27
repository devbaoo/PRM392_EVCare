package com.example.prm392_evcare;

import android.app.Dialog;
import android.content.Intent;
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

import com.airbnb.lottie.LottieAnimationView;
import com.example.prm392_evcare.api.ApiClient;
import com.example.prm392_evcare.api.ApiService;
import com.example.prm392_evcare.models.LoginResponse;
import com.example.prm392_evcare.models.RegisterRequest;
import com.example.prm392_evcare.utils.SessionManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private TextInputLayout tilUsername, tilFullName, tilEmail, tilPhone, tilAddress, tilPassword;
    private TextInputEditText etUsername, etFullName, etEmail, etPhone, etAddress, etPassword;
    private MaterialButton btnRegister;
    private TextView tvLogin;
    private ProgressBar progressBar;
    private LottieAnimationView animationView;
    private ApiService apiService;
    private SessionManager sessionManager;
    
    private Dialog progressDialog;
    private Dialog statusDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize API service
        apiService = ApiClient.getClient().create(ApiService.class);
        
        // Initialize session manager
        sessionManager = new SessionManager(this);
        
        // Initialize views
        initViews();
        
        // Set animation
        animationView.setAnimation(R.raw.electric_car_animation);
        animationView.playAnimation();

        // Set click listeners
        btnRegister.setOnClickListener(v -> attemptRegister());
        tvLogin.setOnClickListener(v -> {
            finish();
        });
    }
    
    private void initViews() {
        tilUsername = findViewById(R.id.tilUsername);
        tilFullName = findViewById(R.id.tilFullName);
        tilEmail = findViewById(R.id.tilEmail);
        tilPhone = findViewById(R.id.tilPhone);
        tilAddress = findViewById(R.id.tilAddress);
        tilPassword = findViewById(R.id.tilPassword);
        
        etUsername = findViewById(R.id.etUsername);
        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etAddress = findViewById(R.id.etAddress);
        etPassword = findViewById(R.id.etPassword);
        
        btnRegister = findViewById(R.id.btnRegister);
        tvLogin = findViewById(R.id.tvLogin);
        progressBar = findViewById(R.id.progressBar);
        animationView = findViewById(R.id.animationView);
    }

    private void attemptRegister() {
        // Reset errors
        resetErrors();

        // Get values
        String username = etUsername.getText().toString().trim();
        String fullName = etFullName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Validate inputs
        boolean cancel = false;
        View focusView = null;

        // Validate password
        if (TextUtils.isEmpty(password)) {
            tilPassword.setError("Password is required");
            focusView = etPassword;
            cancel = true;
        } else if (password.length() < 6) {
            tilPassword.setError("Password must be at least 6 characters");
            focusView = etPassword;
            cancel = true;
        }
        
        // Validate address
        if (TextUtils.isEmpty(address)) {
            tilAddress.setError("Address is required");
            focusView = etAddress;
            cancel = true;
        }
        
        // Validate phone
        if (TextUtils.isEmpty(phone)) {
            tilPhone.setError("Phone number is required");
            focusView = etPhone;
            cancel = true;
        }

        // Validate email
        if (TextUtils.isEmpty(email)) {
            tilEmail.setError("Email is required");
            focusView = etEmail;
            cancel = true;
        } else if (!isEmailValid(email)) {
            tilEmail.setError("Enter a valid email address");
            focusView = etEmail;
            cancel = true;
        }
        
        // Validate full name
        if (TextUtils.isEmpty(fullName)) {
            tilFullName.setError("Full name is required");
            focusView = etFullName;
            cancel = true;
        }
        
        // Validate username
        if (TextUtils.isEmpty(username)) {
            tilUsername.setError("Username is required");
            focusView = etUsername;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            showProgress(true);
            performRegister(username, email, password, fullName, phone, address);
        }
    }
    
    private void resetErrors() {
        tilUsername.setError(null);
        tilFullName.setError(null);
        tilEmail.setError(null);
        tilPhone.setError(null);
        tilAddress.setError(null);
        tilPassword.setError(null);
    }

    private boolean isEmailValid(String email) {
        return email.contains("@") && email.contains(".");
    }

    private void performRegister(String username, String email, String password, String fullName, String phone, String address) {
        RegisterRequest registerRequest = new RegisterRequest(username, email, password, fullName, phone, address);
        
        Call<LoginResponse> call = apiService.register(registerRequest);
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                showProgress(false);
                
                if (response.isSuccessful()) {
                    LoginResponse loginResponse = response.body();
                    
                    // Check if the response indicates success
                    // Backend returns success=true when registration is successful
                    if (loginResponse != null && loginResponse.isSuccess() && 
                        loginResponse.getAccessToken() != null && 
                        loginResponse.getUser() != null) {
                        
                        // Check if user needs email verification
                        if (loginResponse.getUser().isVerified()) {
                            // User is verified, save session and navigate to main
                            sessionManager.saveAuthToken(loginResponse.getAccessToken());
                            sessionManager.saveRefreshToken(loginResponse.getRefreshToken());
                            sessionManager.saveUser(loginResponse.getUser());
                            sessionManager.setLoggedIn(true);
                            
                            // Show success dialog and navigate to main
                            showStatusDialog(true, "Đăng ký thành công", 
                                    "Chào mừng " + loginResponse.getUser().getFullName() + " đã tham gia EVCare!");
                        } else {
                            // User needs email verification
                            // Don't save session, just show message and navigate back to login
                            String message = loginResponse.getMessage() != null 
                                ? loginResponse.getMessage() 
                                : "Vui lòng xác thực tài khoản qua email trước khi đăng nhập.";
                            showStatusDialogWithCallback(true, "Đăng ký thành công", 
                                    message, () -> navigateToLogin());
                        }
                    } else {
                        // Show error dialog
                        String errorMessage = loginResponse != null && loginResponse.getMessage() != null 
                            ? loginResponse.getMessage() 
                            : "Đăng ký thất bại. Vui lòng thử lại.";
                        showStatusDialog(false, "Đăng ký thất bại", errorMessage);
                    }
                } else {
                    // Parse error response
                    try {
                        if (response.errorBody() != null) {
                            String errorBody = response.errorBody().string();
                            // Try to parse JSON to extract message
                            try {
                                JsonObject jsonObject = new JsonParser().parse(errorBody).getAsJsonObject();
                                if (jsonObject.has("message")) {
                                    String errorMessage = jsonObject.get("message").getAsString();
                                    showStatusDialog(false, "Đăng ký thất bại", errorMessage);
                                } else {
                                    showStatusDialog(false, "Đăng ký thất bại", 
                                            "Có lỗi xảy ra. Vui lòng thử lại sau.");
                                }
                            } catch (Exception e) {
                                // If parsing fails, just show the raw error
                                showStatusDialog(false, "Đăng ký thất bại", 
                                        "Có lỗi xảy ra. Vui lòng thử lại sau.");
                            }
                        } else {
                            showStatusDialog(false, "Đăng ký thất bại", 
                                    "Không thể kết nối đến máy chủ. Vui lòng thử lại sau.");
                        }
                    } catch (Exception e) {
                        showStatusDialog(false, "Đăng ký thất bại", 
                                "Không thể kết nối đến máy chủ. Vui lòng thử lại sau.");
                    }
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                showProgress(false);
                // Show network error dialog
                showStatusDialog(false, "Lỗi kết nối", 
                        "Không thể kết nối đến máy chủ: " + t.getMessage());
            }
        });
    }
    
    private void showStatusDialog(boolean isSuccess, String status, String message) {
        showStatusDialogWithCallback(isSuccess, status, message, null);
    }
    
    private void showStatusDialogWithCallback(boolean isSuccess, String status, String message, Runnable onDismiss) {
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
            ivStatus.setColorFilter(getResources().getColor(R.color.success));
        } else {
            ivStatus.setImageResource(R.drawable.ic_error);
            ivStatus.setColorFilter(getResources().getColor(R.color.error));
        }
        
        tvStatus.setText(status);
        tvMessage.setText(message);
        
        btnOk.setOnClickListener(v -> {
            statusDialog.dismiss();
            if (isSuccess) {
                if (onDismiss != null) {
                    // Execute custom callback
                    new Handler(Looper.getMainLooper()).postDelayed(onDismiss, 500);
                } else {
                    // Navigate to main activity with delay for better UX
                    new Handler(Looper.getMainLooper()).postDelayed(this::navigateToMain, 500);
                }
            }
        });
        
        statusDialog.show();
    }

    private void showProgress(boolean show) {
        if (show) {
            showProgressDialog();
        } else {
            hideProgressDialog();
        }
        btnRegister.setEnabled(!show);
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

    private void navigateToMain() {
        Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
    
    private void navigateToLogin() {
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
