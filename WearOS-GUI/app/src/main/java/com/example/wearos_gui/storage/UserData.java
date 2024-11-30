package com.example.wearos_gui.storage;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.wearos_gui.entity.User;
import com.google.gson.Gson;

public class UserData {
    private static final String PREFS_NAME = "UserPrefs";
    private static final String KEY_USER = "user";

    // Method to save the User object into SharedPreferences
    public static void saveUser(Context context, User user) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Convert the User object to a JSON string using Gson
        Gson gson = new Gson();
        String userJson = gson.toJson(user);

        // Save the JSON string in SharedPreferences
        editor.putString(KEY_USER, userJson);
        editor.apply();
    }

    // Method to retrieve the User object from SharedPreferences
    public static User getUser(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        // Get the stored JSON string from SharedPreferences
        String userJson = sharedPreferences.getString(KEY_USER, null);

        if (userJson != null) {
            // Convert the JSON string back to a User object
            Gson gson = new Gson();
            return gson.fromJson(userJson, User.class);
        }

        return null;  // Return null if no user is stored
    }
}
