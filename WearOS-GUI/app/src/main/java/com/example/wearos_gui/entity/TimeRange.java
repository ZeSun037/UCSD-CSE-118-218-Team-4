package com.example.wearos_gui.entity;

import java.util.*;

public class TimeRange {
    private int startHour;
    private int startMinute;
    private int endHour;
    private int endMinute;

    public TimeRange(int startHour, int startMinute, int endHour, int endMinute) {
        this.startHour = startHour;
        this.startMinute = startMinute;
        this.endHour = endHour;
        this.endMinute = endMinute;
    }

    public int getStartHour() {
        return startHour;
    }

    public int getStartMinute() { return startMinute; }

    public int getEndHour() {
        return endHour;
    }

    public int getEndMinute() {
        return endMinute;
    }

    public boolean isWithinRange(long currentTimeMillis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(currentTimeMillis);

        int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
        int currentMinute = calendar.get(Calendar.MINUTE);

        int startTimeInMinutes = startHour * 60 + startMinute;
        int endTimeInMinutes = endHour * 60 + endMinute;
        int currentTimeInMinutes = currentHour * 60 + currentMinute;

        // Handle the case where the range crosses midnight
        if (startTimeInMinutes > endTimeInMinutes) {
            // Time range spans across midnight, so check if current time is either after start or before end
            return currentTimeInMinutes >= startTimeInMinutes || currentTimeInMinutes <= endTimeInMinutes;
        } else {
            // Normal case
            return currentTimeInMinutes >= startTimeInMinutes && currentTimeInMinutes <= endTimeInMinutes;
        }
    }
}
