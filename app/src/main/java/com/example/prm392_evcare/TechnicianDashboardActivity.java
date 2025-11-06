package com.example.prm392_evcare;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.bumptech.glide.Glide;
import com.example.prm392_evcare.models.User;
import com.example.prm392_evcare.utils.SessionManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.navigation.NavigationView;

public class TechnicianDashboardActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    
    private static final String TAG = "TechnicianDashboard";
    
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private SessionManager sessionManager;
    
    // Views
    private TextView tvTechnicianWelcome;
    private TextView tvPendingCount;
    private TextView tvCompletedCount;
    private TextView tvScheduleStatus;
    private ProgressBar progressBar;
    
    // Header views
    private TextView tvTechnicianName;
    private TextView tvTechnicianEmail;
    private ImageView ivTechnicianAvatar;
    
    // Buttons
    private MaterialButton btnViewSchedule;
    private MaterialButton btnMyServices;
    private MaterialButton btnChat;
    private MaterialButton btnHistory;
    private MaterialButton btnViewFullSchedule;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_technician_dashboard);
        
        // Initialize session manager
        sessionManager = new SessionManager(this);
        
        // Check if user is logged in and is technician
        if (!sessionManager.isLoggedIn()) {
            navigateToLogin();
            return;
        }
        
        User user = sessionManager.getUser();
        if (user == null || !isTechnician(user)) {
            Toast.makeText(this, "Access denied. Technician role required.", Toast.LENGTH_LONG).show();
            navigateToHome();
            return;
        }
        
        initializeViews();
        setupToolbar();
        setupNavigationDrawer();
        setupClickListeners();
        loadUserInfo();
    }
    
    private void initializeViews() {
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        toolbar = findViewById(R.id.toolbar);
        
        tvTechnicianWelcome = findViewById(R.id.tvTechnicianWelcome);
        tvPendingCount = findViewById(R.id.tvPendingCount);
        tvCompletedCount = findViewById(R.id.tvCompletedCount);
        tvScheduleStatus = findViewById(R.id.tvScheduleStatus);
        progressBar = findViewById(R.id.progressBar);
        
        btnViewSchedule = findViewById(R.id.btnViewSchedule);
        btnMyServices = findViewById(R.id.btnMyServices);
        btnChat = findViewById(R.id.btnChat);
        btnHistory = findViewById(R.id.btnHistory);
        btnViewFullSchedule = findViewById(R.id.btnViewFullSchedule);
        
        // Get header views
        if (navigationView.getHeaderCount() > 0) {
            tvTechnicianName = navigationView.getHeaderView(0).findViewById(R.id.tvTechnicianName);
            tvTechnicianEmail = navigationView.getHeaderView(0).findViewById(R.id.tvTechnicianEmail);
            ivTechnicianAvatar = navigationView.getHeaderView(0).findViewById(R.id.ivTechnicianAvatar);
        }
    }
    
    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
    }
    
    private void setupNavigationDrawer() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        );
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_dashboard);
    }
    
    private void setupClickListeners() {
        btnViewSchedule.setOnClickListener(v -> navigateToSchedule());
        btnMyServices.setOnClickListener(v -> navigateToServices());
        btnChat.setOnClickListener(v -> navigateToChat());
        btnHistory.setOnClickListener(v -> navigateToHistory());
        btnViewFullSchedule.setOnClickListener(v -> navigateToSchedule());
    }
    
    private void loadUserInfo() {
        User user = sessionManager.getUser();
        if (user != null) {
            // Set welcome message
            tvTechnicianWelcome.setText(user.getFullName() != null ? user.getFullName() : user.getUsername());
            
            // Set header info
            if (tvTechnicianName != null) {
                tvTechnicianName.setText(user.getFullName() != null ? user.getFullName() : user.getUsername());
            }
            if (tvTechnicianEmail != null) {
                tvTechnicianEmail.setText(user.getEmail() != null ? user.getEmail() : "");
            }
            
            // Load avatar
            if (ivTechnicianAvatar != null && user.getAvatar() != null && !user.getAvatar().isEmpty()) {
                Glide.with(this)
                    .load(user.getAvatar())
                    .circleCrop()
                    .placeholder(android.R.drawable.ic_menu_myplaces)
                    .error(android.R.drawable.ic_menu_myplaces)
                    .into(ivTechnicianAvatar);
            }
            
            // TODO: Load statistics from API
            tvPendingCount.setText("0");
            tvCompletedCount.setText("0");
            tvScheduleStatus.setText("No appointments scheduled for today");
        }
    }
    
    private boolean isTechnician(User user) {
        return user.getRole() != null && user.getRole().equalsIgnoreCase("technician");
    }
    
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        
        if (itemId == R.id.nav_dashboard) {
            // Already here
        } else if (itemId == R.id.nav_schedule) {
            navigateToSchedule();
        } else if (itemId == R.id.nav_my_services) {
            navigateToServices();
        } else if (itemId == R.id.nav_chat) {
            navigateToChat();
        } else if (itemId == R.id.nav_history) {
            navigateToHistory();
        } else if (itemId == R.id.nav_settings) {
            navigateToSettings();
        } else if (itemId == R.id.nav_logout) {
            showLogoutDialog();
        }
        
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
    
    private void navigateToSchedule() {
        Intent intent = new Intent(this, TechnicianScheduleActivity.class);
        startActivity(intent);
    }
    
    private void navigateToServices() {
        Intent intent = new Intent(this, TechnicianServicesActivity.class);
        startActivity(intent);
    }
    
    private void navigateToChat() {
        Intent intent = new Intent(this, TechnicianChatActivity.class);
        startActivity(intent);
    }
    
    private void navigateToHistory() {
        Intent intent = new Intent(this, TechnicianHistoryActivity.class);
        startActivity(intent);
    }
    
    private void navigateToSettings() {
        Intent intent = new Intent(this, TechnicianSettingsActivity.class);
        startActivity(intent);
    }
    
    private void showLogoutDialog() {
        new AlertDialog.Builder(this)
            .setTitle("Logout")
            .setMessage("Are you sure you want to logout?")
            .setPositiveButton("Yes", (dialog, which) -> logout())
            .setNegativeButton("Cancel", null)
            .show();
    }
    
    private void logout() {
        sessionManager.clearSession();
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
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
    
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}

