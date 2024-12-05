package com.example.wearos_gui.utility;

import android.os.AsyncTask;
import android.util.Log;

import com.example.wearos_gui.entity.Place;
import com.example.wearos_gui.entity.Time;
import com.example.wearos_gui.entity.TodoItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisConnectionException;

public class RedisHelper {
    private static Jedis jedis;
    private static final String REDIS_HOST = "83.149.103.151";
    private static final int REDIS_PORT = 6379;
    private static final ExecutorService executor = Executors.newSingleThreadExecutor();

    public static void init() {
        executor.submit(() -> {
            try {
                if (jedis == null || !jedis.isConnected()) {
                    jedis = new Jedis(REDIS_HOST, REDIS_PORT);
                }
            } catch (JedisConnectionException e) {
                Log.e("RedisHelper", "Failed to connect to Redis", e);
            }
        });
    }

    public static void close() {
        executor.submit(() -> {
            if (jedis != null) {
                jedis.close();
                jedis = null;
            }
        });
    }

    public static List<TodoItem> getTodos(String key) {
        try {
            Future<List<TodoItem>> future = executor.submit(() -> {
                if (jedis == null) {
                    jedis = new Jedis(REDIS_HOST, REDIS_PORT);
                }
                List<TodoItem> todoItems = new ArrayList<>();
                Map<String, String> todos = jedis.hgetAll(key);

                for (String title : todos.keySet()) {
                    String todoString = todos.get(title);
                    if (todoString != null) {
                        TodoItem convertedTodo = convertTodoFromString(todoString);
                        todoItems.add(convertedTodo);
                    }
                }

                return todoItems;
            });

            return future.get(); // This will block until the result is available
        } catch (Exception e) {
            Log.e("RedisHelper", "Error getting todos", e);
            return new ArrayList<>();
        }
    }

    public static void updateTodos(String key, TodoItem item) {
        executor.submit(() -> {
            if (jedis == null) {
                jedis = new Jedis(REDIS_HOST, REDIS_PORT);
            }
            jedis.hset(key, item.getTitle(), item.toString());
        });
    }

    public static void deleteTodo(String userId, String todoTitle) {
        executor.submit(() -> {
            if (jedis == null) {
                jedis = new Jedis(REDIS_HOST, REDIS_PORT);
            }
            jedis.hdel(userId, todoTitle);
        });
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