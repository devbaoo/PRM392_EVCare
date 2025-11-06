package com.example.prm392_evcare.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392_evcare.R;
import com.example.prm392_evcare.models.Booking;
import com.google.android.material.chip.Chip;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.BookingViewHolder> {

    private Context context;
    private List<Booking> bookings;

    public BookingAdapter(Context context, List<Booking> bookings) {
        this.context = context;
        this.bookings = bookings;
    }

    public void updateBookings(List<Booking> newBookings) {
        this.bookings = newBookings;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_booking, parent, false);
        return new BookingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingViewHolder holder, int position) {
        Booking booking = bookings.get(position);

        // Set booking ID
        holder.tvBookingId.setText("ID: " + booking.getId().substring(0, 8));

        // Set service center name
        if (booking.getServiceCenter() != null) {
            holder.tvServiceCenter.setText(booking.getServiceCenter().getName());
        } else {
            holder.tvServiceCenter.setText("N/A");
        }

        // Set vehicle info
        if (booking.getVehicle() != null && booking.getVehicle().getVehicleInfo() != null) {
            holder.tvVehicle.setText(booking.getVehicle().getVehicleInfo().getFullName());
        } else {
            holder.tvVehicle.setText("N/A");
        }

        // Set booking date
        holder.tvBookingDate.setText(formatDate(booking.getBookingDate()));

        // Set total price
        holder.tvTotalPrice.setText(booking.getFormattedTotalPrice());

        // Set status chip
        setupStatusChip(holder.chipStatus, booking.getStatus());
    }

    @Override
    public int getItemCount() {
        return bookings.size();
    }

    private void setupStatusChip(Chip chip, String status) {
        chip.setText(getStatusText(status));
        
        String statusLower = status != null ? status.toLowerCase() : "";
        
        if (statusLower.contains("pending") || statusLower.equals("pending_confirmation")) {
            chip.setChipBackgroundColorResource(android.R.color.holo_orange_light);
        } else if (statusLower.contains("confirmed")) {
            chip.setChipBackgroundColorResource(android.R.color.holo_blue_light);
        } else if (statusLower.contains("completed")) {
            chip.setChipBackgroundColorResource(android.R.color.holo_green_light);
        } else if (statusLower.contains("cancelled")) {
            chip.setChipBackgroundColorResource(android.R.color.holo_red_light);
        } else if (statusLower.contains("quote")) {
            chip.setChipBackgroundColorResource(android.R.color.holo_purple);
        } else {
            chip.setChipBackgroundColorResource(android.R.color.darker_gray);
        }
    }

    private String getStatusText(String status) {
        if (status == null) return "Unknown";
        
        String statusLower = status.toLowerCase();
        
        // Handle all possible statuses from API
        if (statusLower.equals("pending_confirmation")) return "Chờ xác nhận";
        if (statusLower.equals("confirmed")) return "Đã xác nhận";
        if (statusLower.equals("in_progress")) return "Đang thực hiện";
        if (statusLower.equals("completed")) return "Hoàn thành";
        if (statusLower.equals("cancelled")) return "Đã hủy";
        if (statusLower.equals("quote_provided")) return "Đã báo giá";
        if (statusLower.equals("quote_approved")) return "Đã duyệt báo giá";
        if (statusLower.equals("quote_rejected")) return "Từ chối báo giá";
        
        // Fallback
        if (statusLower.contains("pending")) return "Chờ xác nhận";
        if (statusLower.contains("confirmed")) return "Đã xác nhận";
        if (statusLower.contains("completed")) return "Hoàn thành";
        if (statusLower.contains("cancelled")) return "Đã hủy";
        
        return status;
    }

    private String formatDate(String dateString) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.US);
            Date date = inputFormat.parse(dateString);
            return date != null ? outputFormat.format(date) : dateString;
        } catch (ParseException e) {
            return dateString;
        }
    }

    static class BookingViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView tvBookingId;
        TextView tvServiceCenter;
        TextView tvVehicle;
        TextView tvBookingDate;
        TextView tvTotalPrice;
        Chip chipStatus;

        public BookingViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView);
            tvBookingId = itemView.findViewById(R.id.tvBookingId);
            tvServiceCenter = itemView.findViewById(R.id.tvServiceCenter);
            tvVehicle = itemView.findViewById(R.id.tvVehicle);
            tvBookingDate = itemView.findViewById(R.id.tvBookingDate);
            tvTotalPrice = itemView.findViewById(R.id.tvTotalPrice);
            chipStatus = itemView.findViewById(R.id.chipStatus);
        }
    }
}
