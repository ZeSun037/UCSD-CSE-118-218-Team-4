package com.example.wearos_gui;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.List;

public class TodoPageAdapter extends FragmentStateAdapter {
    private List<TodoItem> todoItems;

    public TodoPageAdapter(FragmentActivity fa, List<TodoItem> todoItems) {
        super(fa);
        this.todoItems = todoItems;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 1: return new FilteredTodoFragment(FilterFactory.getFilter("Today"), todoItems);
            case 2: return new FilteredTodoFragment(FilterFactory.getFilter("Tomorrow"), todoItems);
            case 3: return new FilteredTodoFragment(FilterFactory.getFilter("This Week"), todoItems);
            default: return new FilteredTodoFragment(FilterFactory.getFilter("All"), todoItems);
        }
    }

    @Override
    public int getItemCount() {
        return 4; // Number of filter views
    }
}

