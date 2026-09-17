package com.iwfc.notification;

import com.iwfc.users.User;

import java.util.Objects;

public class ConsoleNotificationObserver implements NotificationObserver {

    private final User user;

    public ConsoleNotificationObserver(User user) {
        this.user = Objects.requireNonNull(user, "user must not be null");
    }

    @Override
    public void onNotify(String message) {
        System.out.println("[Notify -> " + user.getFullName() + "] " + message);
    }
}
