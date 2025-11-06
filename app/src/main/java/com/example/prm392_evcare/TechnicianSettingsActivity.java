package com.example.prm392_evcare;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.prm392_evcare.utils.SessionManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.switchmaterial.SwitchMaterial;

public class TechnicianSettingsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    
    private static final String TAG = "TechnicianSettings";
    
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private SessionManager sessionManager;
    
    // Switches
    private SwitchMaterial switchPushNotifications;
    private SwitchMaterial switchEmailNotifications;
    
    // Buttons
    private MaterialButton btnEditProfile;
    private MaterialButton btnChangePassword;
    private MaterialButton btnManageSchedule;
    private MaterialButton btnAbout;
    private MaterialButton btnHelp;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_technician_settings);
        
        sessionManager = new SessionManager(this);
        
        initializeViews();
        setupToolbar();
        setupNavigationDrawer();
        setupClickListeners();
    }
    
    private void initializeViews() {
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        toolbar = findViewById(R.id.toolbar);
        
        switchPushNotifications = findViewById(R.id.switchPushNotifications);
        switchEmailNotifications = findViewById(R.id.switchEmailNotifications);
        
        btnEditProfile = findViewById(R.id.btnEditProfile);
        btnChangePassword = findViewById(R.id.btnChangePassword);
        btnManageSchedule = findViewById(R.id.btnManageSchedule);
        btnAbout = findViewById(R.id.btnAbout);
        btnHelp = findViewById(R.id.btnHelp);
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
        navigationView.setCheckedItem(R.id.nav_settings);
    }
    
    private void setupClickListeners() {
        btnEditProfile.setOnClickListener(v -> {
            Intent intent = new Intent(this, EditProfileActivity.class);
            startActivity(intent);
        });
        
        btnChangePassword.setOnClickListener(v -> {
            Intent intent = new Intent(this, ChangePasswordActivity.class);
            startActivity(intent);
        });
        
        btnManageSchedule.setOnClickListener(v -> {
            Intent intent = new Intent(this, TechnicianScheduleActivity.class);
            startActivity(intent);
        });
        
        btnAbout.setOnClickListener(v -> {
            Toast.makeText(this, "EVCare - Electric Vehicle Care\nVersion 1.0.0", Toast.LENGTH_LONG).show();
        });
        
        btnHelp.setOnClickListener(v -> {
            Toast.makeText(this, "Help & Support coming soon", Toast.LENGTH_SHORT).show();
        });
        
        switchPushNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Toast.makeText(this, "Push notifications " + (isChecked ? "enabled" : "disabled"), Toast.LENGTH_SHORT).show();
        });
        
        switchEmailNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Toast.makeText(this, "Email notifications " + (isChecked ? "enabled" : "disabled"), Toast.LENGTH_SHORT).show();
        });
    }
    
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        
        if (itemId == R.id.nav_dashboard) {
            finish(); // Go back to dashboard
        } else if (itemId == R.id.nav_schedule) {
            startActivity(new Intent(this, TechnicianScheduleActivity.class));
        } else if (itemId == R.id.nav_my_services) {
            startActivity(new Intent(this, TechnicianServicesActivity.class));
        } else if (itemId == R.id.nav_chat) {
            startActivity(new Intent(this, TechnicianChatActivity.class));
        } else if (itemId == R.id.nav_history) {
            startActivity(new Intent(this, TechnicianHistoryActivity.class));
        } else if (itemId == R.id.nav_settings) {
            // Already here
        } else if (itemId == R.id.nav_logout) {
            showLogoutDialog();
        }
        
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
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
        Intent intent = new Intent(this, LoginActivity.class);
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

