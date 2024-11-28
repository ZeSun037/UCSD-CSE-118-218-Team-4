package com.example.wearos_gui;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.wearos_gui.entity.TodoItem;
import com.example.wearos_gui.entity.User;
import com.example.wearos_gui.storage.TodoDatabase;
import com.example.wearos_gui.utility.FilterFactory;
import com.example.wearos_gui.utility.FilteredTodoFragment;

import java.util.List;

public class TodoPageAdapter extends FragmentStateAdapter {
    private List<TodoItem> todoItems; // Current list of to-do items
    private String currentFilter;    // Keeps track of the current filter ("Personal" or "Group")
    private TodoDatabase todoDatabase;
    private User user;
    private double lat;
    private double lng;
    private long time;

    public TodoPageAdapter(FragmentActivity fa, List<TodoItem> todoItems, TodoDatabase todoDatabase,
                           User user, long time) {
        super(fa);
        this.todoItems = todoItems;
        this.currentFilter = "Personal"; // Default filter
        this.todoDatabase = todoDatabase;
        this.user = user;
        this.time = time;
    }

    public void updateItems(List<TodoItem> newItems, String filter, TodoDatabase todoDatabase) {
        this.todoItems = newItems;
        this.currentFilter = filter;
        this.todoDatabase = todoDatabase;
        notifyDataSetChanged(); // Refresh the fragments
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0: // Personal to-dos
                if ("Personal".equals(currentFilter)) {
                    return new FilteredTodoFragment(FilterFactory.getFilter("Personal"),
                            todoItems, this.todoDatabase, this.user, this.lat, this.lng, this.time);
                } else { // If user toggles while on a "Personal" tab
                    return new FilteredTodoFragment(FilterFactory.getFilter("Group"),
                            todoItems, this.todoDatabase, this.user, this.lat, this.lng, this.time);
                }
            case 1: // Group to-dos
                if ("Group".equals(currentFilter)) {
                    return new FilteredTodoFragment(FilterFactory.getFilter("Group"),
                            todoItems, this.todoDatabase, this.user, this.lat, this.lng, this.time);
                } else { // If user toggles while on a "Group" tab
                    return new FilteredTodoFragment(FilterFactory.getFilter("Personal"),
                            todoItems, this.todoDatabase, this.user, this.lat, this.lng, this.time);
                }
            default:
                throw new IllegalStateException("Invalid position for fragment creation");
        }
//        switch (position) {
//            case 1: return new FilteredTodoFragment(FilterFactory.getFilter("Today"), todoItems);
//            case 2: return new FilteredTodoFragment(FilterFactory.getFilter("Tomorrow"), todoItems);
//            case 3: return new FilteredTodoFragment(FilterFactory.getFilter("This Week"), todoItems);
//            default: return new FilteredTodoFragment(FilterFactory.getFilter("Personal"), todoItems);
//        }
    }

    @Override
    public int getItemCount() {
        return 2; // Number of filter views
    }
}

