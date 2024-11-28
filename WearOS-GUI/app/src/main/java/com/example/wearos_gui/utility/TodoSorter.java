package com.example.wearos_gui.utility;

import android.util.Log;

import com.example.wearos_gui.entity.LocationRange;
import com.example.wearos_gui.entity.Place;
import com.example.wearos_gui.entity.Time;
import com.example.wearos_gui.entity.TodoItem;
import com.example.wearos_gui.entity.User;
import com.example.wearos_gui.mapper.LocationMapper;
import com.example.wearos_gui.mapper.TimeMapper;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class TodoSorter {
    private final static double PRIORITY_WEIGHT = 0.5;
    private final static double PLACE_WEIGHT = 0.2;
    private final static double TIME_WEIGHT = 0.3;
    public static void sortNotDoneItems(List<TodoItem> notDoneItems, User user, double lat, double lng, long time) {
        // put higher score todo item at the front
        notDoneItems.sort(Comparator.comparingDouble(item -> -getItemScore(item, user, lat, lng, time)));
    }

    public static double getItemScore(TodoItem item, User user, double lat, double lng, long time) {
        // Calculate the priority score
        double priorityScore = 0;
        switch (item.getPriority()) {
            case MEDIUM:
                priorityScore = 0.5;
                break;
            case HIGH:
                priorityScore = 1.0;
                break;
            default:
                break;
        }

        // Calculate the place score
        Place curPlace = LocationMapper.mapLocationToPlace(user.getLocationMap(), lat, lng);
        double placeScore = curPlace.equals(item.getPlace()) ? 1 : 0;

        // Calculate the time score
        Time curTime = TimeMapper.mapTimeToContext(user.getTimeMap(), time);
        double timeScore = curTime.equals(item.getTime()) ? 1 : 0;

        return PRIORITY_WEIGHT*priorityScore + PLACE_WEIGHT*placeScore + TIME_WEIGHT*timeScore;
    }
}
