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

import java.text.DecimalFormat;
import java.util.List;

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
        
        holder.tvServiceCenterName.setText(serviceCenter.getName());
        
        // Set address
        String address = serviceCenter.getFullAddress();
        if (address != null && !address.isEmpty()) {
            holder.tvServiceCenterAddress.setText(address);
        } else {
            holder.tvServiceCenterAddress.setText("Address not available");
        }
        
        // Set rating
        if (serviceCenter.getRating() != null) {
            double ratingValue = serviceCenter.getRating().getAverage();
            holder.tvRating.setText(String.valueOf(ratingValue));
        } else {
            holder.tvRating.setText("0.0");
        }
        
        // Format distance
        if (serviceCenter.getDistance() > 0) {
            DecimalFormat df = new DecimalFormat("#.#");
            holder.tvDistance.setText(df.format(serviceCenter.getDistance()) + " km");
        } else {
            holder.tvDistance.setText("Distance unknown");
        }
        
        // Set status (default to "Open" for now)
        if (holder.tvStatus != null) {
            holder.tvStatus.setText("Open");
        }
        
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onServiceCenterClick(serviceCenter);
            }
        });
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
        TextView tvServiceCenterAddress;
        ImageView ivRating;
        TextView tvRating;
        TextView tvDistance;
        TextView tvStatus;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvServiceCenterName = itemView.findViewById(R.id.tvServiceCenterName);
            tvServiceCenterAddress = itemView.findViewById(R.id.tvServiceCenterAddress);
            ivRating = itemView.findViewById(R.id.ivRating);
            tvRating = itemView.findViewById(R.id.tvRating);
            tvDistance = itemView.findViewById(R.id.tvDistance);
            tvStatus = itemView.findViewById(R.id.tvStatus);
        }
    }
}

