package com.example.wearos_gui;

import android.os.Bundle;
import android.util.Log;

import androidx.constraintlayout.motion.widget.Debug;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.wearos_gui.entity.LocationRange;
import com.example.wearos_gui.entity.Place;
import com.example.wearos_gui.entity.Time;
import com.example.wearos_gui.entity.TimeRange;
import com.example.wearos_gui.entity.TodoItem;
import com.example.wearos_gui.entity.User;
import com.example.wearos_gui.storage.TodoDatabase;
import com.example.wearos_gui.storage.UserData;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import redis.clients.jedis.Jedis;

public class MainActivity extends FragmentActivity {
    private static final String redisHost = "83.149.103.151";
    private static final int redistPort = 6379;
    private static final Logger log = LoggerFactory.getLogger(MainActivity.class);
    private ViewPager2 viewPager;
    private TodoPageAdapter adapter;
    private List<TodoItem> personalTodoItems;
    private TodoDatabase persoanlTodoDatabase;
    private TodoDatabase groupTodoDatabase;
    private List<TodoItem> groupTodoItems;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        user = createUser();

        viewPager = findViewById(R.id.viewPager);
        TabLayout tabLayout = findViewById(R.id.tabLayout);

        // Fetch personal and group to-do items
        personalTodoItems = fetchPersonalTodos();
        groupTodoItems = fetchGroupTodos("group 4");

        // Set up adapter for ViewPager
        adapter = new TodoPageAdapter(this, personalTodoItems, persoanlTodoDatabase);
        viewPager.setAdapter(adapter);

        // Attach TabLayout to ViewPager
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            if (position == 0) {
                tab.setText("My TODOs");
            } else {
                tab.setText("Group TODOs");
            }
        }).attach();

        // Listen for tab changes
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if (position == 0) {
                    adapter.updateItems(personalTodoItems, "Personal", persoanlTodoDatabase);
                } else {
                    adapter.updateItems(groupTodoItems, "Group", groupTodoDatabase);
                }
            }
        });
//        TodoPageAdapter adapter = new TodoPageAdapter(this, todoItems);
//        viewPager.setAdapter(adapter);
    }

    private List<TodoItem> fetchPersonalTodos() {
        persoanlTodoDatabase = new TodoDatabase(this, "personalTodo");
        List<TodoItem> todoItems = persoanlTodoDatabase.getAllTodos();
        if (todoItems.isEmpty()) {
            // Create a mock to-do list for testing
            List<TodoItem> todos = new ArrayList<>();
            todos.add(new TodoItem("Buy concert ticket", TodoItem.Priority.MEDIUM,
                    Place.GENERAL, Time.GENERAL, LocalDate.now().plusDays(1),
                    "", true));
            todos.add(new TodoItem("Submit next week's progress", TodoItem.Priority.LOW,
                    Place.GENERAL, Time.WORKING, LocalDate.now().plusDays(7),
                    "",false));
            todos.add(new TodoItem("Buy groceries"));

            // fetch from redis
//            todos.addAll(fetchTodosFromRedis(user.getId(), true));

            for (TodoItem item: todos) {
                persoanlTodoDatabase.insertTodo(item);
            }
            return todos;
        } else {
            for (TodoItem item: fetchTodosFromRedis(user.getId(), true)) {
                persoanlTodoDatabase.insertTodo(item);
            }
            return todoItems;
        }
    }

    private List<TodoItem> fetchGroupTodos(String groupId) {
        groupTodoDatabase = new TodoDatabase(this, "groupTodo");
        List<TodoItem> todoItems = groupTodoDatabase.getAllTodos();
        if (todoItems.isEmpty()) {
            // Create a mock to-do list for testing
            List<TodoItem> todos = new ArrayList<>();
            todos.add(new TodoItem("Plan group presentation", TodoItem.Priority.HIGH,
                    Place.WORK, Time.WORKING, LocalDate.now().plusDays(2),
                    "Will", false));
            todos.add(new TodoItem("Review project milestones", TodoItem.Priority.MEDIUM,
                    Place.WORK, Time.WORKING, LocalDate.now().plusDays(5),
                    "Chuong", false));

            // fetch from redis
//            todos.addAll(fetchTodosFromRedis(groupId, false));

            for (TodoItem item: todos) {
                groupTodoDatabase.insertTodo(item);
            }
            return todos;
        } else {
            for (TodoItem item: fetchTodosFromRedis(groupId, false)) {
                groupTodoDatabase.insertTodo(item);
            }
            return todoItems;
        }
    }

    private User createUser() {
        User retrievedUser = UserData.getUser(this);

        if (retrievedUser != null) {
            return retrievedUser;
        } else {
            // Create a mock user for testing
            Map<String, LocationRange> locationMap = new HashMap<>();
            locationMap.put("home", new LocationRange(32.870, 32.871, -117.217, -117.216));  // CVV
            locationMap.put("school", new LocationRange(32.871, 32.891, -117.242, -117.228)); // UCSD
            locationMap.put("store", new LocationRange(32.868, 32.872, -117.213, -117.208)); // UTC

            Map<String, TimeRange> timeMap = new HashMap<>();
            timeMap.put("before_working", new TimeRange(7, 0, 8, 59));
            timeMap.put("working", new TimeRange(9, 0, 16, 59));
            timeMap.put("rest", new TimeRange(17, 0, 22, 59));
            timeMap.put("sleep", new TimeRange(23, 0, 6, 59));

            User user = new User("Steve Jobs", locationMap, timeMap);
            UserData.saveUser(this, user);

            return user;
        }
    }

    private List<TodoItem> fetchTodosFromRedis(String key, boolean isPersonal) {
        Jedis jedis;
        try {
            jedis = new Jedis(redisHost, redistPort);
            List<String> todoJsonList = jedis.lrange(key, 0, -1);
            List<TodoItem> todoItems = new ArrayList<>();

            // Deserialize each String into a TodoItem object
            for (String todoJson : todoJsonList) {
                TodoItem todoItem = TodoSerializer.deserializeTodoItem(todoJson);
                todoItems.add(todoItem);
            }

            // Delete all values for personal todos
            if (isPersonal) {
                jedis.del(key);
            }

            jedis.close();
            return todoItems;
        } catch (Exception e) {
            log.debug(e.toString());
        }
        return java.util.Collections.emptyList();
    }
}



