package com.example.prm392_evcare;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.button.MaterialButton;

public class PaymentOutcomeActivity extends AppCompatActivity {

    public static final String EXTRA_STATUS = "status"; // success | cancel | failed
    public static final String EXTRA_MESSAGE = "message";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_outcome);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Thanh toán đặt cọc");
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        ImageView ivStatus = findViewById(R.id.ivStatus);
        TextView tvTitle = findViewById(R.id.tvTitle);
        TextView tvSubtitle = findViewById(R.id.tvSubtitle);
        MaterialButton btnGoHistory = findViewById(R.id.btnGoHistory);

        String status = getIntent().getStringExtra(EXTRA_STATUS);
        String message = getIntent().getStringExtra(EXTRA_MESSAGE);

        if ("success".equalsIgnoreCase(status) || "paid".equalsIgnoreCase(status)) {
            ivStatus.setImageResource(R.drawable.ic_check_circle);
            ivStatus.setColorFilter(getResources().getColor(R.color.success_color), PorterDuff.Mode.SRC_IN);
            tvTitle.setText("Thanh toán thành công");
            tvSubtitle.setText(message != null ? message : "Giao dịch đã hoàn tất.");
            btnGoHistory.setText("Về lịch sử đặt lịch");
            btnGoHistory.setBackgroundTintList(getResources().getColorStateList(R.color.primary));
        } else if ("cancel".equalsIgnoreCase(status) || "cancelled".equalsIgnoreCase(status)) {
            ivStatus.setImageResource(R.drawable.ic_error);
            ivStatus.setColorFilter(getResources().getColor(R.color.error_color), PorterDuff.Mode.SRC_IN);
            tvTitle.setText("Thanh toán đã hủy");
            tvSubtitle.setText(message != null ? message : "Bạn đã hủy giao dịch.");
            btnGoHistory.setText("Về lịch sử đặt lịch");
            btnGoHistory.setBackgroundTintList(getResources().getColorStateList(R.color.primary));
        } else {
            ivStatus.setImageResource(R.drawable.ic_error);
            ivStatus.setColorFilter(getResources().getColor(R.color.error_color), PorterDuff.Mode.SRC_IN);
            tvTitle.setText("Thanh toán thất bại");
            tvSubtitle.setText(message != null ? message : "Giao dịch không thể hoàn thành");
            btnGoHistory.setText("Về lịch sử đặt lịch");
            btnGoHistory.setBackgroundTintList(getResources().getColorStateList(R.color.primary));
        }

        btnGoHistory.setOnClickListener(v -> {
            android.content.Intent i = new android.content.Intent(this, BookingHistoryActivity.class);
            i.setFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK | android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            finish();
        });
    }
}