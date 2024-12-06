package com.example.wearos_gui;

import static androidx.core.location.LocationManagerCompat.isLocationEnabled;

import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;
import android.Manifest;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
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
import com.example.wearos_gui.utility.UserManager;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.Priority;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;


public class MainActivity extends FragmentActivity {
    private static final String groupId = "team4";
    private ViewPager2 viewPager;
    private TodoPageAdapter adapter;
    private List<TodoItem> personalTodoItems;
    private TodoDatabase persoanlTodoDatabase;
    private TodoDatabase groupTodoDatabase;
    private List<TodoItem> groupTodoItems;
    private User user;
    private FusedLocationProviderClient fusedLocationClient;
    private ScheduledExecutorService scheduler;
    private double lat = 0.0, lng = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageButton debugButton = findViewById(R.id.debugButton);

        debugButton.setOnClickListener(v -> {
            ArrayList<String> locationsList = new ArrayList<>(user.getLocationMap().keySet());
            ArrayList<String> timesList = new ArrayList<>(user.getTimeMap().keySet());

            Intent intent = new Intent(MainActivity.this, DebugActivity.class);
            intent.putStringArrayListExtra("locations", locationsList);
            intent.putStringArrayListExtra("times", timesList);
            startActivityForResult(intent, 1);
        });

        // Initialize user and UI elements
        user = createUser();
        UserManager.getInstance().setUser(user);

        viewPager = findViewById(R.id.viewPager);
        TabLayout tabLayout = findViewById(R.id.tabLayout);

        // Fetch personal and group to-do items
        RedisHelper.init();
        personalTodoItems = fetchPersonalTodos(true);
        groupTodoItems = fetchGroupTodos(groupId, true);

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

        getCurrentLocation();
    }

    @Override
    protected void onResume() {
        super.onResume();

        RedisHelper.init();
        startPeriodicFetch();

        // Get current time and location and update user object
        Log.d("Get location", "from onResume");
//        getCurrentLocation(); // this will override the location from test module

        // Monitor cognitive load
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BODY_SENSORS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BODY_SENSORS}, REQUEST_BODY_SENSORS_PERMISSION);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                startTodoNotificationService();
            }
            createNotificationChannel();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            // Get the selected location and time
            String selectedLocation = data.getStringExtra("selectedLocation");
            String selectedTime = data.getStringExtra("selectedTime");

            Log.d("MainActivity", "Selected Location: " + selectedLocation);
            Log.d("MainActivity", "Selected Time: " + selectedTime);

            LocationRange selectedLocationRange = user.getLocationMap().get(selectedLocation.toLowerCase());
            this.lat = selectedLocationRange.getMinLatitude();
            this.lng = selectedLocationRange.getMinLongitude();

            TimeRange selectedTimeRange = user.getTimeMap().get(selectedTime.toLowerCase());
            // Convert the selected time range to milliseconds
            long startTimeMillis = convertTimeToMillis(selectedTimeRange.getStartHour(), selectedTimeRange.getStartMinute());
            Log.d("Update fragment", "from updateTime");

            updateFragment(startTimeMillis);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        RedisHelper.close();
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
        }
    }

    // Fetch Redis to-dos every 1 minute
    private void startPeriodicFetch() {
        scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleWithFixedDelay(() -> {
            // Fetch data and update UI on the main thread
            runOnUiThread(() -> {
                try {
                    // Fetch personal and group todos
//                    Log.d("Fetch", "Fetching...");
                    personalTodoItems = fetchPersonalTodos(false);
                    groupTodoItems = fetchGroupTodos(groupId, false);

                    // Update the adapter with the latest data
                    if (viewPager.getCurrentItem() == 0) {
                        adapter.updateItems(personalTodoItems, "Personal", persoanlTodoDatabase);
                    } else {
                        adapter.updateItems(groupTodoItems, "Group", groupTodoDatabase);
                    }
                } catch (Exception e) {
                    Log.e("PeriodicFetch", "Error updating to-dos", e);
                }
            });
        }, 0, 1, TimeUnit.MINUTES); // initial delay: 0, delay b/t executions: 1 minute
    }



    private List<TodoItem> fetchPersonalTodos(boolean isFirstTime) {
        if (isFirstTime) {
            persoanlTodoDatabase = new TodoDatabase(this, "personalTodoTemp");
        }

        // Fetch from redis
        for (TodoItem redisTodo: RedisHelper.getTodos(user.getId())) {
            persoanlTodoDatabase.insertTodo(redisTodo);
            RedisHelper.deleteTodo(user.getId(), redisTodo.getTitle());  // Delete personal to-do after use
        }

        List<TodoItem> todoItems = persoanlTodoDatabase.getAllTodos();
//        if (todoItems.isEmpty()) {
//            // Create a mock to-do list for testing
//            List<TodoItem> todos = new ArrayList<>();
//            todos.add(new TodoItem("Buy concert ticket", TodoItem.Priority.MEDIUM,
//                    Place.GENERAL, Time.GENERAL, LocalDate.now(), "", false));
//            todos.add(new TodoItem("Buy groceries", TodoItem.Priority.LOW,
//                    Place.STORE, Time.WORKING, LocalDate.now(), "",false));
//            todos.add(new TodoItem("Submit next week's progress", TodoItem.Priority.LOW,
//                    Place.SCHOOL, Time.WORKING, LocalDate.now(), "",false));
//
//            for (TodoItem item: todos) {
//                persoanlTodoDatabase.insertTodo(item);
//            }
//            return todos;
//        }

        return todoItems;
    }

    private List<TodoItem> fetchGroupTodos(String groupId, boolean isFirstTime) {
        if (isFirstTime) {
            groupTodoDatabase = new TodoDatabase(this, "groupTodo");
        }

        // Fetch from redis
        List<TodoItem> todos = RedisHelper.getTodos(groupId);
        for (TodoItem todo: todos) {
            if (!groupTodoDatabase.containsTodo(todo)) {
                // Add new to-do if not exist
                groupTodoDatabase.insertTodo(todo);
            } else {
                groupTodoDatabase.updateTodo(todo);
            }
        }

        List<TodoItem> todoItems = groupTodoDatabase.getAllTodos();
//        if (todoItems.isEmpty()) {
//            // Create a mock to-do list for testing
//            List<TodoItem> mockTodos = new ArrayList<>();
//            mockTodos.add(new TodoItem("Plan group presentation", TodoItem.Priority.HIGH,
//                    Place.WORK, Time.WORKING, LocalDate.now().plusDays(2),
//                    "Will", false));
//            mockTodos.add(new TodoItem("Review project milestones", TodoItem.Priority.MEDIUM,
//                    Place.WORK, Time.WORKING, LocalDate.now().plusDays(5),
//                    "Chuong", false));
//
//            for (TodoItem item: todos) {
//                groupTodoDatabase.insertTodo(item);
//            }
//            return mockTodos;
//        }

        return todoItems;
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
            locationMap.put("work", new LocationRange(37.328, 37.338, -122.014, -122.006)); // UCSD
            locationMap.put("store", new LocationRange(32.868, 32.872, -117.213, -117.208)); // UTC

            Map<String, TimeRange> timeMap = new HashMap<>();
            timeMap.put("before_work", new TimeRange(7, 0, 8, 59));
            timeMap.put("working", new TimeRange(9, 0, 16, 59));
            timeMap.put("rest", new TimeRange(17, 0, 22, 59));
            timeMap.put("sleep", new TimeRange(23, 0, 6, 59));

            User user = new User("Will", locationMap, timeMap);
            user.setId("123");
            UserData.saveUser(this, user);

            return user;
        }
    }

    private long convertTimeToMillis(int hour, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    private void getCurrentLocation() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

//        ProgressBar loadingSpinner = findViewById(R.id.locationLoading);
//        loadingSpinner.setVisibility(View.VISIBLE);

        // Check location permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
//            loadingSpinner.setVisibility(View.GONE); // Hide spinner if permission is denied
            return;
        }

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!isLocationEnabled(locationManager)) {
//            loadingSpinner.setVisibility(View.GONE); // Hide spinner if location is disabled
            showLocationEnableDialog();
            return;
        }

        // Use getCurrentLocation for faster, one-time location request
        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
            .addOnCompleteListener(task -> {
//                loadingSpinner.setVisibility(View.GONE); // Hide spinner after getting location
                if (task.isSuccessful() && task.getResult() != null) {
                    Location location = task.getResult();
                    this.lat = location.getLatitude();
                    this.lng = location.getLongitude();

                    updateFragment(System.currentTimeMillis());
                } else {
                    Toast.makeText(this, "Unable to fetch location. Please try again.",
                            Toast.LENGTH_SHORT).show();
                }
            });
    }

    private void updateFragment(long time) {
        // Update the fragment with new location
        int currentPosition = viewPager.getCurrentItem();
        Fragment fragment = getSupportFragmentManager().findFragmentByTag("f" + currentPosition);
        if (fragment instanceof FilteredTodoFragment) {
            ((FilteredTodoFragment) fragment).updateLocationAndTime(this.lat, this.lng, time);
        }

        Log.d("Location", "Latitude: " + this.lat + ", Longitude: " + this.lng);
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
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    startTodoNotificationService();
                }
            } else {
                Toast.makeText(this, "Permission denied!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void createNotificationChannel() {
        // Notification channel ID
        String channelId = "todo_channel";
        CharSequence channelName = "To-Do Notifications";
        int importance = NotificationManager.IMPORTANCE_DEFAULT;

        // Create the channel
        NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
        channel.setDescription("Channel for to-do list notifications");

        // Register the channel with the system
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        if (notificationManager != null) {
            notificationManager.createNotificationChannel(channel);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    private void startTodoNotificationService() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.POST_NOTIFICATIONS},
                    REQUEST_NOTIFICATION_PERMISSION
            );
            return;
        }

        // Start the TodoNotificationService to begin monitoring cognitive load
        Intent serviceIntent = new Intent(this, TodoNotificationService.class);
        startService(serviceIntent);
    }

    private static final int REQUEST_BODY_SENSORS_PERMISSION = 1;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;
    private static final int REQUEST_NOTIFICATION_PERMISSION = 1001;
}



