package com.example.prm392_evcare;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.example.prm392_evcare.api.ApiClient;
import com.example.prm392_evcare.api.ApiService;
import com.example.prm392_evcare.models.UpdateProfileRequest;
import com.example.prm392_evcare.models.UserProfile;
import com.example.prm392_evcare.models.UserProfileResponse;
import com.example.prm392_evcare.utils.SessionManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfileActivity extends AppCompatActivity {
    
    private static final String TAG = "EditProfileActivity";
    public static final String EXTRA_USER_PROFILE = "extra_user_profile";
    public static final int REQUEST_CODE_EDIT_PROFILE = 1001;
    
    private SessionManager sessionManager;
    private ApiService apiService;
    
    // Views
    private ImageView ivAvatar;
    private TextInputEditText etUsername;
    private TextInputEditText etFullName;
    private TextInputEditText etEmail;
    private TextInputEditText etPhone;
    private TextInputEditText etAddress;
    private MaterialButton btnSave;
    private MaterialButton btnCancel;
    private ProgressBar progressBar;
    
    private UserProfile currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // Initialize session manager and API
        sessionManager = new SessionManager(this);
        apiService = ApiClient.getClient().create(ApiService.class);
        
        // Set up toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Chỉnh sửa thông tin");
        }
        
        // Initialize views
        initializeViews();
        
        // Get user profile from intent
        if (getIntent().hasExtra(EXTRA_USER_PROFILE)) {
            currentUser = (UserProfile) getIntent().getSerializableExtra(EXTRA_USER_PROFILE);
            if (currentUser != null) {
                populateFields();
            }
        }
        
        // Set up click listeners
        setupClickListeners();
    }
    
    private void initializeViews() {
        ivAvatar = findViewById(R.id.ivAvatar);
        etUsername = findViewById(R.id.etUsername);
        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etAddress = findViewById(R.id.etAddress);
        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);
        progressBar = findViewById(R.id.progressBar);
    }
    
    private void populateFields() {
        // Load avatar
        if (currentUser.getAvatar() != null && !currentUser.getAvatar().isEmpty()) {
            Glide.with(this)
                .load(currentUser.getAvatar())
                .placeholder(android.R.drawable.ic_menu_myplaces)
                .error(android.R.drawable.ic_menu_myplaces)
                .transition(DrawableTransitionOptions.withCrossFade())
                .circleCrop()
                .into(ivAvatar);
        }
        
        // Populate fields
        etUsername.setText(currentUser.getUsername());
        etFullName.setText(currentUser.getFullName());
        etEmail.setText(currentUser.getEmail());
        etPhone.setText(currentUser.getPhone());
        etAddress.setText(currentUser.getAddress());
    }
    
    private void setupClickListeners() {
        btnCancel.setOnClickListener(v -> finish());
        
        btnSave.setOnClickListener(v -> {
            if (validateInputs()) {
                updateProfile();
            }
        });
    }
    
    private boolean validateInputs() {
        String username = etUsername.getText() != null ? etUsername.getText().toString().trim() : "";
        String fullName = etFullName.getText() != null ? etFullName.getText().toString().trim() : "";
        String phone = etPhone.getText() != null ? etPhone.getText().toString().trim() : "";
        String address = etAddress.getText() != null ? etAddress.getText().toString().trim() : "";
        
        if (username.isEmpty()) {
            etUsername.setError("Tên đăng nhập không được để trống");
            etUsername.requestFocus();
            return false;
        }
        
        if (fullName.isEmpty()) {
            etFullName.setError("Họ và tên không được để trống");
            etFullName.requestFocus();
            return false;
        }
        
        if (phone.isEmpty()) {
            etPhone.setError("Số điện thoại không được để trống");
            etPhone.requestFocus();
            return false;
        }
        
        if (!phone.matches("^[0-9]{9,10}$")) {
            etPhone.setError("Số điện thoại không hợp lệ (9-10 chữ số)");
            etPhone.requestFocus();
            return false;
        }
        
        return true;
    }
    
    private void updateProfile() {
        progressBar.setVisibility(View.VISIBLE);
        btnSave.setEnabled(false);
        
        String username = etUsername.getText().toString().trim();
        String fullName = etFullName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        
        UpdateProfileRequest request = new UpdateProfileRequest(username, fullName, phone, address);
        
        String token = sessionManager.getAuthToken();
        if (token == null || token.isEmpty()) {
            Toast.makeText(this, "Session expired. Please login again.", Toast.LENGTH_SHORT).show();
            navigateToLogin();
            return;
        }
        
        Call<UserProfileResponse> call = apiService.updateUserProfile("Bearer " + token, request);
        call.enqueue(new Callback<UserProfileResponse>() {
            @Override
            public void onResponse(@NonNull Call<UserProfileResponse> call, 
                                 @NonNull Response<UserProfileResponse> response) {
                progressBar.setVisibility(View.GONE);
                btnSave.setEnabled(true);
                
                if (response.isSuccessful() && response.body() != null) {
                    UserProfileResponse profileResponse = response.body();
                    if (profileResponse.isSuccess()) {
                        Toast.makeText(EditProfileActivity.this, 
                            profileResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        
                        // Return updated profile to ProfileActivity
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra(EXTRA_USER_PROFILE, profileResponse.getUser());
                        setResult(RESULT_OK, resultIntent);
                        finish();
                    } else {
                        Toast.makeText(EditProfileActivity.this, 
                            "Failed to update profile", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(EditProfileActivity.this, 
                        "Error: " + response.code(), Toast.LENGTH_SHORT).show();
                    
                    if (response.code() == 401) {
                        navigateToLogin();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<UserProfileResponse> call, @NonNull Throwable t) {
                progressBar.setVisibility(View.GONE);
                btnSave.setEnabled(true);
                Log.e(TAG, "Error updating profile: " + t.getMessage());
                Toast.makeText(EditProfileActivity.this, 
                    "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void navigateToLogin() {
        sessionManager.clearSession();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
    
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
