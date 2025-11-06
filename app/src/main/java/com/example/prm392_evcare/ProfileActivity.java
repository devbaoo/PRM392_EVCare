package com.example.prm392_evcare;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.example.prm392_evcare.api.ApiClient;
import com.example.prm392_evcare.api.ApiService;
import com.example.prm392_evcare.models.UserProfile;
import com.example.prm392_evcare.models.UserProfileResponse;
import com.example.prm392_evcare.utils.SessionManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {
    
    private static final String TAG = "ProfileActivity";
    
    private SessionManager sessionManager;
    private ApiService apiService;
    
    // Views
    private ImageView ivAvatar;
    private ImageView ivVerifiedBadge;
    private TextView tvFullName;
    private Chip chipRole;
    private TextView tvUsername;
    private TextView tvEmail;
    private TextView tvPhone;
    private TextView tvAddress;
    private TextView tvMemberSince;
    private TextView tvUserId;
    private ProgressBar progressBar;
    private FloatingActionButton fabEditAvatar;
    private MaterialButton btnEditProfile;
    private MaterialButton btnChangePassword;
    private MaterialButton btnManageVehicles;
    private MaterialButton btnLogout;
    
    private UserProfile currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

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
            getSupportActionBar().setTitle("Profile");
        }
        
        // Initialize views
        initializeViews();
        
        // Set up click listeners
        setupClickListeners();
        
        // Setup bottom navigation
        setupBottomNavigation();
        
        // Load user profile
        loadUserProfile();
    }
    
    private void initializeViews() {
        ivAvatar = findViewById(R.id.ivAvatar);
        ivVerifiedBadge = findViewById(R.id.ivVerifiedBadge);
        tvFullName = findViewById(R.id.tvFullName);
        chipRole = findViewById(R.id.chipRole);
        tvUsername = findViewById(R.id.tvUsername);
        tvEmail = findViewById(R.id.tvEmail);
        tvPhone = findViewById(R.id.tvPhone);
        tvAddress = findViewById(R.id.tvAddress);
        tvMemberSince = findViewById(R.id.tvMemberSince);
        tvUserId = findViewById(R.id.tvUserId);
        progressBar = findViewById(R.id.progressBar);
        fabEditAvatar = findViewById(R.id.fabEditAvatar);
        btnEditProfile = findViewById(R.id.btnEditProfile);
        btnChangePassword = findViewById(R.id.btnChangePassword);
        btnManageVehicles = findViewById(R.id.btnManageVehicles);
        btnLogout = findViewById(R.id.btnLogout);
    }
    
    private void setupClickListeners() {
        fabEditAvatar.setOnClickListener(v -> {
            if (currentUser != null) {
                Intent intent = new Intent(ProfileActivity.this, UploadAvatarActivity.class);
                intent.putExtra(UploadAvatarActivity.EXTRA_CURRENT_AVATAR, currentUser.getAvatar());
                startActivityForResult(intent, UploadAvatarActivity.REQUEST_CODE_UPLOAD_AVATAR);
            } else {
                Toast.makeText(this, "Profile not loaded yet", Toast.LENGTH_SHORT).show();
            }
        });
        
        btnEditProfile.setOnClickListener(v -> {
            if (currentUser != null) {
                Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
                intent.putExtra(EditProfileActivity.EXTRA_USER_PROFILE, currentUser);
                startActivityForResult(intent, EditProfileActivity.REQUEST_CODE_EDIT_PROFILE);
            } else {
                Toast.makeText(this, "Profile not loaded yet", Toast.LENGTH_SHORT).show();
            }
        });
        
        btnChangePassword.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, ChangePasswordActivity.class);
            startActivity(intent);
        });
        
        btnManageVehicles.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, ManageVehiclesActivity.class);
            startActivity(intent);
        });
        
        btnLogout.setOnClickListener(v -> showLogoutDialog());
    }
    
    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        if (bottomNavigationView == null) {
            return;
        }
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                navigateToHome();
                return true;
            } else if (itemId == R.id.nav_nearby) {
                Intent intent = new Intent(ProfileActivity.this, BookingActivity.class);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.nav_bookings) {
                Intent intent = new Intent(ProfileActivity.this, BookingHistoryActivity.class);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.nav_profile) {
                return true; // already here
            }
            return false;
        });
        bottomNavigationView.setSelectedItemId(R.id.nav_profile);
    }
    
    private void loadUserProfile() {
        progressBar.setVisibility(View.VISIBLE);
        
        String token = sessionManager.getAuthToken();
        if (token == null || token.isEmpty()) {
            Toast.makeText(this, "Session expired. Please login again.", Toast.LENGTH_SHORT).show();
            navigateToLogin();
            return;
        }
        
        Call<UserProfileResponse> call = apiService.getUserProfile("Bearer " + token);
        call.enqueue(new Callback<UserProfileResponse>() {
            @Override
            public void onResponse(@NonNull Call<UserProfileResponse> call, 
                                 @NonNull Response<UserProfileResponse> response) {
                progressBar.setVisibility(View.GONE);
                
                if (response.isSuccessful() && response.body() != null) {
                    UserProfileResponse profileResponse = response.body();
                    if (profileResponse.isSuccess() && profileResponse.getUser() != null) {
                        currentUser = profileResponse.getUser();
                        displayUserProfile(currentUser);
                    } else {
                        Toast.makeText(ProfileActivity.this, 
                            "Failed to load profile", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ProfileActivity.this, 
                        "Error: " + response.code(), Toast.LENGTH_SHORT).show();
                    if (response.code() == 401) {
                        navigateToLogin();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<UserProfileResponse> call, @NonNull Throwable t) {
                progressBar.setVisibility(View.GONE);
                Log.e(TAG, "Error loading profile: " + t.getMessage());
                Toast.makeText(ProfileActivity.this, 
                    "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void displayUserProfile(UserProfile user) {
        // Set full name
        if (user.getFullName() != null && !user.getFullName().isEmpty()) {
            tvFullName.setText(user.getFullName());
        } else {
            tvFullName.setText(user.getUsername());
        }
        
        // Set role
        chipRole.setText(user.getRoleDisplayName());
        
        // Set verified badge
        if (user.isVerified()) {
            ivVerifiedBadge.setVisibility(View.VISIBLE);
        } else {
            ivVerifiedBadge.setVisibility(View.GONE);
        }
        
        // Set username
        tvUsername.setText(user.getUsername());
        
        // Set email
        tvEmail.setText(user.getEmail());
        
        // Set phone
        if (user.getPhone() != null && !user.getPhone().isEmpty()) {
            tvPhone.setText(user.getPhone());
        } else {
            tvPhone.setText("Not provided");
        }
        
        // Set address
        if (user.getAddress() != null && !user.getAddress().isEmpty()) {
            tvAddress.setText(user.getAddress());
        } else {
            tvAddress.setText("Not provided");
        }
        
        // Set member since
        if (user.getCreatedAt() != null) {
            String formattedDate = formatDate(user.getCreatedAt());
            tvMemberSince.setText(formattedDate);
        }
        
        // Set user ID (show first 8 characters)
        if (user.getId() != null) {
            String shortId = user.getId().length() > 8 ? 
                user.getId().substring(0, 8) + "..." : user.getId();
            tvUserId.setText(shortId);
        }
        
        // Load avatar
        if (user.getAvatar() != null && !user.getAvatar().isEmpty()) {
            Glide.with(this)
                .load(user.getAvatar())
                .placeholder(android.R.drawable.ic_menu_myplaces)
                .error(android.R.drawable.ic_menu_myplaces)
                .transition(DrawableTransitionOptions.withCrossFade())
                .circleCrop()
                .into(ivAvatar);
        } else {
            ivAvatar.setImageResource(android.R.drawable.ic_menu_myplaces);
        }
    }
    
    private String formatDate(String dateString) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
            Date date = inputFormat.parse(dateString);
            return date != null ? outputFormat.format(date) : dateString;
        } catch (ParseException e) {
            Log.e(TAG, "Error parsing date: " + e.getMessage());
            return dateString;
        }
    }
    
    private void showLogoutDialog() {
        new AlertDialog.Builder(this)
            .setTitle("Đăng xuất")
            .setMessage("Bạn có chắc chắn muốn đăng xuất?")
            .setPositiveButton("Đăng xuất", (dialog, which) -> performLogout())
            .setNegativeButton("Hủy", null)
            .show();
    }
    
    private void performLogout() {
        sessionManager.clearSession();
        Toast.makeText(this, "Đăng xuất thành công", Toast.LENGTH_SHORT).show();
        navigateToLogin();
    }
    
    private void navigateToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
    
    private void navigateToHome() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }
    
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (requestCode == EditProfileActivity.REQUEST_CODE_EDIT_PROFILE && resultCode == RESULT_OK) {
            if (data != null && data.hasExtra(EditProfileActivity.EXTRA_USER_PROFILE)) {
                UserProfile updatedUser = (UserProfile) data.getSerializableExtra(EditProfileActivity.EXTRA_USER_PROFILE);
                if (updatedUser != null) {
                    currentUser = updatedUser;
                    displayUserProfile(currentUser);
                    Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                }
            }
        } else if (requestCode == UploadAvatarActivity.REQUEST_CODE_UPLOAD_AVATAR && resultCode == RESULT_OK) {
            if (data != null && data.hasExtra(UploadAvatarActivity.EXTRA_UPDATED_USER)) {
                UserProfile updatedUser = (UserProfile) data.getSerializableExtra(UploadAvatarActivity.EXTRA_UPDATED_USER);
                if (updatedUser != null) {
                    currentUser = updatedUser;
                    displayUserProfile(currentUser);
                    Toast.makeText(this, "Avatar uploaded successfully", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
