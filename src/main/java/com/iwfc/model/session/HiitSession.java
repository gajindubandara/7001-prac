package com.iwfc.model.session;

import com.iwfc.model.Session;
import com.iwfc.model.SessionType;
import com.iwfc.model.Zone;
import com.iwfc.users.Instructor;

import java.time.LocalDateTime;

public class HiitSession extends Session {

    private static final int CAPACITY = 12;

    public HiitSession(String sessionId, Instructor instructor, Zone location, LocalDateTime startTime,
                        int durationMinutes) {
        super(sessionId, SessionType.HIIT, instructor, location, startTime, durationMinutes);
    }

    @Override
    public int getCapacity() {
        return CAPACITY;
    }
}
