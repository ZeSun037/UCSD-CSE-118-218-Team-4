package com.example.wearos_gui;

import java.time.LocalDate;

public class TodoItem {
    private String title;
    private Priority priority;
    private Place place;
    private Time time;
    private LocalDate date;
    private Boolean isDone;


    // Enum for Priority
    public enum Priority {
        LOW,
        MEDIUM,
        HIGH
    }

    public enum Place {
        HOME,
        SCHOOL,
        WORK,
        STORE,
        GENERAL
    }

    public enum Time {
        MORNING,
        ON_DUTY,
        OFF_DUTY,
        NOON,
        AFTERNOON,
        NIGHT,
        GENERAL
    }

    // Constructor with priority and place
    public TodoItem(String title, Priority priority, Place place, Time time, LocalDate date, boolean isDone) {
        this.title = title;
        this.priority = priority;
        this.place = place;
        this.time = time;
        this.date = date;
        this.isDone = isDone;
    }

    // Constructor without priority and place (default values)
    public TodoItem(String title) {
        this.title = title;
        this.priority = Priority.MEDIUM;  // Default priority
        this.place = Place.GENERAL;        // Default place
        this.time = Time.GENERAL;
        this.date = LocalDate.now();
        this.isDone = false;
    }

    // Getters and Setters
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public Priority getPriority() { return priority; }
    public void setPriority(Priority priority) { this.priority = priority; }

    public Place getPlace() { return place; }
    public void setPlace(Place place) { this.place = place; }
    public Time getTime() { return time; }
    public void setTime(Time time) { this.time = time; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public Boolean getDone() { return isDone; }
    public void setDone(Boolean done) { isDone = done; }

    public boolean isDueToday(LocalDate today) {
        return date.equals(today);
    }

    public boolean isDueTomorrow(LocalDate tomorrow) {
        return date.equals(tomorrow);
    }

    public boolean isDueThisWeek(LocalDate startOfWeek, LocalDate endOfWeek) {
        return (date.isAfter(startOfWeek.minusDays(1)) && date.isBefore(endOfWeek.plusDays(1)));
    }

}
