package com.example.wearos_gui;

import android.os.Bundle;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.widget.ViewPager2;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends FragmentActivity {
    private ViewPager2 viewPager;
    private List<TodoItem> todoItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager = findViewById(R.id.viewPager);
        todoItems = new ArrayList<>();

        // Sample data
        todoItems.add(new TodoItem("Buy concert ticket", TodoItem.Priority.MEDIUM,
                TodoItem.Place.GENERAL, TodoItem.Time.GENERAL, LocalDate.now().plusDays(1), true));
        todoItems.add(new TodoItem("Submit next week's progress", TodoItem.Priority.LOW,
                TodoItem.Place.GENERAL, TodoItem.Time.GENERAL, LocalDate.now().plusDays(7), false));
        todoItems.add(new TodoItem("Buy groceries"));

        TodoPageAdapter adapter = new TodoPageAdapter(this, todoItems);
        viewPager.setAdapter(adapter);
    }
}


