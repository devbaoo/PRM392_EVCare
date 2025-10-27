package com.example.prm392_evcare.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392_evcare.R;

import java.util.ArrayList;
import java.util.List;

public class FeatureAdapter extends RecyclerView.Adapter<FeatureAdapter.ViewHolder> {
    
    private List<Feature> features = new ArrayList<>();
    
    public FeatureAdapter() {
        initializeFeatures();
    }
    
    private void initializeFeatures() {
        features.add(new Feature("Customer Tracking", "Automated maintenance reminders and vehicle tracking", android.R.drawable.ic_menu_agenda));
        features.add(new Feature("Online Booking", "Easy service scheduling with real-time status updates", android.R.drawable.ic_menu_edit));
        features.add(new Feature("Service History", "Track all your maintenance and service records", android.R.drawable.ic_menu_view));
        features.add(new Feature("Inventory Management", "AI-powered parts management and stock tracking", android.R.drawable.ic_menu_more));
        features.add(new Feature("Staff Management", "Technician scheduling and performance tracking", android.R.drawable.ic_menu_myplaces));
        features.add(new Feature("Analytics & Reporting", "Comprehensive business insights and reporting", android.R.drawable.ic_menu_recent_history));
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_feature, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Feature feature = features.get(position);
        holder.tvFeatureTitle.setText(feature.getTitle());
        holder.tvFeatureDescription.setText(feature.getDescription());
        holder.ivFeatureIcon.setImageResource(feature.getIconResId());
    }

    @Override
    public int getItemCount() {
        return features.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivFeatureIcon;
        TextView tvFeatureTitle;
        TextView tvFeatureDescription;
        ImageView ivArrow;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivFeatureIcon = itemView.findViewById(R.id.ivFeatureIcon);
            tvFeatureTitle = itemView.findViewById(R.id.tvFeatureTitle);
            tvFeatureDescription = itemView.findViewById(R.id.tvFeatureDescription);
            ivArrow = itemView.findViewById(R.id.ivArrow);
        }
    }
    
    private static class Feature {
        private String title;
        private String description;
        private int iconResId;
        
        public Feature(String title, String description, int iconResId) {
            this.title = title;
            this.description = description;
            this.iconResId = iconResId;
        }
        
        public String getTitle() {
            return title;
        }
        
        public String getDescription() {
            return description;
        }
        
        public int getIconResId() {
            return iconResId;
        }
    }
}

