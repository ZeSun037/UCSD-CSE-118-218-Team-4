package com.example.wearos_gui;

import com.example.wearos_gui.entity.TodoItem;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.stream.Collectors;

public interface FilterStrategy {
    List<TodoItem> filter(List<TodoItem> items);

//    class TodayFilter implements FilterStrategy {
//        @Override
//        public List<TodoItem> filter(List<TodoItem> items) {
//            LocalDate today = LocalDate.now();
//            return items.stream()
//                    .filter(item -> item.isDueToday(today))
//                    .collect(Collectors.toList());
//        }
//    }
//
//    class TomorrowFilter implements FilterStrategy {
//        @Override
//        public List<TodoItem> filter(List<TodoItem> items) {
//            LocalDate tomorrow = LocalDate.now().plusDays(1);
//            return items.stream()
//                    .filter(item -> item.isDueTomorrow(tomorrow))
//                    .collect(Collectors.toList());
//        }
//    }
//
//    class ThisWeekFilter implements FilterStrategy {
//        @Override
//        public List<TodoItem> filter(List<TodoItem> items) {
//            LocalDate today = LocalDate.now();
//            LocalDate startOfWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));
//            LocalDate endOfWeek = today.with(DayOfWeek.SATURDAY);
//
//            return items.stream()
//                    .filter(item -> item.isDueThisWeek(startOfWeek, endOfWeek))
//                    .collect(Collectors.toList());
//        }
//    }

    class GroupFilter implements FilterStrategy {
        @Override
        public List<TodoItem> filter(List<TodoItem> items) {
            return items.stream()
                    .filter(item -> !item.getAssignee().isEmpty())
                    .collect(Collectors.toList());
        }
    }

    class AllFilter implements FilterStrategy {
        @Override
        public List<TodoItem> filter(List<TodoItem> items) {
            return items; // Returns all items without filtering
        }
    }
}