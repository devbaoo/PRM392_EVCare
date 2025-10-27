package com.example.prm392_evcare;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
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
import com.example.prm392_evcare.models.ForgotPasswordRequest;
import com.example.prm392_evcare.models.ForgotPasswordResponse;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPasswordActivity extends AppCompatActivity {

    private TextInputLayout tilEmail;
    private TextInputEditText etEmail;
    private MaterialButton btnSubmit;
    private TextView tvBackToLogin;
    private ImageView ivBack;
    private ProgressBar progressBar;
    private LottieAnimationView animationView;
    private ApiService apiService;
    
    private Dialog progressDialog;
    private Dialog statusDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        // Initialize API service
        apiService = ApiClient.getClient().create(ApiService.class);

        // Initialize views
        initViews();
        
        // Set animation
        animationView.setAnimation(R.raw.electric_car_animation);
        animationView.playAnimation();

        // Set click listeners
        btnSubmit.setOnClickListener(v -> attemptForgotPassword());
        tvBackToLogin.setOnClickListener(v -> navigateToLogin());
        ivBack.setOnClickListener(v -> finish());
    }
    
    private void initViews() {
        tilEmail = findViewById(R.id.tilEmail);
        etEmail = findViewById(R.id.etEmail);
        btnSubmit = findViewById(R.id.btnSubmit);
        tvBackToLogin = findViewById(R.id.tvBackToLogin);
        ivBack = findViewById(R.id.ivBack);
        progressBar = findViewById(R.id.progressBar);
        animationView = findViewById(R.id.animationView);
    }

    private void attemptForgotPassword() {
        // Reset errors
        tilEmail.setError(null);

        // Get values
        String email = etEmail.getText().toString().trim();

        // Validate inputs
        boolean cancel = false;
        View focusView = null;

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
            performForgotPassword(email);
        }
    }

    private boolean isEmailValid(String email) {
        return email.contains("@") && email.contains(".");
    }

    private void performForgotPassword(String email) {
        ForgotPasswordRequest forgotPasswordRequest = new ForgotPasswordRequest(email);
        
        Call<ForgotPasswordResponse> call = apiService.forgotPassword(forgotPasswordRequest);
        call.enqueue(new Callback<ForgotPasswordResponse>() {
            @Override
            public void onResponse(Call<ForgotPasswordResponse> call, Response<ForgotPasswordResponse> response) {
                showProgress(false);
                
                if (response.isSuccessful() && response.body() != null) {
                    ForgotPasswordResponse forgotPasswordResponse = response.body();
                    
                    if (forgotPasswordResponse.isSuccess()) {
                        // Show success dialog
                        showStatusDialog(true, "Email sent", forgotPasswordResponse.getMessage());
                    } else {
                        // Show error dialog
                        showStatusDialog(false, "Failed to send email", 
                                forgotPasswordResponse.getMessage() != null 
                                    ? forgotPasswordResponse.getMessage() 
                                    : "Could not send reset email. Please try again.");
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
                                    showStatusDialog(false, "Failed to send email", errorMessage);
                                } else {
                                    showStatusDialog(false, "Failed to send email", 
                                            "An error occurred. Please try again later.");
                                }
                            } catch (Exception e) {
                                showStatusDialog(false, "Failed to send email", 
                                        "An error occurred. Please try again later.");
                            }
                        } else {
                            showStatusDialog(false, "Failed to send email", 
                                    "Could not connect to server. Please try again later.");
                        }
                    } catch (Exception e) {
                        showStatusDialog(false, "Failed to send email", 
                                "Could not connect to server. Please try again later.");
                    }
                }
            }

            @Override
            public void onFailure(Call<ForgotPasswordResponse> call, Throwable t) {
                showProgress(false);
                // Show network error dialog
                showStatusDialog(false, "Connection error", 
                        "Could not connect to server: " + t.getMessage());
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
                // Navigate back to login after successful email send
                navigateToLogin();
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
        btnSubmit.setEnabled(!show);
    }
    
    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new Dialog(this);
            progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            progressDialog.setContentView(R.layout.dialog_login_progress);
            progressDialog.setCancelable(false);
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            
            // Update progress dialog text
            TextView tvLoading = progressDialog.findViewById(R.id.tvLoading);
            if (tvLoading != null) {
                tvLoading.setText("Đang gửi email...");
            }
        }
        progressDialog.show();
    }
    
    private void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    private void navigateToLogin() {
        Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}

