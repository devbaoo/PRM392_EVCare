package com.example.prm392_evcare.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392_evcare.R;
import com.example.prm392_evcare.models.CalendarDay;
import com.google.android.material.card.MaterialCardView;

import java.util.Calendar;
import java.util.List;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder> {

    private List<CalendarDay> calendarDays;
    private OnDateClickListener onDateClickListener;
    private int currentDay = -1;
    private int currentMonth = -1;
    private int currentYear = -1;

    public interface OnDateClickListener {
        void onDateClick(CalendarDay day);
    }

    public CalendarAdapter(List<CalendarDay> calendarDays, OnDateClickListener listener) {
        this.calendarDays = calendarDays;
        this.onDateClickListener = listener;

        // Get current date
        Calendar calendar = Calendar.getInstance();
        currentDay = calendar.get(Calendar.DAY_OF_MONTH);
        currentMonth = calendar.get(Calendar.MONTH);
        currentYear = calendar.get(Calendar.YEAR);
    }

    @NonNull
    @Override
    public CalendarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_calendar_day, parent, false);
        return new CalendarViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CalendarViewHolder holder, int position) {
        CalendarDay day = calendarDays.get(position);
        holder.bind(day);
    }

    @Override
    public int getItemCount() {
        return calendarDays.size();
    }

    public void updateCalendar(List<CalendarDay> newDays) {
        this.calendarDays = newDays;
        notifyDataSetChanged();
    }

    class CalendarViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView cardDay;
        TextView tvDayNumber;
        TextView tvScheduleTime;
        LinearLayout scheduleIndicatorsLayout;
        LinearLayout statusIndicatorsLayout;
        View indicatorWorking;
        View indicatorCompleted;
        View indicatorLeave;
        View indicatorAbsent;

        public CalendarViewHolder(@NonNull View itemView) {
            super(itemView);
            cardDay = itemView.findViewById(R.id.cardDay);
            tvDayNumber = itemView.findViewById(R.id.tvDayNumber);
            tvScheduleTime = itemView.findViewById(R.id.tvScheduleTime);
            scheduleIndicatorsLayout = itemView.findViewById(R.id.scheduleIndicatorsLayout);
            statusIndicatorsLayout = itemView.findViewById(R.id.statusIndicatorsLayout);
            indicatorWorking = itemView.findViewById(R.id.indicatorWorking);
            indicatorCompleted = itemView.findViewById(R.id.indicatorCompleted);
            indicatorLeave = itemView.findViewById(R.id.indicatorLeave);
            indicatorAbsent = itemView.findViewById(R.id.indicatorAbsent);
        }

        public void bind(CalendarDay day) {
            if (day.getDay() == 0) {
                // Empty cell for padding
                tvDayNumber.setText("");
                cardDay.setCardBackgroundColor(Color.parseColor("#F5F5F5"));
                cardDay.setStrokeColor(Color.parseColor("#F5F5F5"));
                tvScheduleTime.setVisibility(View.GONE);
                statusIndicatorsLayout.setVisibility(View.GONE);
                itemView.setOnClickListener(null);
            } else {
                tvDayNumber.setText(String.valueOf(day.getDay()));
                
                // Check if it's current month
                if (!day.isCurrentMonth()) {
                    tvDayNumber.setTextColor(Color.parseColor("#BDBDBD"));
                    cardDay.setCardBackgroundColor(Color.parseColor("#FAFAFA"));
                } else {
                    tvDayNumber.setTextColor(Color.parseColor("#212121"));
                    cardDay.setCardBackgroundColor(Color.WHITE);
                    
                    // Highlight today
                    if (day.getDay() == currentDay && 
                        day.getMonth() == currentMonth && 
                        day.getYear() == currentYear) {
                        cardDay.setCardBackgroundColor(Color.parseColor("#E3F2FD"));
                        cardDay.setStrokeColor(Color.parseColor("#2196F3"));
                        cardDay.setStrokeWidth(3);
                    }
                    
                    // Highlight selected day
                    if (day.isSelected()) {
                        cardDay.setCardBackgroundColor(Color.parseColor("#C5CAE9"));
                        cardDay.setStrokeColor(Color.parseColor("#6366F1"));
                        cardDay.setStrokeWidth(3);
                    }
                }

                // Show schedule indicators
                if (day.hasSchedule()) {
                    tvScheduleTime.setText(day.getScheduleTime());
                    tvScheduleTime.setVisibility(View.VISIBLE);
                    statusIndicatorsLayout.setVisibility(View.VISIBLE);

                    // Show status indicators
                    indicatorWorking.setVisibility(day.isWorking() ? View.VISIBLE : View.GONE);
                    indicatorCompleted.setVisibility(day.isCompleted() ? View.VISIBLE : View.GONE);
                    indicatorLeave.setVisibility(day.isOnLeave() ? View.VISIBLE : View.GONE);
                    indicatorAbsent.setVisibility(day.isAbsent() ? View.VISIBLE : View.GONE);
                } else {
                    tvScheduleTime.setVisibility(View.GONE);
                    statusIndicatorsLayout.setVisibility(View.GONE);
                }

                // Click listener
                itemView.setOnClickListener(v -> {
                    if (onDateClickListener != null && day.isCurrentMonth()) {
                        onDateClickListener.onDateClick(day);
                    }
                });
            }
        }
    }
}

