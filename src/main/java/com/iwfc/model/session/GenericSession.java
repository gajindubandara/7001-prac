package com.iwfc.model.session;

import com.iwfc.model.Session;
import com.iwfc.model.SessionType;
import com.iwfc.model.Zone;
import com.iwfc.users.Instructor;

import java.time.LocalDateTime;

public class GenericSession extends Session {

    private final int capacity;

    public GenericSession(String sessionId, SessionType type, Instructor instructor, Zone location,
                           LocalDateTime startTime, int durationMinutes, int capacity) {
        super(sessionId, type, instructor, location, startTime, durationMinutes);
        if (capacity <= 0) {
            throw new IllegalArgumentException("capacity must be positive");
        }
        this.capacity = capacity;
    }

    @Override
    public int getCapacity() {
        return capacity;
    }
}
