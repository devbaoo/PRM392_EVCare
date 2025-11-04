package com.example.prm392_evcare;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.prm392_evcare.models.ServiceType;
import com.example.prm392_evcare.models.Vehicle;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class ConfirmBookingActivity extends AppCompatActivity {

    private TextInputEditText etSelectDate;
    private TextInputEditText etServiceDescription;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_booking);

        // Get data from intent
        vehicle = (Vehicle) getIntent().getSerializableExtra("vehicle");
        serviceCenterId = getIntent().getStringExtra("service_center_id");
        serviceCenterName = getIntent().getStringExtra("service_center_name");
        serviceType = (ServiceType) getIntent().getSerializableExtra("service_type");
        boolean isInspectionOnly = getIntent().getBooleanExtra("is_inspection_only", false);

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
                    tvSelectedDate.setText(formattedDate);
                },
                selectedDate.get(Calendar.YEAR),
                selectedDate.get(Calendar.MONTH),
                selectedDate.get(Calendar.DAY_OF_MONTH)
        );
        
        datePickerDialog.getDatePicker().setMinDate(minDate.getTimeInMillis());
        datePickerDialog.show();
    }

    private void confirmBooking() {
        // Validate
        if (etSelectDate.getText() == null || etSelectDate.getText().toString().isEmpty()) {
            Toast.makeText(this, "Vui lòng chọn ngày hẹn", Toast.LENGTH_SHORT).show();
            return;
        }

        String paymentMethod = rbAtCenter.isChecked() ? "offline" : "online";
        String description = etServiceDescription.getText() != null ? 
                            etServiceDescription.getText().toString() : "";

        // TODO: Call API to create booking
        Toast.makeText(this, "Đang tạo lịch hẹn...", Toast.LENGTH_SHORT).show();
        
        // For now, just show success and go back
        Toast.makeText(this, "Đặt lịch thành công!", Toast.LENGTH_LONG).show();
        finish();
    }
}
