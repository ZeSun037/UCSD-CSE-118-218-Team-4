package com.example.wearos_gui;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Example of accessing views from XML
        TextView textView = findViewById(R.id.text_view);
        textView.setText("Hello Team 4!");
    }
}
