package com.iwfc.service;

import com.iwfc.exceptions.DuplicateDataException;
import com.iwfc.exceptions.InvalidBookingException;
import com.iwfc.model.Booking;
import com.iwfc.model.Session;
import com.iwfc.repository.Repository;

import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class SchedulingService {

    private static final LocalTime OPENING_TIME = LocalTime.of(6, 0);
    private static final LocalTime CLOSING_TIME = LocalTime.of(22, 0);

    private final Repository<Session> sessionRepository;
    private final Repository<Booking> bookingRepository;

    public SchedulingService(Repository<Session> sessionRepository, Repository<Booking> bookingRepository) {
        this.sessionRepository = Objects.requireNonNull(sessionRepository, "sessionRepository must not be null");
        this.bookingRepository = Objects.requireNonNull(bookingRepository, "bookingRepository must not be null");
    }

    public void addSession(Session session) throws DuplicateDataException, InvalidBookingException {
        validateNoZoneClash(session);
        sessionRepository.add(session);
    }

    public Booking createBooking(Booking booking) throws InvalidBookingException, DuplicateDataException {
        return createRecurringBooking(Collections.singletonList(booking)).get(0);
    }

    public List<Booking> createRecurringBooking(List<Booking> occurrenceBookings)
            throws InvalidBookingException, DuplicateDataException {
        Objects.requireNonNull(occurrenceBookings, "occurrenceBookings must not be null");
        if (occurrenceBookings.isEmpty()) {
            throw new IllegalArgumentException("occurrenceBookings must not be empty");
        }
        for (Booking booking : occurrenceBookings) {
            Session session = booking.getSession();
            validateOperatingHours(session);
            if (session.isFull()) {
                throw new InvalidBookingException("Session " + session.getSessionId() + " is at full capacity");
            }
            validateNoDoubleBooking(booking.getMember().getUserId(), session);
        }
        for (Booking booking : occurrenceBookings) {
            booking.getSession().enrollMember(booking.getMember().getUserId());
            bookingRepository.add(booking);
        }
        return occurrenceBookings;
    }

    public void cancelBooking(String bookingId) {
        Booking booking = getRequiredBooking(bookingId);
        if (booking.isCancelled()) {
            throw new IllegalStateException("Booking " + bookingId + " is already cancelled");
        }
        booking.cancel();
        booking.getSession().withdrawMember(booking.getMember().getUserId());
    }

    public Optional<Booking> findBookingById(String bookingId) {
        return bookingRepository.findById(bookingId);
    }

    public List<Booking> listBookings() {
        return bookingRepository.findAll();
    }

    public List<Session> listSessions() {
        return sessionRepository.findAll();
    }

    private void validateOperatingHours(Session session) throws InvalidBookingException {
        LocalTime start = session.getStartTime().toLocalTime();
        LocalTime end = start.plusMinutes(session.getDurationMinutes());
        boolean crossesMidnight = end.isBefore(start);
        if (crossesMidnight || start.isBefore(OPENING_TIME) || end.isAfter(CLOSING_TIME)) {
            throw new InvalidBookingException("Session " + session.getSessionId()
                    + " falls outside operating hours (" + OPENING_TIME + "-" + CLOSING_TIME + ")");
        }
    }

    private void validateNoZoneClash(Session newSession) throws InvalidBookingException {
        // Only looks at Sessions that already exist. A recurring booking doesn't spawn real
        // Session objects for its future weeks, so there's nothing there yet to clash-check.
        boolean clash = sessionRepository.findAll().stream()
                .filter(existing -> existing.getLocation() == newSession.getLocation())
                .anyMatch(existing -> timesOverlap(existing, newSession));
        if (clash) {
            throw new InvalidBookingException("Zone " + newSession.getLocation()
                    + " already has a session scheduled that overlaps this time");
        }
    }

    private void validateNoDoubleBooking(String memberId, Session newSession) throws InvalidBookingException {
        boolean clash = bookingRepository.findAll().stream()
                .filter(booking -> !booking.isCancelled())
                .filter(booking -> booking.getMember().getUserId().equals(memberId))
                .anyMatch(booking -> clashesWith(booking, newSession));
        if (clash) {
            throw new InvalidBookingException(
                    "Member " + memberId + " already has a clashing booking for this time slot");
        }
    }

    private boolean clashesWith(Booking existingBooking, Session newSession) {
        Session existingSession = existingBooking.getSession();
        if (existingBooking.isRecurring()) {
            // Recurring bookings don't have real future Sessions to compare against, so we fake
            // it: same weekday + overlapping time-of-day counts as a clash on any future week.
            return existingSession.getStartTime().getDayOfWeek() == newSession.getStartTime().getDayOfWeek()
                    && timeRangesOverlap(existingSession, newSession);
        }
        return timesOverlap(existingSession, newSession);
    }

    private boolean timesOverlap(Session a, Session b) {
        return a.getStartTime().isBefore(b.getEndTime()) && b.getStartTime().isBefore(a.getEndTime());
    }

    private boolean timeRangesOverlap(Session a, Session b) {
        LocalTime aStart = a.getStartTime().toLocalTime();
        LocalTime aEnd = aStart.plusMinutes(a.getDurationMinutes());
        LocalTime bStart = b.getStartTime().toLocalTime();
        LocalTime bEnd = bStart.plusMinutes(b.getDurationMinutes());
        return aStart.isBefore(bEnd) && bStart.isBefore(aEnd);
    }

    private Booking getRequiredBooking(String bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("No booking found with id: " + bookingId));
    }
}
