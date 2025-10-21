package com.example.prm392_evcare;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.example.prm392_evcare.models.User;
import com.example.prm392_evcare.utils.SessionManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {

    private SessionManager sessionManager;
    private TextView tvWelcomeUser;
    private CardView cardBookService, cardChargingStations, cardMyVehicles, cardSupport;
    private BottomNavigationView bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Initialize session manager
        sessionManager = new SessionManager(this);
        
        // Check if user is logged in
        if (!sessionManager.isLoggedIn()) {
            navigateToLogin();
            return;
        }
        
        // Set up toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        
        // Initialize views
        tvWelcomeUser = findViewById(R.id.tvWelcomeUser);
        cardBookService = findViewById(R.id.cardBookService);
        cardChargingStations = findViewById(R.id.cardChargingStations);
        cardMyVehicles = findViewById(R.id.cardMyVehicles);
        cardSupport = findViewById(R.id.cardSupport);
        bottomNavigation = findViewById(R.id.bottomNavigation);
        
        // Display user info
        displayUserInfo();
        
        // Set up click listeners
        setupClickListeners();
        
        // Set up bottom navigation
        setupBottomNavigation();
    }
    
    private void displayUserInfo() {
        User user = sessionManager.getUser();
        if (user != null) {
            String welcomeMessage = "Welcome, " + user.getFullName() + "!";
            tvWelcomeUser.setText(welcomeMessage);
        }
    }
    
    private void setupClickListeners() {
        cardBookService.setOnClickListener(v -> {
            Toast.makeText(this, "Book Service coming soon", Toast.LENGTH_SHORT).show();
        });
        
        cardChargingStations.setOnClickListener(v -> {
            Toast.makeText(this, "Charging Stations coming soon", Toast.LENGTH_SHORT).show();
        });
        
        cardMyVehicles.setOnClickListener(v -> {
            Toast.makeText(this, "My Vehicles coming soon", Toast.LENGTH_SHORT).show();
        });
        
        cardSupport.setOnClickListener(v -> {
            Toast.makeText(this, "Support coming soon", Toast.LENGTH_SHORT).show();
        });
    }
    
    private void setupBottomNavigation() {
        bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            
            if (itemId == R.id.navigation_home) {
                // Already on home, do nothing
                return true;
            } else if (itemId == R.id.navigation_services) {
                Toast.makeText(this, "Services coming soon", Toast.LENGTH_SHORT).show();
                return true;
            } else if (itemId == R.id.navigation_map) {
                Toast.makeText(this, "Map coming soon", Toast.LENGTH_SHORT).show();
                return true;
            } else if (itemId == R.id.navigation_profile) {
                Toast.makeText(this, "Profile coming soon", Toast.LENGTH_SHORT).show();
                return true;
            }
            
            return false;
        });
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            logout();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    private void logout() {
        sessionManager.clearSession();
        navigateToLogin();
    }
    
    private void navigateToLogin() {
        Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
