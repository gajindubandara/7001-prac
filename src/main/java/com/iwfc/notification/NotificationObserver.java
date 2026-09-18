package com.iwfc.notification;

// Observer pattern - anything that wants to hear about maintenance events implements this.
public interface NotificationObserver {

    void onNotify(String message);
}
