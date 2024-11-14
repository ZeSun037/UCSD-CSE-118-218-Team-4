package com.example.wearos_gui;

public class TodoItem {
    private String title;
    private Priority priority;
    private String category;  // User-defined category as a String

    // Enum for Priority
    public enum Priority {
        LOW,
        MEDIUM,
        HIGH
    }

    // Constructor with priority and category
    public TodoItem(String title, Priority priority, String category) {
        this.title = title;
        this.priority = priority;
        this.category = category;
    }

    // Constructor without priority and category (default values)
    public TodoItem(String title) {
        this.title = title;
        this.priority = Priority.MEDIUM;  // Default priority
        this.category = "General";        // Default category
    }

    // Getters and Setters
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public Priority getPriority() { return priority; }
    public void setPriority(Priority priority) { this.priority = priority; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
}
