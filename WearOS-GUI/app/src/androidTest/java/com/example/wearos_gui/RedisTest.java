package com.example.wearos_gui;

import android.content.Context;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import redis.clients.jedis.Jedis;

import static org.junit.Assert.*;

import com.example.wearos_gui.entity.Place;
import com.example.wearos_gui.entity.Time;
import com.example.wearos_gui.entity.TodoItem;
import com.example.wearos_gui.utility.RedisHelper;

import java.time.LocalDate;
import java.util.Map;

@RunWith(AndroidJUnit4.class)
public class RedisTest {

    private Jedis jedis;
    private Context context;
    private final String userId = "123";
    private final String groupId = "team123";

    @Before
    public void setUp() {
        context = ApplicationProvider.getApplicationContext();
        jedis = new Jedis("83.149.103.151", 6379);
    }

    @After
    public void tearDown() {
        if (jedis != null) {
//            jedis.flushAll(); // Clears all Redis data (for testing purposes)
            jedis.close();
        }
    }

    @Test
    public void testAddMultipleToDos() {
        // Creating To-Do items for the same user
        TodoItem todo1 = new TodoItem("Buy groceries", TodoItem.Priority.HIGH, Place.STORE, Time.GENERAL, LocalDate.now(),
        "", false);
        TodoItem todo2 = new TodoItem("Prepare slides for meeting", TodoItem.Priority.MEDIUM, Place.SCHOOL, Time.WORKING, LocalDate.now(),
                "", false);

        // Adding To-Do items to Redis for the user
        jedis.hset(userId, todo1.getTitle(), todo1.toString());
        jedis.hset(userId, todo2.getTitle(), todo2.toString());

        // Fetching and printing To-Do items for the user
        Map<String, String> todos = jedis.hgetAll(userId);
        System.out.println("Fetched To-Do items for user " + userId + ": " + todos);

        // Assertions
        assertNotNull(todos);
        assertTrue(todos.containsKey(todo1.getTitle()));
        assertTrue(todos.containsKey(todo2.getTitle()));

        // Verifying that the details are correctly stored
        assertTrue(todos.get(todo1.getTitle()).contains(todo1.getPriority().toString()));
        assertTrue(todos.get(todo2.getTitle()).contains(todo2.getPriority().toString()));

        // Converting the fetched strings back to Todo objects
        for (String title : todos.keySet()) {
            String todoString = todos.get(title);
            assert todoString != null;
            System.out.println("todoString" + todoString);
            TodoItem convertedTodo = RedisHelper.convertTodoFromString(todoString);

            // Printing the converted To-Do item
            System.out.println("Converted To-Do: " + convertedTodo);
            assertNotNull(convertedTodo);
            assertEquals(title, convertedTodo.getTitle());
        }
    }
}
