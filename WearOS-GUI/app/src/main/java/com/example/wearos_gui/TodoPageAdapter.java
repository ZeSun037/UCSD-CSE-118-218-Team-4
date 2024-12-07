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
    private List<TodoItem> personalTodoItems; // Current list of to-do items
    private List<TodoItem> groupTodoItems;
    private TodoDatabase personalTodoDatabase;
    private TodoDatabase groupTodoDatabase;
    private User user;

    public TodoPageAdapter(FragmentActivity fa, List<TodoItem> personalItems, TodoDatabase personalDatabase,
                           List<TodoItem> groupItems, TodoDatabase groupDatabase, User user) {
        super(fa);
        this.personalTodoItems = personalItems;
        this.personalTodoDatabase = personalDatabase;
        this.groupTodoItems = groupItems;
        this.groupTodoDatabase = groupDatabase;
        this.user = user;
    }

    public void updateItems(List<TodoItem> newItems, int position, TodoDatabase todoDatabase) {
        if (position == 0) {
            this.personalTodoItems = newItems;
            this.personalTodoDatabase = todoDatabase;
        } else {
            this.groupTodoItems = newItems;
            this.groupTodoDatabase = todoDatabase;
        }
        notifyItemChanged(position);
    }


    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0: return new FilteredTodoFragment(
                    personalTodoItems, this.personalTodoDatabase, "Personal", this.user);
            case 1: return new FilteredTodoFragment(
                    groupTodoItems, this.groupTodoDatabase, "Group", this.user);
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

