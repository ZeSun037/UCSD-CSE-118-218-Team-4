package com.example.wearos_gui.mapper;

import com.example.wearos_gui.entity.LocationRange;
import com.example.wearos_gui.entity.Place;

import java.util.Map;

public class LocationMapper {

    // usage: Place p = LocationMapper.mapLocationToPlace(user.getLocationMap(), lat, lng);
    public static Place mapLocationToPlace(Map<String, LocationRange> locationMap, double latitude, double longitude) {
        // Iterate through all location ranges and check if the current location is within range
        for (Map.Entry<String, LocationRange> entry : locationMap.entrySet()) {
            LocationRange range = entry.getValue();
            if (range.isWithinRange(latitude, longitude)) {
                // Return the place associated with this range
                return Place.valueOf(entry.getKey().toUpperCase());
            }
        }
        return Place.GENERAL; // Default if no match
    }
}

