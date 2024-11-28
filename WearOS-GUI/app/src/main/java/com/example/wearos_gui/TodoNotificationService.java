package com.example.wearos_gui;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Handler;
import android.util.Log;

public class TodoNotificationService extends Service {
    private ContextDetector contextDetector;

    @Override
    public void onCreate() {
        super.onCreate();
        contextDetector = new ContextDetector(this);
        contextDetector.startListening();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        final Handler handler = new Handler();

        // Periodically check every 5 minutes
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Check cognitive load status
                if (contextDetector.isHighCognitiveLoad()) {
                    Log.d("CL", "High Cognitive Load Detected!");
                } else {
                    Log.d("CL","Low Cognitive Load.");
                }

                // Continue checking every 1 minutes
                handler.postDelayed(this, 60000);
            }
        }, 0);

        return START_STICKY;
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
