package com.iwfc.notification;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class NotificationService {

    private final List<NotificationObserver> observers = new ArrayList<>();

    public void subscribe(NotificationObserver observer) {
        observers.add(Objects.requireNonNull(observer, "observer must not be null"));
    }

    public void unsubscribe(NotificationObserver observer) {
        observers.remove(observer);
    }

    public void notifyObservers(String message) {
        Objects.requireNonNull(message, "message must not be null");
        for (NotificationObserver observer : observers) {
            observer.onNotify(message);
        }
    }
}
