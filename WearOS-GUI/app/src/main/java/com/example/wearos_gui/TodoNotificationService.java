package com.example.wearos_gui;

import static androidx.core.location.LocationManagerCompat.isLocationEnabled;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.IBinder;
import android.os.Handler;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.example.wearos_gui.entity.TodoItem;
import com.example.wearos_gui.entity.User;
import com.example.wearos_gui.storage.TodoDatabase;
import com.example.wearos_gui.utility.TodoSorter;
import com.example.wearos_gui.utility.UserManager;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

import java.util.List;
import java.util.stream.Collectors;

public class TodoNotificationService extends Service {
    private static final long delay = 60000;
    private static final boolean isTestMode = true;
    private ContextDetector contextDetector;
    private TodoDatabase personalTodoDatabase;
    private TodoDatabase groupTodoDatabase;
    private FusedLocationProviderClient fusedLocationClient;
    private SharedPreferences notificationCache;
    private double lat;
    private double lng;
    private User user;

    @Override
    public void onCreate() {
        super.onCreate();
        contextDetector = new ContextDetector(this);
        contextDetector.startListening();

        personalTodoDatabase = TodoDatabase.getInstance(getApplicationContext(), "personalTodoTemp");
        groupTodoDatabase = TodoDatabase.getInstance(getApplicationContext(), "groupTodo");
        notificationCache = getSharedPreferences("todo_notifications", MODE_PRIVATE);
        this.user = UserManager.getInstance().getUser();
        getCurrentLocation();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        final Handler handler = new Handler();

        // Periodically check every 5 minutes
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getCurrentLocation();

                // Check cognitive load status
                if (isTestMode) {
                    TodoItem item = getTopTodoItem();
                    if (item != null) {
                        sendNotification(item);
                    }
                } else {
                    if (contextDetector.isHighCognitiveLoad()) {
                        Log.d("CL", "High Cognitive Load");
                    } else {
                        Log.d("CL","Low Cognitive Load");
                        TodoItem item = getTopTodoItem();
                        if (item != null) {
                            sendNotification(item);
                        }
                    }
                }

                // Continue checking every 1 minutes
                handler.postDelayed(this, isTestMode ? delay : 5*delay);
            }
        }, delay);

        return START_STICKY;
    }

    private TodoItem getTopTodoItem() {
        List<TodoItem> todoItems = personalTodoDatabase.getAllTodos();
        todoItems.addAll(groupTodoDatabase.getAllTodos());
        List<TodoItem> notDoneItems = todoItems.stream().filter(item -> !item.getDone()).collect(Collectors.toList());

        TodoSorter.sortNotDoneItems(notDoneItems, this.user, this.lat, this.lng, System.currentTimeMillis());

        for (int i = 0; i < notDoneItems.size(); i++) {
            TodoItem topItem = notDoneItems.get(i);

            if (isTestMode) {
                return topItem;
            } else {
                // TODO: can set a threshold value for notifying todo item
                // Check the cache for this item's notification status
                String cacheKey = String.valueOf(topItem.getTitle().hashCode());
                long lastNotified = notificationCache.getLong(cacheKey, 0);

                // Notify only if this item has not been notified in the last 24 hours
                if (System.currentTimeMillis() - lastNotified >= 24 * 60 * 60 * 1000) {
                    // Update cache with the current timestamp
                    notificationCache.edit().putLong(cacheKey, System.currentTimeMillis()).apply();

                    return topItem;
                }
            }
        }
        return null;
    }

    private void sendNotification(TodoItem item) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "todo_channel")
                .setSmallIcon(android.R.drawable.ic_dialog_alert)
                .setContentTitle("To-Do Reminder")
                .setContentText("Don't forget: " + item.getTitle())
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManagerCompat manager = NotificationManagerCompat.from(this);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        manager.notify(item.getTitle().hashCode(), builder.build());
    }

    private void getCurrentLocation() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Check location permission
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!isLocationEnabled(locationManager)) {
            return;
        }

        // Use getCurrentLocation for faster, one-time location request
        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
            .addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult() != null) {
                    Location location = task.getResult();
                    lat = location.getLatitude();
                    lng = location.getLongitude();
                }
            });
    }

    @Override
    public void onDestroy() {
        contextDetector.stopListening();
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
