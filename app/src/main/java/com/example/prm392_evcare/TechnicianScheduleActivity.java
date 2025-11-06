package com.example.prm392_evcare;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392_evcare.adapters.CalendarAdapter;
import com.example.prm392_evcare.api.ApiClient;
import com.example.prm392_evcare.api.ApiService;
import com.example.prm392_evcare.models.CalendarDay;
import com.example.prm392_evcare.models.TechnicianScheduleResponse;
import com.example.prm392_evcare.models.User;
import com.example.prm392_evcare.utils.SessionManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.navigation.NavigationView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TechnicianScheduleActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    
    private static final String TAG = "TechnicianSchedule";
    
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private SessionManager sessionManager;
    
    // Views
    private TextView tvCurrentDate;
    private TextView tvStartTime;
    private TextView tvEndTime;
    private TextView tvNoAppointments;
    private ProgressBar progressBar;
    
    // Chips
    private Chip chipWorking;
    private Chip chipCompleted;
    private Chip chipPending;
    private Chip chipVacation;
    private Chip chipSchedule;
    
    // Buttons
    private MaterialButton btnToday;
    private MaterialButton btnCheckOut;
    private MaterialButton btnMonthView;
    private MaterialButton btnYearView;
    
    // Calendar
    private RecyclerView recyclerCalendar;
    private CalendarAdapter calendarAdapter;
    private Spinner spinnerYear;
    private Spinner spinnerMonth;
    private Calendar selectedCalendar;
    private int currentYear;
    private int currentMonth;
    
    // API
    private ApiService apiService;
    private Map<String, TechnicianScheduleResponse.ScheduleData> scheduleMap;
    private List<TechnicianScheduleResponse.ScheduleData> currentSchedules;
    
    // Current selected schedule
    private TechnicianScheduleResponse.ScheduleData currentSelectedSchedule;
    
    // Flag to prevent initial spinner trigger
    private boolean isSpinnerInitialized = false;
    
    // Vietnam timezone
    private static final TimeZone VIETNAM_TIMEZONE = TimeZone.getTimeZone("Asia/Ho_Chi_Minh");
    
    /**
     * Get Calendar instance with Vietnam timezone
     */
    private Calendar getVietnamCalendar() {
        return Calendar.getInstance(VIETNAM_TIMEZONE, Locale.getDefault());
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_technician_schedule);
        
        sessionManager = new SessionManager(this);
        apiService = ApiClient.getClient().create(ApiService.class);
        scheduleMap = new HashMap<>();
        
        // Initialize calendar with Vietnam timezone
        selectedCalendar = getVietnamCalendar();
        currentYear = selectedCalendar.get(Calendar.YEAR);
        currentMonth = selectedCalendar.get(Calendar.MONTH);
        
        initializeViews();
        setupToolbar();
        setupNavigationDrawer();
        setupCalendar();
        setupClickListeners();
        
        // Display current Vietnam date immediately
        displayCurrentDate();
        
        loadScheduleData();
    }
    
    private void initializeViews() {
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        toolbar = findViewById(R.id.toolbar);
        
        tvCurrentDate = findViewById(R.id.tvCurrentDate);
        tvStartTime = findViewById(R.id.tvStartTime);
        tvEndTime = findViewById(R.id.tvEndTime);
        tvNoAppointments = findViewById(R.id.tvNoAppointments);
        progressBar = findViewById(R.id.progressBar);
        
        chipWorking = findViewById(R.id.chipWorking);
        chipCompleted = findViewById(R.id.chipCompleted);
        chipPending = findViewById(R.id.chipPending);
        chipVacation = findViewById(R.id.chipVacation);
        chipSchedule = findViewById(R.id.chipSchedule);
        
        btnToday = findViewById(R.id.btnToday);
        btnCheckOut = findViewById(R.id.btnCheckOut);
        btnMonthView = findViewById(R.id.btnMonthView);
        btnYearView = findViewById(R.id.btnYearView);
        
        recyclerCalendar = findViewById(R.id.recyclerCalendar);
        spinnerYear = findViewById(R.id.spinnerYear);
        spinnerMonth = findViewById(R.id.spinnerMonth);
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
        navigationView.setCheckedItem(R.id.nav_schedule);
    }
    
    private void setupCalendar() {
        // Setup RecyclerView with GridLayout (7 columns for days of week)
        GridLayoutManager layoutManager = new GridLayoutManager(this, 7);
        recyclerCalendar.setLayoutManager(layoutManager);
        
        // Setup adapter
        List<CalendarDay> calendarDays = generateCalendarDays(currentYear, currentMonth);
        calendarAdapter = new CalendarAdapter(calendarDays, day -> {
            // Update selected date and show schedule info
            updateSelectedDate(day);
        });
        recyclerCalendar.setAdapter(calendarAdapter);
        
        // Setup Year Spinner
        List<String> years = new ArrayList<>();
        for (int i = currentYear - 5; i <= currentYear + 5; i++) {
            years.add(String.valueOf(i));
        }
        ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, years);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerYear.setAdapter(yearAdapter);
        spinnerYear.setSelection(5); // Current year
        
        // Setup Month Spinner
        String[] months = {"Th1", "Th2", "Th3", "Th4", "Th5", "Th6", "Th7", "Th8", "Th9", "Th10", "Th11", "Th12"};
        ArrayAdapter<String> monthAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, months);
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMonth.setAdapter(monthAdapter);
        spinnerMonth.setSelection(currentMonth);
        
        // Spinner listeners
        spinnerYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentYear = Integer.parseInt(years.get(position));
                // Only fetch if spinners are initialized (not during setup)
                if (isSpinnerInitialized) {
                    fetchTechnicianSchedule();
                }
            }
            
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        
        spinnerMonth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentMonth = position;
                // Only fetch if spinners are initialized (not during setup)
                if (isSpinnerInitialized) {
                    fetchTechnicianSchedule();
                }
            }
            
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        
        // Mark spinners as initialized after setup
        isSpinnerInitialized = true;
    }
    
    private List<CalendarDay> generateCalendarDays(int year, int month) {
        List<CalendarDay> days = new ArrayList<>();
        
        Calendar calendar = getVietnamCalendar();
        calendar.set(year, month, 1);
        
        // Get first day of month (1 = Sunday, 2 = Monday, ...)
        int firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        int dayOfWeek = (firstDayOfWeek == 1) ? 7 : firstDayOfWeek - 1; // Convert to Monday = 1
        
        // Get days in month
        int daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        
        // Get previous month
        calendar.add(Calendar.MONTH, -1);
        int prevMonth = calendar.get(Calendar.MONTH);
        int prevYear = calendar.get(Calendar.YEAR);
        int daysInPrevMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        
        // Add empty cells for days before month starts
        for (int i = dayOfWeek - 1; i > 0; i--) {
            days.add(new CalendarDay(daysInPrevMonth - i + 1, prevMonth, prevYear, false));
        }
        
        // SimpleDateFormat for creating lookup keys
        SimpleDateFormat keyFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        keyFormat.setTimeZone(VIETNAM_TIMEZONE);
        
        // Add days of current month
        for (int i = 1; i <= daysInMonth; i++) {
            CalendarDay day = new CalendarDay(i, month, year, true);
            
            // Create key for this day
            Calendar dayCal = getVietnamCalendar();
            dayCal.set(year, month, i);
            String dateKey = keyFormat.format(dayCal.getTime());
            
            // Check if schedule exists for this day
            if (scheduleMap.containsKey(dateKey)) {
                TechnicianScheduleResponse.ScheduleData schedule = scheduleMap.get(dateKey);
                
                if (schedule != null) {
                    day.setHasSchedule(true);
                    
                    // Set schedule time
                    String startTime = schedule.getStartTime() != null ? schedule.getStartTime() : "08:00";
                    String endTime = schedule.getEndTime() != null ? schedule.getEndTime() : "17:00";
                    day.setScheduleTime(startTime + " - " + endTime);
                    
                    // Set status based on schedule status
                    String status = schedule.getStatus();
                    if ("completed".equalsIgnoreCase(status)) {
                        day.setCompleted(true);
                    } else if ("in_progress".equalsIgnoreCase(status)) {
                        day.setWorking(true);
                    } else if ("pending".equalsIgnoreCase(status)) {
                        day.setOnLeave(true); // Using onLeave for pending (orange indicator)
                    } else if ("cancelled".equalsIgnoreCase(status)) {
                        day.setAbsent(true); // Using absent for cancelled (red indicator)
                    }
                    
                    // Store appointment info if available
                    if (schedule.getAppointment() != null) {
                        TechnicianScheduleResponse.AppointmentInfo appointment = schedule.getAppointment();
                        
                        // You can add more details to CalendarDay if needed
                        // For now, we'll just use the notes field
                        if (appointment.getCustomer() != null) {
                            String customerInfo = appointment.getCustomer().getName();
                            day.setScheduleTime(startTime + " - " + endTime + "\n" + customerInfo);
                        }
                    }
                }
            }
            
            days.add(day);
        }
        
        // Add empty cells to complete the grid (42 cells = 6 rows)
        int remainingCells = 42 - days.size();
        for (int i = 1; i <= remainingCells; i++) {
            days.add(new CalendarDay(i, (month + 1) % 12, year, false));
        }
        
        return days;
    }
    
    private void updateCalendar() {
        // Always regenerate calendar with current data (even if empty)
        List<CalendarDay> newDays = generateCalendarDays(currentYear, currentMonth);
        calendarAdapter.updateCalendar(newDays);
    }
    
    /**
     * Display current date in Vietnam timezone
     */
    private void displayCurrentDate() {
        Calendar vietnamCalendar = getVietnamCalendar();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        dateFormat.setTimeZone(VIETNAM_TIMEZONE);
        tvCurrentDate.setText(dateFormat.format(vietnamCalendar.getTime()));
    }
    
    private void updateSelectedDate(CalendarDay day) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        dateFormat.setTimeZone(VIETNAM_TIMEZONE);
        Calendar cal = getVietnamCalendar();
        cal.set(day.getYear(), day.getMonth(), day.getDay());
        tvCurrentDate.setText(dateFormat.format(cal.getTime()));
        
        // Find schedule for selected date
        TechnicianScheduleResponse.ScheduleData selectedSchedule = findScheduleForDate(day);
        
        if (selectedSchedule != null) {
            // Update UI with schedule info
            updateScheduleDisplay(selectedSchedule);
        } else {
            // No schedule for this date
            showNoScheduleMessage();
        }
    }
    
    private TechnicianScheduleResponse.ScheduleData findScheduleForDate(CalendarDay day) {
        if (currentSchedules == null || currentSchedules.isEmpty()) {
            return null;
        }
        
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        dateFormat.setTimeZone(VIETNAM_TIMEZONE);
        Calendar cal = getVietnamCalendar();
        cal.set(day.getYear(), day.getMonth(), day.getDay());
        String targetDate = dateFormat.format(cal.getTime());
        
        for (TechnicianScheduleResponse.ScheduleData schedule : currentSchedules) {
            String scheduleDate = schedule.getDate();
            if (scheduleDate != null && scheduleDate.startsWith(targetDate)) {
                return schedule;
            }
        }
        
        return null;
    }
    
    private void updateScheduleDisplay(TechnicianScheduleResponse.ScheduleData schedule) {
        // Update time display
        String startTime = schedule.getStartTime() != null ? schedule.getStartTime() : "08:00";
        String endTime = schedule.getEndTime() != null ? schedule.getEndTime() : "17:00";
        tvStartTime.setText(startTime);
        tvEndTime.setText(endTime);
        
        // Store current selected schedule for check-in/check-out
        currentSelectedSchedule = schedule;
        
        // Update button based on check-in/check-out status
        updateCheckInOutButton();
    }
    
    private void showNoScheduleMessage() {
        // Clear time display and show "no schedule" message
        tvStartTime.setText("--:--");
        tvEndTime.setText("--:--");
        currentSelectedSchedule = null;
        
        // Hide check-in/check-out button when no schedule
        btnCheckOut.setVisibility(View.GONE);
        
        Toast.makeText(this, "Hôm nay không có lịch", Toast.LENGTH_SHORT).show();
    }
    
    /**
     * Update check-in/check-out button based on current schedule status
     */
    private void updateCheckInOutButton() {
        if (currentSelectedSchedule == null) {
            btnCheckOut.setVisibility(View.GONE);
            return;
        }
        
        btnCheckOut.setVisibility(View.VISIBLE);
        
        // Check if already checked out
        if (currentSelectedSchedule.isCheckedOut()) {
            btnCheckOut.setText("Đã Check Out");
            btnCheckOut.setEnabled(false);
            btnCheckOut.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFF9E9E9E)); // Gray
        }
        // Check if already checked in
        else if (currentSelectedSchedule.isCheckedIn()) {
            btnCheckOut.setText("Check Out");
            btnCheckOut.setEnabled(true);
            btnCheckOut.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFFEF5350)); // Red
        }
        // Not checked in yet
        else {
            btnCheckOut.setText("Check In");
            btnCheckOut.setEnabled(true);
            btnCheckOut.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFF6366F1)); // Blue
        }
    }
    
    private void updateTodayScheduleDisplay() {
        // Create CalendarDay for today with Vietnam timezone
        Calendar today = getVietnamCalendar();
        CalendarDay todayDay = new CalendarDay(
            today.get(Calendar.DAY_OF_MONTH),
            today.get(Calendar.MONTH),
            today.get(Calendar.YEAR),
            true
        );
        
        // Update date display with Vietnam timezone
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        dateFormat.setTimeZone(VIETNAM_TIMEZONE);
        tvCurrentDate.setText(dateFormat.format(today.getTime()));
        
        // Find and display today's schedule
        TechnicianScheduleResponse.ScheduleData todaySchedule = findScheduleForDate(todayDay);
        
        if (todaySchedule != null) {
            updateScheduleDisplay(todaySchedule);
        } else {
            showNoScheduleMessage();
        }
    }
    
    private void setupClickListeners() {
        btnToday.setOnClickListener(v -> {
            Calendar today = getVietnamCalendar();
            currentYear = today.get(Calendar.YEAR);
            currentMonth = today.get(Calendar.MONTH);
            
            spinnerYear.setSelection(5); // Current year position
            spinnerMonth.setSelection(currentMonth);
            // fetchTechnicianSchedule() will be called by spinner listener
            
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            dateFormat.setTimeZone(VIETNAM_TIMEZONE);
            tvCurrentDate.setText(dateFormat.format(today.getTime()));
            
            Toast.makeText(this, "Showing today's schedule", Toast.LENGTH_SHORT).show();
        });
        
        btnCheckOut.setOnClickListener(v -> {
            if (currentSelectedSchedule == null) {
                Toast.makeText(this, "Không có lịch làm việc", Toast.LENGTH_SHORT).show();
                return;
            }
            
            // If already checked in, perform check-out
            if (currentSelectedSchedule.isCheckedIn() && !currentSelectedSchedule.isCheckedOut()) {
                performCheckOut();
            }
            // If not checked in yet, perform check-in
            else if (!currentSelectedSchedule.isCheckedIn()) {
                performCheckIn();
            }
        });
        
        btnMonthView.setOnClickListener(v -> {
            // Already in month view
            Toast.makeText(this, "Month view active", Toast.LENGTH_SHORT).show();
        });
        
        btnYearView.setOnClickListener(v -> {
            Toast.makeText(this, "Year view coming soon", Toast.LENGTH_SHORT).show();
        });
        
        chipWorking.setOnCheckedChangeListener((buttonView, isChecked) -> filterSchedule());
        chipCompleted.setOnCheckedChangeListener((buttonView, isChecked) -> filterSchedule());
        chipPending.setOnCheckedChangeListener((buttonView, isChecked) -> filterSchedule());
        chipVacation.setOnCheckedChangeListener((buttonView, isChecked) -> filterSchedule());
        chipSchedule.setOnCheckedChangeListener((buttonView, isChecked) -> filterSchedule());
    }
    
    private void loadScheduleData() {
        // Set current date
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        tvCurrentDate.setText(dateFormat.format(new Date()));
        
        // Set default working hours
        tvStartTime.setText("08:00");
        tvEndTime.setText("17:00");
        
        // Load schedule from API
        fetchTechnicianSchedule();
    }
    
    private void fetchTechnicianSchedule() {
        Log.d(TAG, "========== FETCHING SCHEDULE ==========");
        progressBar.setVisibility(View.VISIBLE);
        
        // Debug: Check session data
        User currentUser = sessionManager.getUser();
        Log.d(TAG, "Current User: " + (currentUser != null ? "EXISTS" : "NULL"));
        if (currentUser != null) {
            Log.d(TAG, "User ID: " + currentUser.getId());
            Log.d(TAG, "User Role: " + currentUser.getRole());
            Log.d(TAG, "User Name: " + currentUser.getFullName());
        }
        
        // Get technician ID from session (you may need to store this during login)
        String technicianId = sessionManager.getUserId(); // Assuming the user is a technician
        Log.d(TAG, "Technician ID from getUserId(): " + technicianId);
        
        if (technicianId == null || technicianId.isEmpty()) {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(this, "Unable to load schedule: Technician ID not found", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "ERROR: Technician ID is null or empty");
            Log.e(TAG, "Please make sure you are logged in with a technician account");
            return;
        }
        
        // Calculate date range (current month) using Vietnam timezone
        Calendar startCal = getVietnamCalendar();
        startCal.set(currentYear, currentMonth, 1);
        
        Calendar endCal = getVietnamCalendar();
        endCal.set(currentYear, currentMonth, endCal.getActualMaximum(Calendar.DAY_OF_MONTH));
        
        SimpleDateFormat apiDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        apiDateFormat.setTimeZone(VIETNAM_TIMEZONE);
        String startDate = apiDateFormat.format(startCal.getTime());
        String endDate = apiDateFormat.format(endCal.getTime());
        
        Log.d(TAG, "Date Range: " + startDate + " to " + endDate);
        
        String token = "Bearer " + sessionManager.getAuthToken();
        Log.d(TAG, "Calling API with Token: " + (token != null ? "Present" : "Missing"));
        
        Call<TechnicianScheduleResponse> call = apiService.getTechnicianSchedules(token, technicianId, startDate, endDate);
        Log.d(TAG, "API Call URL: " + call.request().url());
        call.enqueue(new Callback<TechnicianScheduleResponse>() {
            @Override
            public void onResponse(Call<TechnicianScheduleResponse> call, Response<TechnicianScheduleResponse> response) {
                progressBar.setVisibility(View.GONE);
                
                Log.d(TAG, "========== API RESPONSE ==========");
                Log.d(TAG, "Response Code: " + response.code());
                Log.d(TAG, "Response Success: " + response.isSuccessful());
                
                // Log raw JSON response
                try {
                    String rawJson = new com.google.gson.Gson().toJson(response.body());
                    Log.d(TAG, "Raw JSON Response: " + rawJson);
                } catch (Exception e) {
                    Log.e(TAG, "Error logging JSON", e);
                }
                
                if (response.isSuccessful() && response.body() != null) {
                    TechnicianScheduleResponse scheduleResponse = response.body();
                    Log.d(TAG, "Response Body Success: " + scheduleResponse.isSuccess());
                    Log.d(TAG, "Response Message: " + scheduleResponse.getMessage());
                    
                    if (scheduleResponse.isSuccess() && scheduleResponse.getData() != null) {
                        Log.d(TAG, "Number of schedules: " + scheduleResponse.getData().size());
                        
                        // Store current schedules for date selection
                        currentSchedules = scheduleResponse.getData();
                        
                        // Build schedule map for quick lookup
                        scheduleMap.clear();
                        for (TechnicianScheduleResponse.ScheduleData schedule : scheduleResponse.getData()) {
                            Log.d(TAG, "Schedule: date=" + schedule.getDate() + 
                                ", status=" + schedule.getStatus() + 
                                ", time=" + schedule.getStartTime() + "-" + schedule.getEndTime());
                            
                            // Skip if date is null or empty
                            if (schedule.getDate() == null || schedule.getDate().isEmpty()) {
                                Log.w(TAG, "Skipping schedule with null/empty date");
                                continue;
                            }
                            
                            try {
                                // Parse date from API format "2024-11-05T00:00:00.000Z"
                                SimpleDateFormat apiFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
                                Date date = apiFormat.parse(schedule.getDate());
                                
                                if (date != null) {
                                    SimpleDateFormat keyFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                                    String key = keyFormat.format(date);
                                    scheduleMap.put(key, schedule);
                                    Log.d(TAG, "Added schedule for date: " + key);
                                }
                            } catch (Exception e) {
                                Log.e(TAG, "Error parsing date: " + schedule.getDate(), e);
                            }
                        }
                        
                        // Refresh calendar with schedule data
                        Log.d(TAG, "Updating calendar with " + scheduleMap.size() + " mapped schedules");
                        updateCalendar();
                        
                        // Update display for today's date if exists
                        updateTodayScheduleDisplay();
                        
                        Toast.makeText(TechnicianScheduleActivity.this, 
                            "Loaded " + scheduleResponse.getData().size() + " schedules", 
                            Toast.LENGTH_SHORT).show();
                    } else {
                        Log.w(TAG, "No schedules found or API returned unsuccessful");
                        Toast.makeText(TechnicianScheduleActivity.this, 
                            "No schedules found for this month", 
                            Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e(TAG, "API Error: Code=" + response.code());
                    try {
                        if (response.errorBody() != null) {
                            Log.e(TAG, "Error Body: " + response.errorBody().string());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(TechnicianScheduleActivity.this, 
                        "Failed to load schedule: " + response.code(), 
                        Toast.LENGTH_SHORT).show();
                }
            }
            
            @Override
            public void onFailure(Call<TechnicianScheduleResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Log.e(TAG, "========== API FAILURE ==========");
                Log.e(TAG, "Error: " + t.getMessage());
                Log.e(TAG, "Stack trace: ", t);
                Toast.makeText(TechnicianScheduleActivity.this, 
                    "Error: " + t.getMessage(), 
                    Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void filterSchedule() {
        // TODO: Filter appointments based on selected chips
        Toast.makeText(this, "Filtering schedule...", Toast.LENGTH_SHORT).show();
    }
    
    /**
     * Perform check-in for the current schedule
     */
    private void performCheckIn() {
        if (currentSelectedSchedule == null || currentSelectedSchedule.getId() == null) {
            Toast.makeText(this, "Không tìm thấy lịch làm việc", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Show confirmation dialog
        new AlertDialog.Builder(this)
            .setTitle("Check In")
            .setMessage("Bạn có chắc muốn check in không?")
            .setPositiveButton("Check In", (dialog, which) -> {
                progressBar.setVisibility(View.VISIBLE);
                btnCheckOut.setEnabled(false);
                
                String token = "Bearer " + sessionManager.getAuthToken();
                String scheduleId = currentSelectedSchedule.getId();
                
                Log.d(TAG, "Performing check-in for schedule: " + scheduleId);
                
                apiService.checkIn(token, scheduleId).enqueue(new Callback<com.example.prm392_evcare.models.CheckInOutResponse>() {
                    @Override
                    public void onResponse(Call<com.example.prm392_evcare.models.CheckInOutResponse> call, 
                                         Response<com.example.prm392_evcare.models.CheckInOutResponse> response) {
                        progressBar.setVisibility(View.GONE);
                        btnCheckOut.setEnabled(true);
                        
                        if (response.isSuccessful() && response.body() != null) {
                            com.example.prm392_evcare.models.CheckInOutResponse checkInResponse = response.body();
                            
                            if (checkInResponse.isSuccess()) {
                                // Update current schedule with new data
                                currentSelectedSchedule = checkInResponse.getData();
                                
                                // Update UI
                                updateCheckInOutButton();
                                
                                // Refresh calendar to show updated status
                                fetchTechnicianSchedule();
                                
                                Toast.makeText(TechnicianScheduleActivity.this, 
                                    checkInResponse.getMessage(), 
                                    Toast.LENGTH_SHORT).show();
                                
                                Log.d(TAG, "Check-in successful: " + checkInResponse.getMessage());
                            } else {
                                Toast.makeText(TechnicianScheduleActivity.this, 
                                    "Check-in thất bại: " + checkInResponse.getMessage(), 
                                    Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            String errorMsg = "Check-in thất bại";
                            try {
                                if (response.errorBody() != null) {
                                    errorMsg = response.errorBody().string();
                                    Log.e(TAG, "Check-in error: " + errorMsg);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            Toast.makeText(TechnicianScheduleActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                        }
                    }
                    
                    @Override
                    public void onFailure(Call<com.example.prm392_evcare.models.CheckInOutResponse> call, Throwable t) {
                        progressBar.setVisibility(View.GONE);
                        btnCheckOut.setEnabled(true);
                        Log.e(TAG, "Check-in failed: " + t.getMessage(), t);
                        Toast.makeText(TechnicianScheduleActivity.this, 
                            "Lỗi: " + t.getMessage(), 
                            Toast.LENGTH_SHORT).show();
                    }
                });
            })
            .setNegativeButton("Hủy", null)
            .show();
    }
    
    /**
     * Perform check-out for the current schedule
     */
    private void performCheckOut() {
        if (currentSelectedSchedule == null || currentSelectedSchedule.getId() == null) {
            Toast.makeText(this, "Không tìm thấy lịch làm việc", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Show confirmation dialog
        new AlertDialog.Builder(this)
            .setTitle("Check Out")
            .setMessage("Bạn có chắc muốn check out không?")
            .setPositiveButton("Check Out", (dialog, which) -> {
                progressBar.setVisibility(View.VISIBLE);
                btnCheckOut.setEnabled(false);
                
                String token = "Bearer " + sessionManager.getAuthToken();
                String scheduleId = currentSelectedSchedule.getId();
                
                Log.d(TAG, "Performing check-out for schedule: " + scheduleId);
                
                apiService.checkOut(token, scheduleId).enqueue(new Callback<com.example.prm392_evcare.models.CheckInOutResponse>() {
                    @Override
                    public void onResponse(Call<com.example.prm392_evcare.models.CheckInOutResponse> call, 
                                         Response<com.example.prm392_evcare.models.CheckInOutResponse> response) {
                        progressBar.setVisibility(View.GONE);
                        btnCheckOut.setEnabled(true);
                        
                        if (response.isSuccessful() && response.body() != null) {
                            com.example.prm392_evcare.models.CheckInOutResponse checkOutResponse = response.body();
                            
                            if (checkOutResponse.isSuccess()) {
                                // Update current schedule with new data
                                currentSelectedSchedule = checkOutResponse.getData();
                                
                                // Update UI
                                updateCheckInOutButton();
                                
                                // Refresh calendar to show updated status
                                fetchTechnicianSchedule();
                                
                                Toast.makeText(TechnicianScheduleActivity.this, 
                                    checkOutResponse.getMessage(), 
                                    Toast.LENGTH_SHORT).show();
                                
                                Log.d(TAG, "Check-out successful: " + checkOutResponse.getMessage());
                            } else {
                                Toast.makeText(TechnicianScheduleActivity.this, 
                                    "Check-out thất bại: " + checkOutResponse.getMessage(), 
                                    Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            String errorMsg = "Check-out thất bại";
                            try {
                                if (response.errorBody() != null) {
                                    errorMsg = response.errorBody().string();
                                    Log.e(TAG, "Check-out error: " + errorMsg);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            Toast.makeText(TechnicianScheduleActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                        }
                    }
                    
                    @Override
                    public void onFailure(Call<com.example.prm392_evcare.models.CheckInOutResponse> call, Throwable t) {
                        progressBar.setVisibility(View.GONE);
                        btnCheckOut.setEnabled(true);
                        Log.e(TAG, "Check-out failed: " + t.getMessage(), t);
                        Toast.makeText(TechnicianScheduleActivity.this, 
                            "Lỗi: " + t.getMessage(), 
                            Toast.LENGTH_SHORT).show();
                    }
                });
            })
            .setNegativeButton("Hủy", null)
            .show();
    }
    
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        
        if (itemId == R.id.nav_dashboard) {
            finish(); // Go back to dashboard
        } else if (itemId == R.id.nav_schedule) {
            // Already here
        } else if (itemId == R.id.nav_my_services) {
            startActivity(new Intent(this, TechnicianServicesActivity.class));
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

