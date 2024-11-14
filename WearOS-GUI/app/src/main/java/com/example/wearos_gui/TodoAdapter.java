package com.example.wearos_gui;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TodoAdapter extends RecyclerView.Adapter<TodoAdapter.TodoViewHolder> {
    private List<TodoItem> todoItems;

    public TodoAdapter(List<TodoItem> todoItems) {
        this.todoItems = todoItems;
    }

    @Override
    public TodoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.todo_item, parent, false);
        return new TodoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TodoViewHolder holder, int position) {
        TodoItem item = todoItems.get(position);
        String indexedTitle = (position + 1) + ". " + item.getTitle();
        holder.titleTextView.setText(indexedTitle);

        // Handle checkmark button click to delete item
        holder.checkmarkButton.setOnClickListener(v -> {
            todoItems.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, todoItems.size());
        });
    }

    @Override
    public int getItemCount() {
        return todoItems.size();
    }

    public static class TodoViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        ImageButton checkmarkButton;

        public TodoViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            checkmarkButton = itemView.findViewById(R.id.checkmark_button);
        }
    }
}

