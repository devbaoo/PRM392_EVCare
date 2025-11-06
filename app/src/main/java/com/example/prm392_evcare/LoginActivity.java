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
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.example.prm392_evcare.api.ApiClient;
import com.example.prm392_evcare.api.ApiService;
import com.example.prm392_evcare.models.LoginRequest;
import com.example.prm392_evcare.models.LoginResponse;
import com.example.prm392_evcare.models.User;
import com.example.prm392_evcare.models.UserProfile;
import com.example.prm392_evcare.models.UserProfileResponse;
import com.example.prm392_evcare.utils.SessionManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private TextInputLayout tilEmail, tilPassword;
    private TextInputEditText etEmail, etPassword;
    private MaterialButton btnLogin;
    private TextView tvForgotPassword, tvRegister;
    private ProgressBar progressBar;
    private LottieAnimationView animationView;
    private ApiService apiService;
    private SessionManager sessionManager;
    
    private Dialog progressDialog;
    private Dialog statusDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize API service
        apiService = ApiClient.getClient().create(ApiService.class);
        
        // Initialize session manager
        sessionManager = new SessionManager(this);
        
        // Check if user is already logged in
        if (sessionManager.isLoggedIn()) {
            navigateToMain();
            return;
        }

        // Initialize views
        tilEmail = findViewById(R.id.tilEmail);
        tilPassword = findViewById(R.id.tilPassword);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        tvRegister = findViewById(R.id.tvRegister);
        progressBar = findViewById(R.id.progressBar);
        animationView = findViewById(R.id.animationView);
        
        // Set animation
        animationView.setAnimation(R.raw.electric_car_animation);
        animationView.playAnimation();

        // Set click listeners
        btnLogin.setOnClickListener(v -> attemptLogin());
        tvForgotPassword.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
        });
        tvRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    private void attemptLogin() {
        // Reset errors
        tilEmail.setError(null);
        tilPassword.setError(null);

        // Get values
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Validate inputs
        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(password)) {
            tilPassword.setError("Password is required");
            focusView = etPassword;
            cancel = true;
        }

        if (TextUtils.isEmpty(email)) {
            tilEmail.setError("Email is required");
            focusView = etEmail;
            cancel = true;
        } else if (!isEmailValid(email)) {
            tilEmail.setError("Enter a valid email address");
            focusView = etEmail;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            showProgress(true);
            performLogin(email, password);
        }
    }

    private boolean isEmailValid(String email) {
        return email.contains("@") && email.contains(".");
    }

    private void fetchUserProfileAndCompleteLogin(LoginResponse loginResponse) {
        String token = "Bearer " + sessionManager.getAuthToken();
        
        Call<UserProfileResponse> call = apiService.getUserProfile(token);
        call.enqueue(new Callback<UserProfileResponse>() {
            @Override
            public void onResponse(Call<UserProfileResponse> call, Response<UserProfileResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserProfileResponse profileResponse = response.body();
                    
                    if (profileResponse.isSuccess() && profileResponse.getUser() != null) {
                        UserProfile userProfile = profileResponse.getUser();
                        
                        // Convert UserProfile to User
                        User completeUser = new User();
                        completeUser.setId(userProfile.getId());
                        completeUser.setUsername(userProfile.getUsername());
                        completeUser.setEmail(userProfile.getEmail());
                        completeUser.setFullName(userProfile.getFullName());
                        completeUser.setPhone(userProfile.getPhone());
                        completeUser.setAddress(userProfile.getAddress());
                        completeUser.setRole(userProfile.getRole());
                        completeUser.setAvatar(userProfile.getAvatar());
                        completeUser.setVerified(userProfile.isVerified());
                        
                        // Debug logs
                        android.util.Log.d("LoginActivity", "========== PROFILE FETCHED ==========");
                        android.util.Log.d("LoginActivity", "User ID from profile: " + completeUser.getId());
                        android.util.Log.d("LoginActivity", "User Role: " + completeUser.getRole());
                        android.util.Log.d("LoginActivity", "User Email: " + completeUser.getEmail());
                        android.util.Log.d("LoginActivity", "=====================================");
                        
                        // Save complete user with ID
                        sessionManager.saveUser(completeUser);
                        sessionManager.setLoggedIn(true);
                        
                        // Show success dialog
                        showStatusDialog(true, "Đăng nhập thành công", 
                                "Chào mừng " + completeUser.getFullName() + " đã trở lại!");
                    } else {
                        // Fallback: save user from login response (without ID)
                        android.util.Log.w("LoginActivity", "Failed to fetch profile, using login response user");
                        sessionManager.saveUser(loginResponse.getUser());
                        sessionManager.setLoggedIn(true);
                        
                        showStatusDialog(true, "Đăng nhập thành công", 
                                "Chào mừng " + loginResponse.getUser().getFullName() + " đã trở lại!");
                    }
                } else {
                    // Fallback: save user from login response
                    android.util.Log.w("LoginActivity", "Profile API failed, using login response user");
                    sessionManager.saveUser(loginResponse.getUser());
                    sessionManager.setLoggedIn(true);
                    
                    showStatusDialog(true, "Đăng nhập thành công", 
                            "Chào mừng " + loginResponse.getUser().getFullName() + " đã trở lại!");
                }
            }

            @Override
            public void onFailure(Call<UserProfileResponse> call, Throwable t) {
                // Fallback: save user from login response
                android.util.Log.e("LoginActivity", "Profile API error: " + t.getMessage());
                sessionManager.saveUser(loginResponse.getUser());
                sessionManager.setLoggedIn(true);
                
                showStatusDialog(true, "Đăng nhập thành công", 
                        "Chào mừng " + loginResponse.getUser().getFullName() + " đã trở lại!");
            }
        });
    }
    
    private void performLogin(String email, String password) {
        LoginRequest loginRequest = new LoginRequest(email, password);
        
        Call<LoginResponse> call = apiService.login(loginRequest);
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                showProgress(false);
                
                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse loginResponse = response.body();
                    
                    if (loginResponse.isSuccess()) {
                        // Save tokens first
                        sessionManager.saveAuthToken(loginResponse.getAccessToken());
                        sessionManager.saveRefreshToken(loginResponse.getRefreshToken());
                        
                        // Fetch complete user profile to get user ID
                        fetchUserProfileAndCompleteLogin(loginResponse);
                    } else {
                        // Show error dialog
                        showStatusDialog(false, "Đăng nhập thất bại", 
                                loginResponse.getMessage() != null ? loginResponse.getMessage() : "Sai email hoặc mật khẩu.");
                    }
                } else {
                    // Show error dialog
                    showStatusDialog(false, "Đăng nhập thất bại", 
                            "Không thể kết nối đến máy chủ. Vui lòng thử lại sau.");
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
                // Navigate to main activity with delay for better UX
                new Handler(Looper.getMainLooper()).postDelayed(this::navigateToMain, 500);
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
        btnLogin.setEnabled(!show);
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
        // Check user role and navigate to appropriate screen
        User user = sessionManager.getUser();
        Intent intent;
        
        if (user != null && user.getRole() != null) {
            String role = user.getRole().toLowerCase();
            
            // Navigate based on role
            switch (role) {
                case "technician":
                    intent = new Intent(LoginActivity.this, TechnicianDashboardActivity.class);
                    break;
                case "admin":
                    // TODO: Create AdminDashboardActivity in future
                    intent = new Intent(LoginActivity.this, HomeActivity.class);
                    Toast.makeText(this, "Welcome Admin!", Toast.LENGTH_SHORT).show();
                    break;
                case "staff":
                    // TODO: Create StaffDashboardActivity in future
                    intent = new Intent(LoginActivity.this, HomeActivity.class);
                    Toast.makeText(this, "Welcome Staff!", Toast.LENGTH_SHORT).show();
                    break;
                case "customer":
                default:
                    // Customer goes to HomeActivity
                    intent = new Intent(LoginActivity.this, HomeActivity.class);
                    break;
            }
        } else {
            // Default to HomeActivity if role is not set
            intent = new Intent(LoginActivity.this, HomeActivity.class);
        }
        
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}