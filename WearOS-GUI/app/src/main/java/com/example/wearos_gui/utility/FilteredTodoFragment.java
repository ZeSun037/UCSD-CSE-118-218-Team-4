package com.example.wearos_gui.utility;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.wearos_gui.R;
import com.example.wearos_gui.entity.TodoItem;
import com.example.wearos_gui.entity.User;
import com.example.wearos_gui.storage.TodoDatabase;

import java.util.ArrayList;
import java.util.List;

public class FilteredTodoFragment extends Fragment {
    private final static String GROUP_ID = "team4";
    private List<TodoItem> allTodoItems;
    private TodoDatabase todoDatabase;
    private User user;
    private double lat;
    private double lng;
    private long time;

    public FilteredTodoFragment(FilterStrategy filterStrategy, List<TodoItem> allTodoItems, TodoDatabase database,
                                User user) {
        this.allTodoItems = allTodoItems;
        this.todoDatabase = database;
        this.user = user;
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
        renderTodoList(view);
    }

    public void updateLocationAndTime(double lat, double lng, long curTime) {
        this.lat = lat;
        this.lng = lng;
        this.time = curTime;

        if (getView() != null) {
            renderTodoList(getView());
        }
        Log.d("DEBUG", "Updating location and time");
    }

    private void renderTodoList(View view) {
        LinearLayout todoListContainer = view.findViewById(R.id.todoListContainer);
        todoListContainer.removeAllViews();

        // Create the header
//        TextView headerView = new TextView(getContext());
//        String headerText = "";
//        switch (filterStrategy.getClass().getSimpleName()) {
//            case "TodayFilter":
//                headerText = "Today's";
//                break;
//            case "TomorrowFilter":
//                headerText = "Tomorrow's";
//                break;
//            case "ThisWeekFilter":
//                headerText = "This Week's";
//                break;
//            case "GroupFilter":
//                headerText = "Group To-Dos";
//                break;
//            default:
//                headerText = "My To-Do List";
//        }
//        headerView.setText(headerText);
//        headerView.setTextColor(Color.BLACK);
//        headerView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
//        headerView.setTypeface(null, Typeface.BOLD);
//
//        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
//                LinearLayout.LayoutParams.WRAP_CONTENT,
//                LinearLayout.LayoutParams.WRAP_CONTENT
//        );
//        layoutParams.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
//        headerView.setLayoutParams(layoutParams);
//        todoListContainer.addView(headerView);

        // Filter the to-do items using the selected strategy
//        List<TodoItem> filteredItems = filterStrategy.filter(allTodoItems);

        // Separate items into "Done" and "Not Done" categories
        List<TodoItem> doneItems = new ArrayList<>();
        List<TodoItem> notDoneItems = new ArrayList<>();
        for (TodoItem item : allTodoItems) {
            if (item.getDone()) {
                doneItems.add(item);
            } else {
                notDoneItems.add(item);
            }
        }
        TodoSorter.sortNotDoneItems(notDoneItems, this.user, lat, lng, time);

        // Create and add "Not Done" header and items
        addSectionHeader(todoListContainer, "Not Done", notDoneItems.size());
        addItemsToList(todoListContainer, notDoneItems, false);

        // Create and add "Done" header and items
        addSectionHeader(todoListContainer, "Done", doneItems.size());
        addItemsToList(todoListContainer, doneItems, true);
    }


    private void addSectionHeader(LinearLayout container, String status, int itemCount) {
        // Create header for each status (Done/Not Done)
        TextView headerView = new TextView(getContext());
        headerView.setText(status + " (" + itemCount + ")");
        headerView.setTextColor(Color.GRAY);
        headerView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        headerView.setPadding(0, 8, 0, 4); // Padding for spacing

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams.gravity = Gravity.TOP;
        headerView.setLayoutParams(layoutParams);
        container.addView(headerView);
    }

    private void addItemsToList(LinearLayout container, List<TodoItem> items, Boolean isDone) {
        // Add each to-do item to the container
        for (int i = 0; i < items.size(); i++) {
            LinearLayout itemContainer = new LinearLayout(getContext());
            itemContainer.setOrientation(LinearLayout.HORIZONTAL);
            itemContainer.setPadding(2, 2, 2, 2);

            TodoItem item = items.get(i);

            // TextView for the to-do index
            TextView todoIndexView = new TextView(getContext());
            LinearLayout.LayoutParams indexParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            indexParams.rightMargin = 8; // Add a little space between the index and title
            todoIndexView.setLayoutParams(indexParams);
            todoIndexView.setText((i + 1) + ".");
            todoIndexView.setTextSize(14);
            todoIndexView.setTextColor(Color.BLACK);

            itemContainer.addView(todoIndexView);

            // TextView for the to-do title
            TextView todoTextView = new TextView(getContext());
            if (item.getAssignee().equals("")) {
                todoTextView.setText(item.getTitle());
            } else {
                todoTextView.setText(item.getAssignee() + ": " + item.getTitle());
            }
            todoTextView.setTextSize(14);
            todoTextView.setTextColor(Color.BLACK);
            todoTextView.setMaxLines(Integer.MAX_VALUE); // Allow unlimited lines for wrapping
            todoTextView.setEllipsize(null); // Disable ellipsis
            todoTextView.setLayoutParams(new LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f)); // Weight for title

            itemContainer.addView(todoTextView);

            // Add checkbox for items that are not marked as "Done"
            if (!isDone) {
                CheckBox checkBox = new CheckBox(getContext());
                checkBox.setChecked(item.getDone());
                LinearLayout.LayoutParams checkboxParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                checkboxParams.gravity = Gravity.CENTER_VERTICAL;
                checkBox.setLayoutParams(checkboxParams);
                checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    // Update item status when checkbox is toggled
                    item.setDone(isChecked);
                    renderTodoList(getView()); // Refresh the list dynamically

                    // Update the database
                    todoDatabase.updateTodo(item);

                    // Update the REDIS server
                    if (!item.getAssignee().isEmpty()) {
                        RedisHelper.updateTodos(GROUP_ID, item);
                    }
                });

                itemContainer.addView(checkBox);
            }

            container.addView(itemContainer);
        }
    }
}

