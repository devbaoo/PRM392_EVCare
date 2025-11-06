package com.example.prm392_evcare.models;

public class CalendarDay {
    private int day;
    private int month;
    private int year;
    private boolean isCurrentMonth;
    private boolean isSelected;
    private boolean hasSchedule;
    private String scheduleTime;
    
    // Status flags
    private boolean isWorking;
    private boolean isCompleted;
    private boolean isOnLeave;
    private boolean isAbsent;

    public CalendarDay(int day, int month, int year, boolean isCurrentMonth) {
        this.day = day;
        this.month = month;
        this.year = year;
        this.isCurrentMonth = isCurrentMonth;
        this.isSelected = false;
        this.hasSchedule = false;
        this.scheduleTime = "";
        this.isWorking = false;
        this.isCompleted = false;
        this.isOnLeave = false;
        this.isAbsent = false;
    }

    // Getters
    public int getDay() {
        return day;
    }

    public int getMonth() {
        return month;
    }

    public int getYear() {
        return year;
    }

    public boolean isCurrentMonth() {
        return isCurrentMonth;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public boolean hasSchedule() {
        return hasSchedule;
    }

    public String getScheduleTime() {
        return scheduleTime;
    }

    public boolean isWorking() {
        return isWorking;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public boolean isOnLeave() {
        return isOnLeave;
    }

    public boolean isAbsent() {
        return isAbsent;
    }

    // Setters
    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public void setHasSchedule(boolean hasSchedule) {
        this.hasSchedule = hasSchedule;
    }

    public void setScheduleTime(String scheduleTime) {
        this.scheduleTime = scheduleTime;
    }

    public void setWorking(boolean working) {
        isWorking = working;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public void setOnLeave(boolean onLeave) {
        isOnLeave = onLeave;
    }

    public void setAbsent(boolean absent) {
        isAbsent = absent;
    }
}

