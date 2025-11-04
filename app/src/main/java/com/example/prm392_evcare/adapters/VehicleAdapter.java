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
import com.example.prm392_evcare.models.Vehicle;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class VehicleAdapter extends RecyclerView.Adapter<VehicleAdapter.VehicleViewHolder> {

    private Context context;
    private List<Vehicle> vehicleList;
    private OnVehicleClickListener listener;

    public interface OnVehicleClickListener {
        void onVehicleClick(Vehicle vehicle);
        void onVehicleEdit(Vehicle vehicle);
        void onVehicleDelete(Vehicle vehicle);
    }

    public VehicleAdapter(Context context, List<Vehicle> vehicleList, OnVehicleClickListener listener) {
        this.context = context;
        this.vehicleList = vehicleList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public VehicleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_vehicle, parent, false);
        return new VehicleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VehicleViewHolder holder, int position) {
        Vehicle vehicle = vehicleList.get(position);

        // Set vehicle model name
        if (vehicle.getVehicleModel() != null) {
            holder.tvVehicleName.setText(vehicle.getVehicleModel().getFullName());
            holder.tvBrand.setText(vehicle.getVehicleModel().getBrand());
        }

        // Set license plate
        holder.tvLicensePlate.setText(vehicle.getLicensePlate());

        // Set color and year
        holder.tvColorYear.setText(vehicle.getColor() + " • " + vehicle.getYear());

        // Set mileage
        holder.tvMileage.setText(String.format("%,d km", vehicle.getMileage()));

        // Click listeners
        holder.cardView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onVehicleClick(vehicle);
            }
        });

        holder.ivEdit.setOnClickListener(v -> {
            if (listener != null) {
                listener.onVehicleEdit(vehicle);
            }
        });

        holder.ivDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onVehicleDelete(vehicle);
            }
        });
    }

    @Override
    public int getItemCount() {
        return vehicleList.size();
    }

    static class VehicleViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView cardView;
        TextView tvVehicleName, tvBrand, tvLicensePlate, tvColorYear, tvMileage;
        ImageView ivEdit, ivDelete;

        public VehicleViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = (MaterialCardView) itemView;
            tvVehicleName = itemView.findViewById(R.id.tvVehicleName);
            tvBrand = itemView.findViewById(R.id.tvBrand);
            tvLicensePlate = itemView.findViewById(R.id.tvLicensePlate);
            tvColorYear = itemView.findViewById(R.id.tvColorYear);
            tvMileage = itemView.findViewById(R.id.tvMileage);
            ivEdit = itemView.findViewById(R.id.ivEdit);
            ivDelete = itemView.findViewById(R.id.ivDelete);
        }
    }
}
