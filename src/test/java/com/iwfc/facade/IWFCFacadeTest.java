package com.iwfc.facade;

import com.iwfc.exceptions.UnauthorizedAccessException;
import com.iwfc.model.Booking;
import com.iwfc.model.Equipment;
import com.iwfc.model.MaintenanceRequest;
import com.iwfc.model.Session;
import com.iwfc.model.Zone;
import com.iwfc.model.equipment.Treadmill;
import com.iwfc.model.session.HiitSession;
import com.iwfc.notification.NotificationService;
import com.iwfc.repository.Repository;
import com.iwfc.service.EquipmentService;
import com.iwfc.service.MaintenanceService;
import com.iwfc.service.SchedulingService;
import com.iwfc.service.UserService;
import com.iwfc.users.Administrator;
import com.iwfc.users.Instructor;
import com.iwfc.users.Member;
import com.iwfc.users.User;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class IWFCFacadeTest {

    private IWFCFacade facade;
    private Administrator admin;
    private Instructor instructor;
    private Member member;
    private Member otherMember;

    @BeforeEach
    void setUp() {
        EquipmentService equipmentService = new EquipmentService(new Repository<>(Equipment::getEquipmentId));
        SchedulingService schedulingService = new SchedulingService(
                new Repository<>(Session::getSessionId), new Repository<>(Booking::getBookingId));
        MaintenanceService maintenanceService = new MaintenanceService(
                new Repository<>(MaintenanceRequest::getRequestId), new NotificationService());
        UserService userService = new UserService(new Repository<>(User::getUserId));
        facade = new IWFCFacade(equipmentService, schedulingService, maintenanceService, userService);

        admin = new Administrator("U-ADMIN", "Alice Admin", "alice@iwfc.local");
        instructor = new Instructor("U-INSTR", "Ian Instructor", "ian@iwfc.local");
        member = new Member("U-MEMBER", "Mona Member", "mona@iwfc.local");
        otherMember = new Member("U-MEMBER-2", "Otto Other", "otto@iwfc.local");
    }

    @Test
    void memberCannotRegisterEquipment() {
        Equipment treadmill = new Treadmill("EQ-1", Zone.CARDIO_ZONE);

        assertThrows(UnauthorizedAccessException.class, () -> facade.registerEquipment(member, treadmill));
    }

    @Test
    void administratorCanRegisterEquipment() throws Exception {
        Equipment treadmill = new Treadmill("EQ-1", Zone.CARDIO_ZONE);

        facade.registerEquipment(admin, treadmill);

        assertTrue(facade.findEquipment("EQ-1").isPresent());
    }

    @Test
    void memberCannotViewMaintenanceRequests() {
        assertThrows(UnauthorizedAccessException.class, () -> facade.listMaintenanceRequests(member));
    }

    @Test
    void memberCannotRegisterUser() {
        Member newMember = new Member("U-MEMBER-3", "Nia New", "nia@iwfc.local");

        assertThrows(UnauthorizedAccessException.class, () -> facade.registerUser(member, newMember));
    }

    @Test
    void administratorCanRegisterAndListUsers() throws Exception {
        facade.registerUser(admin, member);

        List<User> users = facade.listUsers(admin);

        assertTrue(users.contains(member));
    }

    @Test
    void memberCannotListUsers() {
        assertThrows(UnauthorizedAccessException.class, () -> facade.listUsers(member));
    }

    @Test
    void memberCannotBookOnBehalfOfAnotherMember() throws Exception {
        facade.scheduleSession(instructor, new HiitSession("SS-1", instructor, Zone.STUDIO_A,
                LocalDateTime.of(2026, 9, 21, 9, 0), 45));
        Session session = facade.listSessions().get(0);
        Booking bookingForOther = new Booking("BK-1", otherMember, session, LocalDateTime.now());

        assertThrows(UnauthorizedAccessException.class, () -> facade.bookSession(member, bookingForOther));
    }

    @Test
    void memberCannotCancelAnotherMembersBooking() throws Exception {
        facade.scheduleSession(instructor, new HiitSession("SS-1", instructor, Zone.STUDIO_A,
                LocalDateTime.of(2026, 9, 21, 9, 0), 45));
        Session session = facade.listSessions().get(0);
        Booking booking = new Booking("BK-1", member, session, LocalDateTime.now());
        facade.bookSession(member, booking);

        assertThrows(UnauthorizedAccessException.class, () -> facade.cancelBooking(otherMember, "BK-1"));
    }
}
