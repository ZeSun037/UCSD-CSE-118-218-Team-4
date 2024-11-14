package com.example.wearos_gui;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
        holder.title.setText(item.getTitle());
    }

    @Override
    public int getItemCount() {
        return todoItems.size();
    }

    public static class TodoViewHolder extends RecyclerView.ViewHolder {
        TextView title, description;

        public TodoViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.titleTextView);
        }
    }
}

