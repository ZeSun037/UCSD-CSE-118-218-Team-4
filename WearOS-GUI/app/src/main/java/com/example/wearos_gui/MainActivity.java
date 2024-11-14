package com.example.wearos_gui;

import android.os.Bundle;

import android.app.Activity;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.wear.widget.WearableRecyclerView;
import androidx.wear.widget.WearableLinearLayoutManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends Activity {
    private WearableRecyclerView recyclerView;
    private TodoAdapter todoAdapter;
    private List<TodoItem> todoItems;
    private TextView itemIndexText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        itemIndexText = findViewById(R.id.item_index);

        // Initialize the RecyclerView and the to-do list
        recyclerView = findViewById(R.id.recyclerView);
        todoItems = new ArrayList<>();

        // Sample data
        todoItems.add(new TodoItem("Task 1"));

        todoItems.add(new TodoItem("Buy KPOP concert ticket by the end of this week"));
        todoItems.add(new TodoItem("Finish Lab 4"));

        todoAdapter = new TodoAdapter(todoItems);
        recyclerView.setAdapter(todoAdapter);

        // Set the LayoutManager to enable scrolling
        WearableLinearLayoutManager layoutManager = new WearableLinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // Enable circular scrolling and centering
        recyclerView.setCircularScrollingGestureEnabled(true);
        recyclerView.setEdgeItemsCenteringEnabled(true);

        // Initialize index display
        if (!todoItems.isEmpty()) {
            updateIndexDisplay(0);

            // Set up scroll listener to update the index display as the user scrolls
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    WearableLinearLayoutManager layoutManager = (WearableLinearLayoutManager) recyclerView.getLayoutManager();
                    if (layoutManager != null) {
                        int currentIndex = layoutManager.findFirstVisibleItemPosition();
                        int lastVisibleIndex = layoutManager.findLastVisibleItemPosition();
                        int totalItems = todoItems.size();

                        // If the last item is visible, set the index to the last item explicitly
                        if (lastVisibleIndex == totalItems - 1) {
                            updateIndexDisplay(totalItems - 1); // Set to last index
                        } else {
                            updateIndexDisplay(currentIndex);
                        }
                    }
                }
            });

        }
    }

    private void updateIndexDisplay(int currentIndex) {
        String indexText = (currentIndex + 1) + "/" + todoItems.size();
        itemIndexText.setText(indexText);
    }
}

