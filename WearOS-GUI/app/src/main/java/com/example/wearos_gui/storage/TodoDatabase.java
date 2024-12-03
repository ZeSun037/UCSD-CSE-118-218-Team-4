package com.example.wearos_gui.storage;

import android.content.SharedPreferences;
import android.content.Context;
import android.util.Log;

import com.example.wearos_gui.entity.TodoItem;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class TodoDatabase {
    private static TodoDatabase instance;
    private SharedPreferences sharedPreferences;

    public TodoDatabase(Context context, String key) {
        this.sharedPreferences = context.getSharedPreferences(key, Context.MODE_PRIVATE);
    }

    public static synchronized TodoDatabase getInstance(Context context, String key) {
        if (instance == null) {
            instance = new TodoDatabase(context, key);
        }
        return instance;
    }

    public void insertTodo(TodoItem todoItem) {
        Gson gson = new Gson();
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Get existing list or create a new list
        String todoListJson = sharedPreferences.getString("todo_list", "[]"); // Default to empty JSON array
        Type listType = new TypeToken<List<TodoItem>>() {}.getType();
        List<TodoItem> todoList = gson.fromJson(todoListJson, listType); // Deserialize existing list

        // Add new item
        todoList.add(todoItem);

        // Save updated list
        String updatedTodoListJson = gson.toJson(todoList); // Serialize back to JSON
        editor.putString("todo_list", updatedTodoListJson);
        editor.apply();
    }

    public List<TodoItem> getAllTodos() {
        String todoListJson = sharedPreferences.getString("todo_list", "[]"); // Default to empty JSON array
        Gson gson = new Gson();
        Type listType = new TypeToken<List<TodoItem>>() {}.getType();

        return gson.fromJson(todoListJson, listType); // Deserialize as list of TodoItem
    }

    public boolean containsTodo(TodoItem todo) {
        List<TodoItem> todos = getAllTodos();
        return todos.contains(todo);
    }

    public void updateTodo(TodoItem updatedTodo) {
        List<TodoItem> todos = getAllTodos();
        for (int i = 0; i < todos.size(); i++) {
            if (todos.get(i).getTitle().equals(updatedTodo.getTitle())) {
                // Replace the old to-do with the updated one
                todos.set(i, updatedTodo);
                break;
            }
        }
        saveTodos(todos);
    }

    public void deleteTodo(String title) {
        List<TodoItem> todos = getAllTodos();
        for (int i = 0; i < todos.size(); i++) {
            if (todos.get(i).getTitle().equals(title)) {
                todos.remove(i);
                break;
            }
        }
        saveTodos(todos);
    }

    public void removeTodoList() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("todo_list");
        editor.apply();
    }

    private void saveTodos(List<TodoItem> todos) {
        Gson gson = new Gson();
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Serialize the list to a JSON string
        String updatedTodoListJson = gson.toJson(todos);

        // Save the JSON string to SharedPreferences
        editor.putString("todo_list", updatedTodoListJson);
        editor.apply();
    }
}
