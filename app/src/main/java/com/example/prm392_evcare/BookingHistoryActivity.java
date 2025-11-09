package com.example.prm392_evcare;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392_evcare.adapters.BookingAdapter;
import com.example.prm392_evcare.api.ApiClient;
import com.example.prm392_evcare.api.ApiService;
import com.example.prm392_evcare.models.Booking;
import com.example.prm392_evcare.models.BookingResponse;
import com.example.prm392_evcare.utils.SessionManager;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookingHistoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private BookingAdapter bookingAdapter;
    private ProgressBar progressBar;
    private TextView tvEmptyState;
    private ChipGroup chipGroupStatus;
    private Chip chipAll, chipPending, chipConfirmed, chipCompleted, chipCancelled;

    private ApiService apiService;
    private SessionManager sessionManager;
    private List<Booking> allBookings = new ArrayList<>();
    private String currentFilter = "all";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_history);

        // Initialize API and session
        apiService = ApiClient.getClient().create(ApiService.class);
        sessionManager = new SessionManager(this);

        // Setup toolbar
        setupToolbar();

        // Initialize views
        initViews();

        // Setup RecyclerView
        setupRecyclerView();

        // Setup filter chips
        setupFilterChips();

        // Load bookings
        loadBookings();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Lịch sử đặt lịch");
        }
        // If opened right after payment, pressing back should take user to Home instead of exiting.
        final boolean fromPaymentFlow = getIntent().getBooleanExtra("fromPaymentFlow", false);
        toolbar.setNavigationOnClickListener(v -> {
            if (fromPaymentFlow) {
                android.content.Intent intent = new android.content.Intent(BookingHistoryActivity.this, HomeActivity.class);
                // Start Home and finish this screen so we don't go back to payment pages
                startActivity(intent);
                finish();
            } else {
                finish();
            }
        });
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recyclerViewBookings);
        progressBar = findViewById(R.id.progressBar);
        tvEmptyState = findViewById(R.id.tvEmptyState);
        chipGroupStatus = findViewById(R.id.chipGroupStatus);
        chipAll = findViewById(R.id.chipAll);
        chipPending = findViewById(R.id.chipPending);
        chipConfirmed = findViewById(R.id.chipConfirmed);
        chipCompleted = findViewById(R.id.chipCompleted);
        chipCancelled = findViewById(R.id.chipCancelled);
    }

    private void setupRecyclerView() {
        bookingAdapter = new BookingAdapter(this, new ArrayList<>());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(bookingAdapter);
    }

    private void setupFilterChips() {
        chipAll.setOnClickListener(v -> filterBookings("all"));
        chipPending.setOnClickListener(v -> filterBookings("pending"));
        chipConfirmed.setOnClickListener(v -> filterBookings("confirmed"));
        chipCompleted.setOnClickListener(v -> filterBookings("completed"));
        chipCancelled.setOnClickListener(v -> filterBookings("cancelled"));
    }

    private void loadBookings() {
        progressBar.setVisibility(View.VISIBLE);
        tvEmptyState.setVisibility(View.GONE);

        String token = sessionManager.getAuthToken();
        if (token == null || token.isEmpty()) {
            Toast.makeText(this, "Session expired", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Call<BookingResponse> call = apiService.getMyBookings("Bearer " + token);
        call.enqueue(new Callback<BookingResponse>() {
            @Override
            public void onResponse(@NonNull Call<BookingResponse> call, 
                                 @NonNull Response<BookingResponse> response) {
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    BookingResponse bookingResponse = response.body();
                    if (bookingResponse.isSuccess()) {
                        allBookings = bookingResponse.getBookings();
                        filterBookings(currentFilter);
                    } else {
                        showEmptyState("Không thể tải lịch hẹn");
                    }
                } else {
                    showEmptyState("Lỗi: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<BookingResponse> call, @NonNull Throwable t) {
                progressBar.setVisibility(View.GONE);
                showEmptyState("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    private void filterBookings(String status) {
        currentFilter = status;
        List<Booking> filteredBookings = new ArrayList<>();

        if (status.equals("all")) {
            filteredBookings = new ArrayList<>(allBookings);
        } else {
            for (Booking booking : allBookings) {
                if (booking.getStatus().equalsIgnoreCase(status)) {
                    filteredBookings.add(booking);
                }
            }
        }

        if (filteredBookings.isEmpty()) {
            showEmptyState("Không có lịch hẹn nào");
        } else {
            tvEmptyState.setVisibility(View.GONE);
            bookingAdapter.updateBookings(filteredBookings);
        }
    }

    private void showEmptyState(String message) {
        tvEmptyState.setText(message);
        tvEmptyState.setVisibility(View.VISIBLE);
        bookingAdapter.updateBookings(new ArrayList<>());
    }
}
