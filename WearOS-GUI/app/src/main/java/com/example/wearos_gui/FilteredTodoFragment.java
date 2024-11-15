package com.example.wearos_gui;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.w3c.dom.Text;

import java.util.List;

public class FilteredTodoFragment extends Fragment {
    private FilterStrategy filterStrategy;
    private List<TodoItem> allTodoItems;

    public FilteredTodoFragment(FilterStrategy filterStrategy, List<TodoItem> allTodoItems) {
        this.filterStrategy = filterStrategy;
        this.allTodoItems = allTodoItems;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_todo_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        LinearLayout todoListContainer = view.findViewById(R.id.todoListContainer);
        // Create the header
        TextView headerView = new TextView(getContext());
        String headerText = "";
        switch (filterStrategy.getClass().getSimpleName()) {
            case "TodayFilter":
                headerText = "Today's";
                break;
            case "TomorrowFilter":
                headerText = "Tomorrow's";
                break;
            case "ThisWeekFilter":
                headerText = "This Week's";
                break;
            default:
                headerText = "My To-Do List";
        }
        headerView.setText(headerText);
        headerView.setTextColor(Color.BLACK);
        headerView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        headerView.setTypeface(null, Typeface.BOLD);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
        headerView.setLayoutParams(layoutParams);
        todoListContainer.addView(headerView);


        // Filter the to-do items using the selected strategy
        List<TodoItem> filteredItems = filterStrategy.filter(allTodoItems);

        for (int i = 0; i < filteredItems.size(); i++) {
            TodoItem item = filteredItems.get(i);

            // Create a view for each to-do item
            TextView todoTextView = new TextView(getContext());
            todoTextView.setText((i + 1) + ". " + item.getTitle()); // Display index + title
            todoTextView.setTextSize(16);
            todoTextView.setPadding(4, 10, 4, 10);

            // Set up onClickListener to mark as complete or open detail view
            todoTextView.setOnClickListener(v -> {
                // Code to mark as complete or open detailed view
            });

            todoListContainer.addView(todoTextView);
        }
    }
}

