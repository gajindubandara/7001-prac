package com.iwfc.service;

import com.iwfc.exceptions.InvalidBookingException;
import com.iwfc.model.Booking;
import com.iwfc.model.Session;
import com.iwfc.model.SessionType;
import com.iwfc.model.Zone;
import com.iwfc.model.session.GenericSession;
import com.iwfc.model.session.HiitSession;
import com.iwfc.repository.Repository;
import com.iwfc.users.Instructor;
import com.iwfc.users.Member;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SchedulingServiceTest {

    private SchedulingService schedulingService;
    private Instructor instructor;
    private Member member;

    @BeforeEach
    void setUp() {
        Repository<Session> sessionRepository = new Repository<>(Session::getSessionId);
        Repository<Booking> bookingRepository = new Repository<>(Booking::getBookingId);
        schedulingService = new SchedulingService(sessionRepository, bookingRepository);
        instructor = new Instructor("IN-1", "Alex Coach", "alex@iwfc.com");
        member = new Member("ME-1", "Sam Member", "sam@iwfc.com");
    }

    @Test
    void createBookingEnrollsMemberAndPersistsBooking() throws Exception {
        HiitSession session = new HiitSession("SE-1", instructor, Zone.STUDIO_A,
                LocalDateTime.of(2026, 9, 21, 9, 0), 45);
        schedulingService.addSession(session);
        Booking booking = new Booking("BK-1", member, session, LocalDateTime.now());

        schedulingService.createBooking(booking);

        assertEquals(1, session.getEnrolledCount());
        assertTrue(schedulingService.findBookingById("BK-1").isPresent());
    }

    @Test
    void addSessionRejectsOverlappingSessionInSameZone() throws Exception {
        schedulingService.addSession(new HiitSession("SE-1", instructor, Zone.STUDIO_A,
                LocalDateTime.of(2026, 9, 21, 9, 0), 45));
        HiitSession overlapping = new HiitSession("SE-2", instructor, Zone.STUDIO_A,
                LocalDateTime.of(2026, 9, 21, 9, 15), 45);

        assertThrows(InvalidBookingException.class, () -> schedulingService.addSession(overlapping));
    }

    @Test
    void addSessionAllowsOverlappingTimeInDifferentZones() throws Exception {
        schedulingService.addSession(new HiitSession("SE-1", instructor, Zone.STUDIO_A,
                LocalDateTime.of(2026, 9, 21, 9, 0), 45));

        schedulingService.addSession(new HiitSession("SE-2", instructor, Zone.STUDIO_B,
                LocalDateTime.of(2026, 9, 21, 9, 0), 45));

        assertEquals(2, schedulingService.listSessions().size());
    }

    @Test
    void createBookingOutsideOperatingHoursThrows() throws Exception {
        // starts 21:30, duration 45m -> ends 22:15, past the 22:00 closing time
        HiitSession session = new HiitSession("SE-1", instructor, Zone.STUDIO_A,
                LocalDateTime.of(2026, 9, 21, 21, 30), 45);
        schedulingService.addSession(session);
        Booking booking = new Booking("BK-1", member, session, LocalDateTime.now());

        assertThrows(InvalidBookingException.class, () -> schedulingService.createBooking(booking));
    }

    @Test
    void createBookingOnFullSessionThrows() throws Exception {
        GenericSession session = new GenericSession("SE-1", SessionType.ZUMBA, instructor, Zone.STUDIO_A,
                LocalDateTime.of(2026, 9, 21, 9, 0), 45, 1);
        schedulingService.addSession(session);
        Member firstMember = new Member("ME-2", "Other Member", "other@iwfc.com");
        schedulingService.createBooking(new Booking("BK-1", firstMember, session, LocalDateTime.now()));

        Booking secondBooking = new Booking("BK-2", member, session, LocalDateTime.now());
        assertThrows(InvalidBookingException.class, () -> schedulingService.createBooking(secondBooking));
    }

    @Test
    void createBookingRejectsClashingTimeForSameMember() throws Exception {
        HiitSession sessionA = new HiitSession("SE-1", instructor, Zone.STUDIO_A,
                LocalDateTime.of(2026, 9, 21, 9, 0), 45);
        HiitSession sessionB = new HiitSession("SE-2", instructor, Zone.STUDIO_B,
                LocalDateTime.of(2026, 9, 21, 9, 15), 45);
        schedulingService.addSession(sessionA);
        schedulingService.addSession(sessionB);
        schedulingService.createBooking(new Booking("BK-1", member, sessionA, LocalDateTime.now()));

        Booking clashing = new Booking("BK-2", member, sessionB, LocalDateTime.now());
        assertThrows(InvalidBookingException.class, () -> schedulingService.createBooking(clashing));
    }

    @Test
    void recurringBookingBlocksSameWeekdayOverlappingSlot() throws Exception {
        HiitSession firstOccurrence = new HiitSession("SE-1", instructor, Zone.STUDIO_A,
                LocalDateTime.of(2026, 9, 21, 9, 0), 45);
        HiitSession nextWeekOccurrence = new HiitSession("SE-2", instructor, Zone.STUDIO_A,
                LocalDateTime.of(2026, 9, 28, 9, 0), 45); // same weekday, one week later
        schedulingService.addSession(firstOccurrence);
        schedulingService.addSession(nextWeekOccurrence);
        schedulingService.createRecurringBooking(
                List.of(new Booking("BK-1", member, firstOccurrence, LocalDateTime.now(), 4)));

        Booking clashingOccurrence = new Booking("BK-2", member, nextWeekOccurrence, LocalDateTime.now());
        assertThrows(InvalidBookingException.class, () -> schedulingService.createBooking(clashingOccurrence));
    }

    @Test
    void cancelBookingWithdrawsMemberFromSession() throws Exception {
        HiitSession session = new HiitSession("SE-1", instructor, Zone.STUDIO_A,
                LocalDateTime.of(2026, 9, 21, 9, 0), 45);
        schedulingService.addSession(session);
        schedulingService.createBooking(new Booking("BK-1", member, session, LocalDateTime.now()));

        schedulingService.cancelBooking("BK-1");

        assertTrue(schedulingService.findBookingById("BK-1").get().isCancelled());
        assertEquals(0, session.getEnrolledCount());
    }

    @Test
    void cancelBookingTwiceThrows() throws Exception {
        HiitSession session = new HiitSession("SE-1", instructor, Zone.STUDIO_A,
                LocalDateTime.of(2026, 9, 21, 9, 0), 45);
        schedulingService.addSession(session);
        schedulingService.createBooking(new Booking("BK-1", member, session, LocalDateTime.now()));
        schedulingService.cancelBooking("BK-1");

        assertThrows(IllegalStateException.class, () -> schedulingService.cancelBooking("BK-1"));
    }

    @Test
    void createRecurringBookingRejectsEmptyList() {
        assertThrows(IllegalArgumentException.class,
                () -> schedulingService.createRecurringBooking(List.of()));
    }
}
