package com.iwfc.model.session;

import com.iwfc.model.Session;
import com.iwfc.model.SessionType;
import com.iwfc.model.Zone;
import com.iwfc.users.Instructor;

import java.time.LocalDateTime;

public class YogaSession extends Session {

    private static final int CAPACITY = 20;

    public YogaSession(String sessionId, Instructor instructor, Zone location, LocalDateTime startTime,
                        int durationMinutes) {
        super(sessionId, SessionType.YOGA, instructor, location, startTime, durationMinutes);
    }

    @Override
    public int getCapacity() {
        return CAPACITY;
    }
}
