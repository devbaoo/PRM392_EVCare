package com.example.prm392_evcare;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.prm392_evcare.api.ApiClient;
import com.example.prm392_evcare.api.ApiService;
import com.example.prm392_evcare.models.BookingResponse;
import com.example.prm392_evcare.models.CreateBookingRequest;
import com.example.prm392_evcare.models.ServiceType;
import com.example.prm392_evcare.models.Vehicle;
import com.example.prm392_evcare.utils.SessionManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConfirmBookingActivity extends AppCompatActivity {

    private TextInputEditText etSelectDate;
    private TextInputEditText etServiceDescription;
    private TextInputEditText etSelectTime;
    private RadioGroup rgPaymentMethod;
    private RadioButton rbAtCenter;
    private RadioButton rbOnline;
    private MaterialButton btnBack;
    private MaterialButton btnConfirm;

    private TextView tvVehicleName;
    private TextView tvServiceName;
    private TextView tvServiceCenter;
    private TextView tvSelectedDate;
    private TextView tvPaymentMethod;
    private TextView tvServicePrice;

    private Vehicle vehicle;
    private String serviceCenterId;
    private String serviceCenterName;
    private ServiceType serviceType;
    private Calendar selectedDate;
    private boolean isInspectionOnly;
    private Integer selectedHour = null;
    private Integer selectedMinute = null;
    
    private ApiService apiService;
    private SessionManager sessionManager;
    private Dialog progressDialog;
    private Dialog statusDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_booking);

        // Get data from intent
        vehicle = (Vehicle) getIntent().getSerializableExtra("vehicle");
        serviceCenterId = getIntent().getStringExtra("service_center_id");
        serviceCenterName = getIntent().getStringExtra("service_center_name");
        serviceType = (ServiceType) getIntent().getSerializableExtra("service_type");
        isInspectionOnly = getIntent().getBooleanExtra("is_inspection_only", false);

        // Initialize API service and session
        apiService = ApiClient.getClient().create(ApiService.class);
        sessionManager = new SessionManager(this);

        setupToolbar();
        initViews();
        populateData(isInspectionOnly);
        setupListeners();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Thông tin cuối cùng");
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void initViews() {
        etSelectDate = findViewById(R.id.etSelectDate);
        etSelectTime = findViewById(R.id.etSelectTime);
        etServiceDescription = findViewById(R.id.etServiceDescription);
        rgPaymentMethod = findViewById(R.id.rgPaymentMethod);
        rbAtCenter = findViewById(R.id.rbAtCenter);
        rbOnline = findViewById(R.id.rbOnline);
        btnBack = findViewById(R.id.btnBack);
        btnConfirm = findViewById(R.id.btnConfirm);

        // Summary views
        tvVehicleName = findViewById(R.id.tvVehicleName);
        tvServiceName = findViewById(R.id.tvServiceName);
        tvServiceCenter = findViewById(R.id.tvServiceCenter);
        tvSelectedDate = findViewById(R.id.tvSelectedDate);
        tvPaymentMethod = findViewById(R.id.tvPaymentMethod);
        tvServicePrice = findViewById(R.id.tvServicePrice);

        selectedDate = Calendar.getInstance();
    }

    private void populateData(boolean isInspectionOnly) {
        // Vehicle info
        if (vehicle != null) {
            tvVehicleName.setText(vehicle.getFullName());
        }

        // Service info
        if (isInspectionOnly) {
            tvServiceName.setText("Chỉ kiểm tra tình trạng xe");
            tvServicePrice.setText("Liên hệ");
            etServiceDescription.setHint("Mô tả chi tiết vấn đề cần kiểm tra...");
        } else if (serviceType != null) {
            tvServiceName.setText(serviceType.getName());
            tvServicePrice.setText(serviceType.getFormattedPrice());
            etServiceDescription.setHint("Mô tả chi tiết về vấn đề cần sửa chữa hoặc dịch vụ cần thực hiện...");
        }

        // Service center
        if (serviceCenterName != null) {
            tvServiceCenter.setText(serviceCenterName);
        }

        // Default date
        tvSelectedDate.setText("Chưa chọn");
        
        // Default payment method
        rbAtCenter.setChecked(true);
        tvPaymentMethod.setText("Tại trung tâm");
    }

    private void setupListeners() {
        etSelectDate.setOnClickListener(v -> showDatePicker());
        etSelectTime.setOnClickListener(v -> showTimePicker());

        rgPaymentMethod.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbAtCenter) {
                tvPaymentMethod.setText("Tại trung tâm");
            } else if (checkedId == R.id.rbOnline) {
                tvPaymentMethod.setText("Trực tuyến");
            }
        });

        btnBack.setOnClickListener(v -> finish());

        btnConfirm.setOnClickListener(v -> confirmBooking());
    }

    private void showDatePicker() {
        Calendar minDate = Calendar.getInstance();
        minDate.add(Calendar.DAY_OF_MONTH, 1); // Minimum tomorrow

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    selectedDate.set(Calendar.YEAR, year);
                    selectedDate.set(Calendar.MONTH, month);
                    selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", new Locale("vi", "VN"));
                    String formattedDate = sdf.format(selectedDate.getTime());
                    etSelectDate.setText(formattedDate);
                    // Update summary with time if selected
                    if (selectedHour != null) {
                        tvSelectedDate.setText(formattedDate + " • " + formatTime(selectedHour, selectedMinute != null ? selectedMinute : 0));
                    } else {
                        tvSelectedDate.setText(formattedDate);
                    }
                },
                selectedDate.get(Calendar.YEAR),
                selectedDate.get(Calendar.MONTH),
                selectedDate.get(Calendar.DAY_OF_MONTH)
        );
        
        datePickerDialog.getDatePicker().setMinDate(minDate.getTimeInMillis());
        datePickerDialog.show();
    }

    private void showTimePicker() {
        int hour = selectedHour != null ? selectedHour : 9;
        int minute = selectedMinute != null ? selectedMinute : 0;

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                (view, hourOfDay, minuteOfHour) -> {
                    selectedHour = hourOfDay;
                    selectedMinute = minuteOfHour;

                    String timeTxt = formatTime(hourOfDay, minuteOfHour);
                    etSelectTime.setText(timeTxt);

                    // Update summary label combining date and time
                    CharSequence dateText = etSelectDate.getText();
                    if (dateText != null && dateText.length() > 0) {
                        tvSelectedDate.setText(dateText + " • " + timeTxt);
                    } else {
                        tvSelectedDate.setText(timeTxt);
                    }
                },
                hour,
                minute,
                true
        );

        timePickerDialog.show();
    }

    private String formatTime(int hour, int minute) {
        return String.format(Locale.getDefault(), "%02d:%02d", hour, minute);
    }

    private void confirmBooking() {
        // Validate
        if (etSelectDate.getText() == null || etSelectDate.getText().toString().isEmpty()) {
            Toast.makeText(this, "Vui lòng chọn ngày hẹn", Toast.LENGTH_SHORT).show();
            return;
        }

        String paymentPreference = rbAtCenter.isChecked() ? "offline" : "online";
        String description = etServiceDescription.getText() != null ? 
                            etServiceDescription.getText().toString() : "";

        // Create booking request
        showProgressDialog();
        
        // Format date to ISO 8601 format (yyyy-MM-dd)
        SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        String appointmentDate = isoFormat.format(selectedDate.getTime());
        
        // Validate time selection
        if (selectedHour == null) {
            hideProgressDialog();
            Toast.makeText(this, "Vui lòng chọn giờ hẹn", Toast.LENGTH_SHORT).show();
            return;
        }

        // Format appointment time as HH:mm (e.g., "21:00")
        String appointmentTime = formatTime(selectedHour, selectedMinute != null ? selectedMinute : 0);
        
        // Get customerId from session
        String customerId = sessionManager.getUserId();
        
        // Get serviceTypeId (null if inspection only)
        String serviceTypeId = (!isInspectionOnly && serviceType != null) ? serviceType.getId() : null;
        
        CreateBookingRequest request = new CreateBookingRequest(
            customerId,
            vehicle.getId(),
            serviceCenterId,
            serviceTypeId,
            appointmentDate,
            appointmentTime,
            description,
            paymentPreference,
            isInspectionOnly
        );
        
        String token = "Bearer " + sessionManager.getAuthToken();
        Call<BookingResponse> call = apiService.createBooking(token, request);
        
        call.enqueue(new Callback<BookingResponse>() {
            @Override
            public void onResponse(Call<BookingResponse> call, Response<BookingResponse> response) {
                hideProgressDialog();
                
                if (response.isSuccessful() && response.body() != null) {
                    BookingResponse bookingResponse = response.body();
                    
                    Log.d("ConfirmBooking", "Booking success: " + bookingResponse.isSuccess());
                    Log.d("ConfirmBooking", "Requires payment: " + bookingResponse.requiresPayment());
                    
                    if (bookingResponse.isSuccess()) {
                        // Check if payment is required (online payment)
                        BookingResponse.PaymentInfo paymentInfo = bookingResponse.getPaymentInfo();
                        
                        Log.d("ConfirmBooking", "Payment info: " + (paymentInfo != null ? "EXISTS" : "NULL"));
                        if (paymentInfo != null) {
                            Log.d("ConfirmBooking", "Payment link: " + paymentInfo.getPaymentLink());
                            Log.d("ConfirmBooking", "Checkout URL: " + paymentInfo.getCheckoutUrl());
                        }
                        
                        if (Boolean.TRUE.equals(bookingResponse.requiresPayment()) && paymentInfo != null) {
                            // Has payment link - show payment dialog
                            Log.d("ConfirmBooking", "Showing payment dialog");
                            showPaymentDialog(paymentInfo);
                        } else {
                            // No payment required or offline payment - show success
                            Log.d("ConfirmBooking", "No payment required, showing success");
                            String successMessage = bookingResponse.getMessage() != null ? 
                                bookingResponse.getMessage() : 
                                "Đặt lịch bảo dưỡng thành công! Vui lòng kiểm tra lịch sử đặt lịch.";
                            showStatusDialog(true, "Thành công", successMessage);
                        }
                    } else {
                        showStatusDialog(false, "Thất bại",
                            bookingResponse.getMessage() != null ? 
                            bookingResponse.getMessage() : "Không thể đặt lịch");
                    }
                } else {
                    String errorMsg = "Không thể đặt lịch. Vui lòng thử lại.";
                    try {
                        if (response.errorBody() != null) {
                            errorMsg = response.errorBody().string();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    showStatusDialog(false, "Thất bại", errorMsg);
                }
            }

            @Override
            public void onFailure(Call<BookingResponse> call, Throwable t) {
                hideProgressDialog();
                showStatusDialog(false, "Lỗi kết nối",
                    "Không thể kết nối đến máy chủ: " + t.getMessage());
            }
        });
    }
    
    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new Dialog(this);
            progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            progressDialog.setContentView(R.layout.dialog_login_progress);
            progressDialog.setCancelable(false);
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        progressDialog.show();
    }

    private void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
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
            ivStatus.setColorFilter(getResources().getColor(R.color.success_color));
            tvStatus.setTextColor(getResources().getColor(R.color.success_color));
        } else {
            ivStatus.setImageResource(R.drawable.ic_error);
            ivStatus.setColorFilter(getResources().getColor(R.color.error_color));
            tvStatus.setTextColor(getResources().getColor(R.color.error_color));
        }

        tvStatus.setText(status);
        tvMessage.setText(message);

        btnOk.setOnClickListener(v -> {
            statusDialog.dismiss();
            if (isSuccess) {
                finish();
            }
        });

        statusDialog.show();
    }
    
    private void showPaymentDialog(BookingResponse.PaymentInfo paymentInfo) {
        Dialog paymentDialog = new Dialog(this);
        paymentDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        paymentDialog.setContentView(R.layout.dialog_login_status);
        paymentDialog.setCancelable(false);
        paymentDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        ImageView ivStatus = paymentDialog.findViewById(R.id.ivStatus);
        TextView tvStatus = paymentDialog.findViewById(R.id.tvStatus);
        TextView tvMessage = paymentDialog.findViewById(R.id.tvMessage);
        MaterialButton btnOk = paymentDialog.findViewById(R.id.btnOk);

        ivStatus.setImageResource(R.drawable.ic_check_circle);
        ivStatus.setColorFilter(getResources().getColor(R.color.success_color));
        tvStatus.setTextColor(getResources().getColor(R.color.success_color));
        tvStatus.setText("Đặt lịch thành công!");
        
        String message = "Vui lòng thanh toán đặt cọc " + 
                        String.format("%,.0f VND", paymentInfo.getAmount()) + 
                        "\n\nSau khi thanh toán xong, vui lòng quay lại ứng dụng.";
        tvMessage.setText(message);
        
        btnOk.setText("Thanh toán ngay");
        btnOk.setOnClickListener(v -> {
            paymentDialog.dismiss();
            // Use checkoutUrl if available, otherwise use paymentLink
            String paymentUrl = paymentInfo.getCheckoutUrl() != null && !paymentInfo.getCheckoutUrl().isEmpty() 
                ? paymentInfo.getCheckoutUrl() 
                : paymentInfo.getPaymentLink();

            Log.d("ConfirmBooking", "Opening payment URL (in-app): " + paymentUrl);
            // Open internal WebView to reliably intercept success/cancel
            Intent i = new Intent(this, PaymentWebViewActivity.class);
            i.putExtra(PaymentWebViewActivity.EXTRA_PAYMENT_URL, paymentUrl);
            // booking id optional; if needed, pass from response earlier by capturing
            startActivity(i);
        });

        paymentDialog.show();
    }
    
    private void openPaymentLink(String paymentLink) {
        try {
            Log.d("ConfirmBooking", "Opening payment link: " + paymentLink);
            
            // Open payment link in browser
            android.content.Intent browserIntent = new android.content.Intent(
                android.content.Intent.ACTION_VIEW, 
                android.net.Uri.parse(paymentLink)
            );
            startActivity(browserIntent);
            
            // Just finish this activity - user will complete payment in browser
            // and return to app via deep link or manually
            Toast.makeText(this, "Vui lòng hoàn tất thanh toán trong trình duyệt", 
                Toast.LENGTH_LONG).show();
            finish();
        } catch (Exception e) {
            Log.e("ConfirmBooking", "Error opening payment link", e);
            Toast.makeText(this, "Không thể mở link thanh toán: " + e.getMessage(), 
                Toast.LENGTH_LONG).show();
        }
    }
}
