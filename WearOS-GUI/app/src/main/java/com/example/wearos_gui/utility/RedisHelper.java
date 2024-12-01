package com.example.wearos_gui.utility;

import com.example.wearos_gui.entity.Place;
import com.example.wearos_gui.entity.Time;
import com.example.wearos_gui.entity.TodoItem;
import com.example.wearos_gui.entity.User;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import redis.clients.jedis.Jedis;

public class RedisHelper {
    private static Jedis jedis;

    public static void init() {
        jedis = new Jedis("", 6379);
        System.out.println("Connected to Redis: " + jedis.ping());
    }

    public static void close() {
        if (jedis != null) {
            jedis.close();
        }
    }

    public static List<TodoItem> getTodos(String key) {
        List<TodoItem> todoItems = new ArrayList<>();
        Map<String, String> todos = jedis.hgetAll(key);

        for (String title : todos.keySet()) {
            String todoString = todos.get(title);
            assert todoString != null;
//            System.out.println("todoString" + todoString);
            TodoItem convertedTodo = convertTodoFromString(todoString);
            todoItems.add(convertedTodo);
        }

        return todoItems;
    }

    public static void updateTodos(String key, TodoItem item) {
        jedis.hset(key, item.getTitle(), item.toString());
    }

    public static void deleteTodo(String userId, String todoTitle) {
         jedis.hdel(userId, todoTitle);
    }

    public static TodoItem convertTodoFromString(String todoString) {
        String[] parts = todoString.split(", ");
        String title = parts[0].split(": ")[1];
        String priority = parts[1].split(": ")[1];
        String place = parts[2].split(": ")[1];
        String time = parts[3].split(": ")[1];
        String assignee = "";
        if (parts[4].split(": ").length > 1) {
            assignee = parts[4].split(": ")[1];
        }
        String isDone = parts[5].split(": ")[1];

        return new TodoItem(
                title,
                TodoItem.Priority.valueOf(priority),
                Place.valueOf(place),
                Time.valueOf(time),
                assignee,
                Boolean.parseBoolean(isDone)
        );
    }
}
