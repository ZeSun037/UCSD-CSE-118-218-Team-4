package com.example.wearos_gui;

public class FilterFactory {
    public static FilterStrategy getFilter(String filterType) {
        switch (filterType) {
            case "Today":
                return new FilterStrategy.TodayFilter();
            case "Tomorrow":
                return new FilterStrategy.TomorrowFilter();
            case "This Week":
                return new FilterStrategy.ThisWeekFilter();
            default:
                return new FilterStrategy.AllFilter();
        }
    }
}
