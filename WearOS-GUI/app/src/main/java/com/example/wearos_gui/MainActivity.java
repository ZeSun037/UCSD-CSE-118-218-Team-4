package com.example.wearos_gui;

import static androidx.core.location.LocationManagerCompat.isLocationEnabled;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.Manifest;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
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
import com.example.wearos_gui.utility.FilteredTodoFragment;
import com.example.wearos_gui.utility.RedisHelper;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.Priority;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationServices;


public class MainActivity extends FragmentActivity {
    private static final Logger log = LoggerFactory.getLogger(MainActivity.class);
    private static final String groupId = "Group 4";
    private ViewPager2 viewPager;
    private TodoPageAdapter adapter;
    private List<TodoItem> personalTodoItems;
    private TodoDatabase persoanlTodoDatabase;
    private TodoDatabase groupTodoDatabase;
    private List<TodoItem> groupTodoItems;
    private User user;
    private FusedLocationProviderClient fusedLocationClient;
    private double lat = 0.0, lng = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize user and UI elements
        user = createUser();

        viewPager = findViewById(R.id.viewPager);
        TabLayout tabLayout = findViewById(R.id.tabLayout);

        // Fetch personal and group to-do items
        personalTodoItems = fetchPersonalTodos();
        groupTodoItems = fetchGroupTodos(groupId);

        // Set up adapter for ViewPager
        adapter = new TodoPageAdapter(this, personalTodoItems, persoanlTodoDatabase, user);
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
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Get current time and location and update user object
        getCurrentLocation();

        // Monitor cognitive load
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BODY_SENSORS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BODY_SENSORS}, REQUEST_BODY_SENSORS_PERMISSION);
        } else {
            startTodoNotificationService();
        }
    }


    private List<TodoItem> fetchPersonalTodos() {
        persoanlTodoDatabase = new TodoDatabase(this, "personalTodoTemp");
        List<TodoItem> todoItems = persoanlTodoDatabase.getAllTodos();
        if (todoItems.isEmpty()) {
            // Create a mock to-do list for testing
            List<TodoItem> todos = new ArrayList<>();
            todos.add(new TodoItem("Buy concert ticket", TodoItem.Priority.MEDIUM,
                    Place.GENERAL, Time.GENERAL, LocalDate.now(), "", false));
            todos.add(new TodoItem("Buy groceries", TodoItem.Priority.LOW,
                    Place.STORE, Time.WORKING, LocalDate.now(), "",false));
            todos.add(new TodoItem("Submit next week's progress", TodoItem.Priority.LOW,
                    Place.SCHOOL, Time.WORKING, LocalDate.now(), "",false));

            // fetch from redis
            todos.addAll(RedisHelper.getTodos(user.getId()));

            for (TodoItem item: todos) {
                persoanlTodoDatabase.insertTodo(item);
            }
            return todos;
        } else {
            for (TodoItem item: RedisHelper.getTodos(user.getId())) {
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

            // Fetch from redis
            todos.addAll(RedisHelper.getTodos(groupId));

            for (TodoItem item: todos) {
                groupTodoDatabase.insertTodo(item);
            }
            return todos;
        } else {
            for (TodoItem item: RedisHelper.getTodos(groupId)) {
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

    private void getCurrentLocation() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        ProgressBar loadingSpinner = findViewById(R.id.locationLoading);
        loadingSpinner.setVisibility(View.VISIBLE);

        // Check location permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
            loadingSpinner.setVisibility(View.GONE); // Hide spinner if permission is denied
            return;
        }

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!isLocationEnabled(locationManager)) {
            loadingSpinner.setVisibility(View.GONE); // Hide spinner if location is disabled
            showLocationEnableDialog();
            return;
        }

        // Use getCurrentLocation for faster, one-time location request
        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
            .addOnCompleteListener(task -> {
                loadingSpinner.setVisibility(View.GONE); // Hide spinner after getting location
                if (task.isSuccessful() && task.getResult() != null) {
                    Location location = task.getResult();
                    lat = location.getLatitude();
                    lng = location.getLongitude();

                    // Update the fragment with new location
                    int currentPosition = viewPager.getCurrentItem();
                    Fragment fragment = getSupportFragmentManager().findFragmentByTag("f" + currentPosition);
                    if (fragment instanceof FilteredTodoFragment) {
                        ((FilteredTodoFragment) fragment).updateLocationAndTime(lat, lng, System.currentTimeMillis());
                    }

                    Log.d("Location", "Latitude: " + lat + ", Longitude: " + lng);
                } else {
                    Toast.makeText(this, "Unable to fetch location. Please try again.",
                            Toast.LENGTH_SHORT).show();
                }
            });
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
                getCurrentLocation();
            } else {
                Toast.makeText(this, "Location permission denied！", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == REQUEST_BODY_SENSORS_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startTodoNotificationService();
            } else {
                Toast.makeText(this, "Permission denied!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void startTodoNotificationService() {
        // Start the TodoNotificationService to begin monitoring cognitive load
        Intent serviceIntent = new Intent(this, TodoNotificationService.class);
        startService(serviceIntent);
    }

    private static final int REQUEST_BODY_SENSORS_PERMISSION = 1;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;
}



