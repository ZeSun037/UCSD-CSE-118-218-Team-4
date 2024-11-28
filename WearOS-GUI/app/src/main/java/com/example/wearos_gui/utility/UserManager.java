package com.example.wearos_gui.utility;

import com.example.wearos_gui.entity.User;

public class UserManager {
    private static UserManager instance;
    private User user;

    private UserManager() {}

    public static UserManager getInstance() {
        if (instance == null) {
            instance = new UserManager();
        }
        return instance;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}

