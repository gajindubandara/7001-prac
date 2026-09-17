package com.iwfc.model;

import com.iwfc.exceptions.InvalidBookingException;
import com.iwfc.users.Instructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public abstract class Session {

    private final String sessionId;
    private final SessionType type;
    private final Instructor instructor;
    private Zone location;
    private LocalDateTime startTime;
    private final int durationMinutes;
    private final List<String> enrolledMemberIds;

    protected Session(String sessionId, SessionType type, Instructor instructor, Zone location,
                       LocalDateTime startTime, int durationMinutes) {
        this.sessionId = Objects.requireNonNull(sessionId, "sessionId must not be null");
        this.type = Objects.requireNonNull(type, "type must not be null");
        this.instructor = Objects.requireNonNull(instructor, "instructor must not be null");
        this.location = Objects.requireNonNull(location, "location must not be null");
        this.startTime = Objects.requireNonNull(startTime, "startTime must not be null");
        if (durationMinutes <= 0) {
            throw new IllegalArgumentException("durationMinutes must be positive");
        }
        this.durationMinutes = durationMinutes;
        this.enrolledMemberIds = new ArrayList<>();
    }

    public String getSessionId() {
        return sessionId;
    }

    public SessionType getType() {
        return type;
    }

    public Instructor getInstructor() {
        return instructor;
    }

    public Zone getLocation() {
        return location;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public LocalDateTime getEndTime() {
        return startTime.plusMinutes(durationMinutes);
    }

    public void reschedule(Zone newLocation, LocalDateTime newStartTime) {
        this.location = Objects.requireNonNull(newLocation, "newLocation must not be null");
        this.startTime = Objects.requireNonNull(newStartTime, "newStartTime must not be null");
    }

    public abstract int getCapacity();

    public List<String> getEnrolledMemberIds() {
        return Collections.unmodifiableList(enrolledMemberIds);
    }

    public int getEnrolledCount() {
        return enrolledMemberIds.size();
    }

    public boolean isFull() {
        return enrolledMemberIds.size() >= getCapacity();
    }

    public void enrollMember(String memberId) throws InvalidBookingException {
        Objects.requireNonNull(memberId, "memberId must not be null");
        if (enrolledMemberIds.contains(memberId)) {
            throw new InvalidBookingException("Member " + memberId + " is already enrolled in session " + sessionId);
        }
        if (isFull()) {
            throw new InvalidBookingException("Session " + sessionId + " is at full capacity");
        }
        enrolledMemberIds.add(memberId);
    }

    public void withdrawMember(String memberId) {
        enrolledMemberIds.remove(memberId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Session)) {
            return false;
        }
        Session session = (Session) o;
        return sessionId.equals(session.sessionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sessionId);
    }

    @Override
    public String toString() {
        return type + " " + sessionId + " @ " + location + " (" + startTime + ", " + durationMinutes + "min)";
    }
}
