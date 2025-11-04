package com.example.prm392_evcare.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392_evcare.R;
import com.example.prm392_evcare.models.ServiceCenter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.google.android.material.chip.Chip;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ServiceCenterAdapter extends RecyclerView.Adapter<ServiceCenterAdapter.ViewHolder> {
    
    private List<ServiceCenter> serviceCenters;
    private Context context;
    private OnServiceCenterClickListener listener;

    public interface OnServiceCenterClickListener {
        void onServiceCenterClick(ServiceCenter serviceCenter);
    }

    public ServiceCenterAdapter(List<ServiceCenter> serviceCenters, Context context, OnServiceCenterClickListener listener) {
        this.serviceCenters = serviceCenters;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_service_center, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ServiceCenter serviceCenter = serviceCenters.get(position);
        
        // Set name
        holder.tvServiceCenterName.setText(serviceCenter.getName());
        
        // Set description
        if (serviceCenter.getDescription() != null && !serviceCenter.getDescription().isEmpty()) {
            holder.tvDescription.setText(serviceCenter.getDescription());
            holder.tvDescription.setVisibility(View.VISIBLE);
        } else {
            holder.tvDescription.setVisibility(View.GONE);
        }
        
        // Set address
        String address = serviceCenter.getFullAddress();
        if (address != null && !address.isEmpty()) {
            holder.tvServiceCenterAddress.setText(address);
        } else {
            holder.tvServiceCenterAddress.setText("Address not available");
        }
        
        // Set distance
        if (serviceCenter.getDistance() > 0) {
            DecimalFormat df = new DecimalFormat("#.#");
            holder.tvDistance.setText(df.format(serviceCenter.getDistance()) + " km away");
            holder.layoutDistance.setVisibility(View.VISIBLE);
        } else {
            holder.layoutDistance.setVisibility(View.GONE);
        }
        
        // Set rating
        if (serviceCenter.getRating() != null) {
            double ratingValue = serviceCenter.getRating().getAverage();
            int ratingCount = serviceCenter.getRating().getCount();
            if (ratingValue > 0) {
                holder.tvRating.setText(String.format(Locale.US, "%.1f", ratingValue));
            } else {
                holder.tvRating.setText("N/A");
            }
        } else {
            holder.tvRating.setText("N/A");
        }
        
        // Set operating hours and status
        String operatingHoursText = getCurrentOperatingHours(serviceCenter);
        holder.tvOperatingHours.setText(operatingHoursText);
        
        // Set status chip
        boolean isOpen = isServiceCenterOpen(serviceCenter);
        if (isOpen) {
            holder.chipStatus.setText("Open");
            holder.chipStatus.setChipBackgroundColorResource(android.R.color.holo_green_dark);
        } else {
            holder.chipStatus.setText("Closed");
            holder.chipStatus.setChipBackgroundColorResource(android.R.color.holo_red_dark);
        }
        
        // Set phone
        if (serviceCenter.getContact() != null && serviceCenter.getContact().getPhone() != null) {
            holder.tvPhone.setText(serviceCenter.getContact().getPhone());
        } else {
            holder.tvPhone.setText("N/A");
        }
        
        // Set staff count
        if (serviceCenter.getStaff() != null) {
            int staffCount = serviceCenter.getStaff().size();
            holder.tvStaffCount.setText(staffCount + " Staff");
        } else {
            holder.tvStaffCount.setText("0 Staff");
        }
        
        // Load image
        String imageUrl = serviceCenter.getPrimaryImageUrl();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(context)
                .load(imageUrl)
                .placeholder(android.R.drawable.ic_menu_gallery)
                .error(android.R.drawable.ic_menu_gallery)
                .transition(DrawableTransitionOptions.withCrossFade())
                .centerCrop()
                .into(holder.ivServiceCenterImage);
        } else {
            holder.ivServiceCenterImage.setImageResource(android.R.drawable.ic_menu_gallery);
        }
        
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onServiceCenterClick(serviceCenter);
            }
        });
    }

    private String getCurrentOperatingHours(ServiceCenter serviceCenter) {
        if (serviceCenter.getOperatingHours() == null) {
            return "Hours not available";
        }
        
        Calendar calendar = Calendar.getInstance();
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        
        String dayName = "";
        switch (dayOfWeek) {
            case Calendar.MONDAY:
                dayName = "monday";
                break;
            case Calendar.TUESDAY:
                dayName = "tuesday";
                break;
            case Calendar.WEDNESDAY:
                dayName = "wednesday";
                break;
            case Calendar.THURSDAY:
                dayName = "thursday";
                break;
            case Calendar.FRIDAY:
                dayName = "friday";
                break;
            case Calendar.SATURDAY:
                dayName = "saturday";
                break;
            case Calendar.SUNDAY:
                dayName = "sunday";
                break;
        }
        
        ServiceCenter.OperatingHours.DaySchedule schedule = serviceCenter.getOperatingHours().getScheduleForDay(dayName);
        if (schedule != null && schedule.isOpen()) {
            return schedule.getOpen() + " - " + schedule.getClose() + " (Open)";
        } else {
            return "Closed today";
        }
    }

    private boolean isServiceCenterOpen(ServiceCenter serviceCenter) {
        if (serviceCenter.getOperatingHours() == null) {
            return false;
        }
        
        Calendar calendar = Calendar.getInstance();
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        
        String dayName = "";
        switch (dayOfWeek) {
            case Calendar.MONDAY:
                dayName = "monday";
                break;
            case Calendar.TUESDAY:
                dayName = "tuesday";
                break;
            case Calendar.WEDNESDAY:
                dayName = "wednesday";
                break;
            case Calendar.THURSDAY:
                dayName = "thursday";
                break;
            case Calendar.FRIDAY:
                dayName = "friday";
                break;
            case Calendar.SATURDAY:
                dayName = "saturday";
                break;
            case Calendar.SUNDAY:
                dayName = "sunday";
                break;
        }
        
        ServiceCenter.OperatingHours.DaySchedule schedule = serviceCenter.getOperatingHours().getScheduleForDay(dayName);
        return schedule != null && schedule.isOpen();
    }

    @Override
    public int getItemCount() {
        return serviceCenters != null ? serviceCenters.size() : 0;
    }

    public void updateServiceCenters(List<ServiceCenter> newServiceCenters) {
        this.serviceCenters = newServiceCenters;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvServiceCenterName;
        TextView tvDescription;
        TextView tvServiceCenterAddress;
        TextView tvDistance;
        TextView tvOperatingHours;
        TextView tvPhone;
        TextView tvStaffCount;
        ImageView ivRating;
        TextView tvRating;
        ImageView ivServiceCenterImage;
        Chip chipStatus;
        View layoutDistance;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvServiceCenterName = itemView.findViewById(R.id.tvServiceCenterName);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvServiceCenterAddress = itemView.findViewById(R.id.tvServiceCenterAddress);
            tvDistance = itemView.findViewById(R.id.tvDistance);
            tvOperatingHours = itemView.findViewById(R.id.tvOperatingHours);
            tvPhone = itemView.findViewById(R.id.tvPhone);
            tvStaffCount = itemView.findViewById(R.id.tvStaffCount);
            ivServiceCenterImage = itemView.findViewById(R.id.ivServiceCenterImage);
            ivRating = itemView.findViewById(R.id.ivRating);
            tvRating = itemView.findViewById(R.id.tvRating);
            chipStatus = itemView.findViewById(R.id.chipStatus);
            layoutDistance = itemView.findViewById(R.id.layoutDistance);
        }
    }
}

