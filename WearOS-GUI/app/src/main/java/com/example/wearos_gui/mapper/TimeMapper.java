package com.example.wearos_gui.mapper;

import com.example.wearos_gui.entity.Time;
import com.example.wearos_gui.entity.TimeRange;

import java.util.Map;

public class TimeMapper {

    // usage:
    // long currentTimeMillis = System.currentTimeMillis();
    // Time time = TimeMapper.mapTimeToContext(user.getTimeMap(), currentTimeMillis);
    public static Time mapTimeToContext(Map<String, TimeRange> timeMap, long currentTimeMillis) {
        // Iterate through all time ranges and check if the current time is within range
        for (Map.Entry<String, TimeRange> entry : timeMap.entrySet()) {
            TimeRange timeRange = entry.getValue();
            if (timeRange.isWithinRange(currentTimeMillis)) {
                // Return the time context associated with this range
                return Time.valueOf(entry.getKey().toUpperCase());
            }
        }
        return Time.GENERAL; // Default if no match
    }
}

