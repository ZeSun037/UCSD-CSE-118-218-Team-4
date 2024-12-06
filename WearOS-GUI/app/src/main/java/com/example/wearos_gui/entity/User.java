package com.example.wearos_gui.entity;

import java.util.Map;
import java.util.UUID;

public class User {
    private String id;
    private String name;
    private Map<String, LocationRange> locationMap;
    private Map<String, TimeRange> timeMap;

    public User(String name, Map<String, LocationRange> locationMap, Map<String, TimeRange> timeMap) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.locationMap = locationMap;
        this.timeMap = timeMap;
    }

    public String getId() { return id; }

    public void setId(String id) { this.id = id; }

    public String getName() {
        return name;
    }

    public void setName(String name) { this.name = name; }

    public Map<String, LocationRange> getLocationMap() {
        return locationMap;
    }

    public void setLocationMap(Map<String, LocationRange> locationMap) {
        this.locationMap = locationMap;
    }

    public Map<String, TimeRange> getTimeMap() {
        return timeMap;
    }

    public void setTimeMap(Map<String, TimeRange> timeMap) {
        this.timeMap = timeMap;
    }

}
