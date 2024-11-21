package com.example.wearos_gui;

import com.example.wearos_gui.entity.Place;
import com.example.wearos_gui.entity.Time;
import com.example.wearos_gui.entity.TodoItem;

import java.time.LocalDate;

public class TodoSerializer {
    public static String serializeTodoItem(TodoItem todoItem) {
        StringBuilder sb = new StringBuilder();

        sb.append(todoItem.getTitle()).append(";");
        sb.append(todoItem.getPriority()).append(";");
        sb.append(todoItem.getPlace()).append(";");
        sb.append(todoItem.getTime()).append(";");
//        sb.append(todoItem.getDate()).append(";");
        sb.append(todoItem.getDone()).append(";");
        sb.append(todoItem.getAssignee());

        return sb.toString();
    }

    public static TodoItem deserializeTodoItem(String todoJson) {
        String[] fields = todoJson.split(";");

        TodoItem todoItem = new TodoItem(fields[0]);
        todoItem.setPriority(TodoItem.Priority.valueOf(fields[1]));
        todoItem.setPlace(Place.valueOf(fields[2]));
        todoItem.setTime(Time.valueOf(fields[3]));
//        todoItem.setDate(LocalDate.parse(fields[4]));
        todoItem.setDone(Boolean.parseBoolean(fields[4]));
        todoItem.setAssignee(fields[5]);

        return todoItem;
    }
}
