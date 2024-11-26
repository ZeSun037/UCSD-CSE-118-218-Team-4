package com.example.wearos_gui;

import static androidx.core.location.LocationManagerCompat.getCurrentLocation;
import static androidx.core.location.LocationManagerCompat.isLocationEnabled;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;
import android.Manifest;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
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
import com.example.wearos_gui.utility.RedisHelper;
import com.example.wearos_gui.utility.TodoSerializer;
import com.google.android.gms.location.LocationRequest;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import redis.clients.jedis.Jedis;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

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
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;

    private double lat = 0.0, lng = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        user = createUser();
        String groupId = "Group 4";

        getCurrentLocation();

        viewPager = findViewById(R.id.viewPager);
        TabLayout tabLayout = findViewById(R.id.tabLayout);

        // Fetch personal and group to-do items
        personalTodoItems = fetchPersonalTodos();
        groupTodoItems = fetchGroupTodos(groupId);

        // Get current time and location
        long curTime = System.currentTimeMillis();

        // Set up adapter for ViewPager
        adapter = new TodoPageAdapter(this, personalTodoItems, persoanlTodoDatabase, user, lat, lng, curTime);
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
        persoanlTodoDatabase = new TodoDatabase(this, "personalTodo3");
        List<TodoItem> todoItems = persoanlTodoDatabase.getAllTodos();
        if (todoItems.isEmpty()) {
            // Create a mock to-do list for testing
            List<TodoItem> todos = new ArrayList<>();
            todos.add(new TodoItem("Buy concert ticket", TodoItem.Priority.MEDIUM,
                    Place.GENERAL, Time.GENERAL, LocalDate.now(), "", true));
            todos.add(new TodoItem("Submit next week's progress", TodoItem.Priority.LOW,
                    Place.SCHOOL, Time.WORKING, LocalDate.now(), "",false));
            todos.add(new TodoItem("Buy groceries", TodoItem.Priority.HIGH,
                    Place.STORE, Time.WORKING, LocalDate.now(), "",false));

            // fetch from redis
            todos.addAll(RedisHelper.getTodos(user.getId()));

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
            todos.addAll(RedisHelper.getTodos(groupId));

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
        return Collections.emptyList();
    }

    private void getCurrentLocation() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Check location permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }
        
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!isLocationEnabled(locationManager)) {
            showLocationEnableDialog();
            return;
        }

        // Create location request
        LocationRequest locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10000) .setFastestInterval(5000);

        // Location callback to handle location updates
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                Location location = locationResult.getLastLocation();
                if (location != null) {
                    lat = location.getLatitude();
                    lng = location.getLongitude();
                    // Stop location updates after getting the location
                    fusedLocationClient.removeLocationUpdates(locationCallback);

//                    // Update the adapter with new location
//                    if (adapter != null) {
//                        adapter.updateLocation(lat, lng);
//                    }

                    Log.d("Location", "Latitude: " + lat + ", Longitude: " + lng);
                } else {
                    Log.d("Location Error", "location is null");
                }
            }
        };

        // Request location updates
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }

    private void showLocationEnableDialog() {
        new AlertDialog.Builder(this)
            .setTitle("Location Services Disabled")
            .setMessage("Please enable location services to use this feature")
            .setPositiveButton("Settings", (dialog, which) -> {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            })
            .setNegativeButton("Cancel", null)
            .show();
    }

    @Override public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, try to get location again
                getCurrentLocation();
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;
}



