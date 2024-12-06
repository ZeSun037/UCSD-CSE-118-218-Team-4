package com.example.wearos_gui;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class ContextDetector implements SensorEventListener {
    private SensorManager sensorManager;
    private Sensor heartRateSensor, accelerometer;
    private float currentHeartRate = 0f;
    private float movementMagnitude = 0f;

    // Constructor
    public ContextDetector(Context context) {
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            heartRateSensor = sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }
    }

    // Start listening to sensors
    public void startListening() {
        if (heartRateSensor != null) {
            sensorManager.registerListener(this, heartRateSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    // Stop listening to sensors
    public void stopListening() {
        sensorManager.unregisterListener(this);
    }

    // Handle sensor events
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_HEART_RATE) {
            currentHeartRate = event.values[0];
        } else if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            // Calculate movement magnitude
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            movementMagnitude = (float) Math.sqrt(x * x + y * y + z * z);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    // Method to check if user is under high cognitive load
    public boolean isHighCognitiveLoad() {
        // Set thresholds for high cognitive load (adjust as necessary)
        return currentHeartRate > 100 && movementMagnitude > 2.0;
    }
}
