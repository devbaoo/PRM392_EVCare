package com.example.prm392_evcare;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392_evcare.utils.SessionManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.navigation.NavigationView;

public class TechnicianChatActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    
    private static final String TAG = "TechnicianChat";
    
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private SessionManager sessionManager;
    
    // Views
    private MaterialCardView cardEmptyState;
    private RecyclerView recyclerViewChats;
    private ProgressBar progressBar;
    
    // Buttons
    private MaterialButton btnActiveChats;
    private MaterialButton btnHistory;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_technician_chat);
        
        sessionManager = new SessionManager(this);
        
        initializeViews();
        setupToolbar();
        setupNavigationDrawer();
        setupClickListeners();
        loadChatData();
    }
    
    private void initializeViews() {
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        toolbar = findViewById(R.id.toolbar);
        
        cardEmptyState = findViewById(R.id.cardEmptyState);
        recyclerViewChats = findViewById(R.id.recyclerViewChats);
        progressBar = findViewById(R.id.progressBar);
        
        btnActiveChats = findViewById(R.id.btnActiveChats);
        btnHistory = findViewById(R.id.btnHistory);
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
        navigationView.setCheckedItem(R.id.nav_chat);
    }
    
    private void setupClickListeners() {
        btnActiveChats.setOnClickListener(v -> {
            loadChatData();
            Toast.makeText(this, "Showing active chats", Toast.LENGTH_SHORT).show();
        });
        
        btnHistory.setOnClickListener(v -> {
            Toast.makeText(this, "Showing chat history", Toast.LENGTH_SHORT).show();
        });
    }
    
    private void loadChatData() {
        // TODO: Load actual chat data from API
        // For now, show empty state
        cardEmptyState.setVisibility(View.VISIBLE);
        recyclerViewChats.setVisibility(View.GONE);
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
            // Already here
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

