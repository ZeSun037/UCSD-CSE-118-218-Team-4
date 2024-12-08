package com.example.wearos_gui;

import com.example.wearos_gui.entity.Place;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.wearos_gui.entity.Time;

import java.util.ArrayList;

public class DebugActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        ArrayList<String> locationsList = getIntent().getStringArrayListExtra("locations");
        assert locationsList != null;
        locationsList.sort((a, b) -> {
            if (a.equals("school") && !b.equals("school")) {
                return -1;
            } else if (a.equals("school")) {
                return 0;
            } else {
                return 1;
            }
        });
        ArrayList<String> timesList = getIntent().getStringArrayListExtra("times");
        assert timesList != null;
        timesList.sort((a, b) -> {
            if (a.equals("working") && !b.equals("working")) {
                return -1;
            } else if (a.equals("working")) {
                return 0;
            } else {
                return 1;
            }
        });

        // Location Dropdown
        Spinner locationSpinner = findViewById(R.id.locationSpinner);
        ArrayAdapter<String> locationAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, locationsList);
        locationAdapter.setDropDownViewResource(R.layout.spinner_item);
        locationSpinner.setAdapter(locationAdapter);

        // Time Dropdown
        Spinner timeSpinner = findViewById(R.id.timeSpinner);
        ArrayAdapter<String> timeAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, timesList);
        timeAdapter.setDropDownViewResource(R.layout.spinner_item);
        timeSpinner.setAdapter(timeAdapter);

        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> {
            String selectedLocation = locationSpinner.getSelectedItem().toString();
            String selectedTime = timeSpinner.getSelectedItem().toString();

            // Create an intent to pass the result back
            Intent resultIntent = new Intent();
            resultIntent.putExtra("selectedLocation", selectedLocation);
            resultIntent.putExtra("selectedTime", selectedTime);
            setResult(RESULT_OK, resultIntent);

            finish();
        });
    }
}