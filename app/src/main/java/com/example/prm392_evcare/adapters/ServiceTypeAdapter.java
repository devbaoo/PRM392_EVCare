package com.example.prm392_evcare.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392_evcare.R;
import com.example.prm392_evcare.models.ServiceType;

import java.util.List;

public class ServiceTypeAdapter extends RecyclerView.Adapter<ServiceTypeAdapter.ViewHolder> {

    private List<ServiceType> serviceTypes;
    private OnServiceTypeClickListener listener;

    public interface OnServiceTypeClickListener {
        void onServiceTypeClick(ServiceType serviceType);
    }

    public ServiceTypeAdapter(List<ServiceType> serviceTypes, OnServiceTypeClickListener listener) {
        this.serviceTypes = serviceTypes;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_service_type, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ServiceType serviceType = serviceTypes.get(position);
        
        holder.tvName.setText(serviceType.getName());
        holder.tvDescription.setText(serviceType.getDescription());
        
        // Format price
        if (serviceType.getBasePrice() > 0) {
            holder.tvPrice.setText(serviceType.getFormattedPrice());
            holder.tvPrice.setVisibility(View.VISIBLE);
        } else {
            holder.tvPrice.setVisibility(View.GONE);
        }
        
        // Format duration
        if (serviceType.getEstimatedDuration() > 0) {
            holder.tvDuration.setText(serviceType.getFormattedDuration());
            holder.tvDuration.setVisibility(View.VISIBLE);
        } else {
            holder.tvDuration.setVisibility(View.GONE);
        }
        
        holder.cardView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onServiceTypeClick(serviceType);
            }
        });
    }

    @Override
    public int getItemCount() {
        return serviceTypes != null ? serviceTypes.size() : 0;
    }

    public void updateServiceTypes(List<ServiceType> newServiceTypes) {
        this.serviceTypes = newServiceTypes;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView tvName;
        TextView tvDescription;
        TextView tvPrice;
        TextView tvDuration;

        ViewHolder(View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView);
            tvName = itemView.findViewById(R.id.tvServiceName);
            tvDescription = itemView.findViewById(R.id.tvServiceDescription);
            tvPrice = itemView.findViewById(R.id.tvServicePrice);
            tvDuration = itemView.findViewById(R.id.tvServiceDuration);
        }
    }
}
