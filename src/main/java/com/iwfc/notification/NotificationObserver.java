package com.iwfc.notification;

// Observer pattern - anything that wants to hear about maintenance events implements this.
// An interface here, not an abstract class like User/Equipment/Session - there's no
// shared state or default behaviour to inherit, just a contract to implement.
public interface NotificationObserver {

    void onNotify(String message);
}
