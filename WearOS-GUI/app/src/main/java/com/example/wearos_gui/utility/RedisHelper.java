package com.example.wearos_gui.utility;

import com.example.wearos_gui.entity.TodoItem;
import com.example.wearos_gui.entity.User;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

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
        return new ArrayList<>();
    }

    public static void updateTodos(TodoItem item) {
    }

    public static void deleteTodo(String userId, String todoTitle) {
        // jedis.hdel(userId, todoTitle);
    }
}
