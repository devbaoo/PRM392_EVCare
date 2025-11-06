package com.example.prm392_evcare;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
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

import com.example.prm392_evcare.utils.SessionManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.navigation.NavigationView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class TechnicianServicesActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    
    private static final String TAG = "TechnicianServices";
    
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private SessionManager sessionManager;
    
    // Views
    private TextView tvDateRange;
    private TextView tvNoServices;
    private ProgressBar progressBar;
    
    // Chips
    private Chip chipWorking;
    private Chip chipCompleted;
    private Chip chipPending;
    private Chip chipVacation;
    private Chip chipSchedule;
    
    // Buttons
    private MaterialButton btnToday;
    private MaterialButton btnUpcoming;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_technician_services);
        
        sessionManager = new SessionManager(this);
        
        initializeViews();
        setupToolbar();
        setupNavigationDrawer();
        setupClickListeners();
        loadServicesData();
    }
    
    private void initializeViews() {
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        toolbar = findViewById(R.id.toolbar);
        
        tvDateRange = findViewById(R.id.tvDateRange);
        tvNoServices = findViewById(R.id.tvNoServices);
        progressBar = findViewById(R.id.progressBar);
        
        chipWorking = findViewById(R.id.chipWorking);
        chipCompleted = findViewById(R.id.chipCompleted);
        chipPending = findViewById(R.id.chipPending);
        chipVacation = findViewById(R.id.chipVacation);
        chipSchedule = findViewById(R.id.chipSchedule);
        
        btnToday = findViewById(R.id.btnToday);
        btnUpcoming = findViewById(R.id.btnUpcoming);
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
        navigationView.setCheckedItem(R.id.nav_my_services);
    }
    
    private void setupClickListeners() {
        btnToday.setOnClickListener(v -> {
            loadServicesData();
            Toast.makeText(this, "Showing today's services", Toast.LENGTH_SHORT).show();
        });
        
        btnUpcoming.setOnClickListener(v -> {
            Toast.makeText(this, "Showing upcoming services", Toast.LENGTH_SHORT).show();
        });
        
        chipWorking.setOnCheckedChangeListener((buttonView, isChecked) -> filterServices());
        chipCompleted.setOnCheckedChangeListener((buttonView, isChecked) -> filterServices());
        chipPending.setOnCheckedChangeListener((buttonView, isChecked) -> filterServices());
        chipVacation.setOnCheckedChangeListener((buttonView, isChecked) -> filterServices());
        chipSchedule.setOnCheckedChangeListener((buttonView, isChecked) -> filterServices());
    }
    
    private void loadServicesData() {
        // Set date range
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();
        String startDate = dateFormat.format(calendar.getTime());
        calendar.add(Calendar.DAY_OF_MONTH, 30);
        String endDate = dateFormat.format(calendar.getTime());
        tvDateRange.setText("Khoảng: " + startDate + " - " + endDate);
        
        // TODO: Load actual services data from API
        tvNoServices.setText("No active services at the moment");
    }
    
    private void filterServices() {
        // TODO: Filter services based on selected chips
        Toast.makeText(this, "Filtering services...", Toast.LENGTH_SHORT).show();
    }
    
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        
        if (itemId == R.id.nav_dashboard) {
            finish(); // Go back to dashboard
        } else if (itemId == R.id.nav_schedule) {
            startActivity(new Intent(this, TechnicianScheduleActivity.class));
        } else if (itemId == R.id.nav_my_services) {
            // Already here
        } else if (itemId == R.id.nav_chat) {
            startActivity(new Intent(this, TechnicianChatActivity.class));
        } else if (itemId == R.id.nav_history) {
            startActivity(new Intent(this, TechnicianHistoryActivity.class));
        } else if (itemId == R.id.nav_settings) {
            startActivity(new Intent(this, TechnicianSettingsActivity.class));
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

