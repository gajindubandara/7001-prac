package com.iwfc.model;

import com.iwfc.users.Member;

import java.time.LocalDateTime;
import java.util.Objects;

public class Booking {

    private final String bookingId;
    private final Member member;
    private final Session session;
    private final LocalDateTime bookingTime;
    private final int recurrenceWeeks;
    private boolean cancelled;

    public Booking(String bookingId, Member member, Session session, LocalDateTime bookingTime) {
        this(bookingId, member, session, bookingTime, 0);
    }

    public Booking(String bookingId, Member member, Session session, LocalDateTime bookingTime, int recurrenceWeeks) {
        this.bookingId = Objects.requireNonNull(bookingId, "bookingId must not be null");
        this.member = Objects.requireNonNull(member, "member must not be null");
        this.session = Objects.requireNonNull(session, "session must not be null");
        this.bookingTime = Objects.requireNonNull(bookingTime, "bookingTime must not be null");
        if (recurrenceWeeks < 0) {
            throw new IllegalArgumentException("recurrenceWeeks must not be negative");
        }
        this.recurrenceWeeks = recurrenceWeeks;
        this.cancelled = false;
    }

    public String getBookingId() {
        return bookingId;
    }

    public Member getMember() {
        return member;
    }

    public Session getSession() {
        return session;
    }

    public LocalDateTime getBookingTime() {
        return bookingTime;
    }

    public boolean isRecurring() {
        return recurrenceWeeks > 0;
    }

    public int getRecurrenceWeeks() {
        return recurrenceWeeks;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void cancel() {
        this.cancelled = true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Booking)) {
            return false;
        }
        Booking booking = (Booking) o;
        return bookingId.equals(booking.bookingId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bookingId);
    }

    @Override
    public String toString() {
        return "Booking " + bookingId + " [" + member.getUserId() + " -> " + session.getSessionId() + "]"
                + (cancelled ? " CANCELLED" : "");
    }
}
