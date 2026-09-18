package com.iwfc.facade;

import com.iwfc.exceptions.DuplicateDataException;
import com.iwfc.exceptions.InvalidBookingException;
import com.iwfc.exceptions.UnauthorizedAccessException;
import com.iwfc.model.Booking;
import com.iwfc.model.Equipment;
import com.iwfc.model.EquipmentStatus;
import com.iwfc.model.MaintenanceRequest;
import com.iwfc.model.Session;
import com.iwfc.model.Zone;
import com.iwfc.service.EquipmentService;
import com.iwfc.service.MaintenanceService;
import com.iwfc.service.SchedulingService;
import com.iwfc.service.UserService;
import com.iwfc.users.Administrator;
import com.iwfc.users.Role;
import com.iwfc.users.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class IWFCFacade {

    private final EquipmentService equipmentService;
    private final SchedulingService schedulingService;
    private final MaintenanceService maintenanceService;
    private final UserService userService;

    public IWFCFacade(EquipmentService equipmentService, SchedulingService schedulingService,
                       MaintenanceService maintenanceService, UserService userService) {
        this.equipmentService = Objects.requireNonNull(equipmentService, "equipmentService must not be null");
        this.schedulingService = Objects.requireNonNull(schedulingService, "schedulingService must not be null");
        this.maintenanceService = Objects.requireNonNull(maintenanceService, "maintenanceService must not be null");
        this.userService = Objects.requireNonNull(userService, "userService must not be null");
    }

    public void registerUser(User actor, User newUser) throws UnauthorizedAccessException, DuplicateDataException {
        requireRole(actor, Role.ADMINISTRATOR);
        userService.registerUser(newUser);
    }

    public List<User> listUsers(User actor) throws UnauthorizedAccessException {
        requireRole(actor, Role.ADMINISTRATOR);
        return userService.listAll();
    }

    public void registerEquipment(User actor, Equipment equipment)
            throws UnauthorizedAccessException, DuplicateDataException {
        requireRole(actor, Role.ADMINISTRATOR);
        equipmentService.registerEquipment(equipment);
    }

    public void removeEquipment(User actor, String equipmentId) throws UnauthorizedAccessException {
        requireRole(actor, Role.ADMINISTRATOR);
        equipmentService.removeEquipment(equipmentId);
    }

    public void deactivateEquipment(User actor, String equipmentId) throws UnauthorizedAccessException {
        requireRole(actor, Role.ADMINISTRATOR);
        equipmentService.deactivateEquipment(equipmentId);
    }

    public void updateEquipmentStatus(User actor, String equipmentId, EquipmentStatus status)
            throws UnauthorizedAccessException {
        requireRole(actor, Role.ADMINISTRATOR);
        equipmentService.updateEquipmentStatus(equipmentId, status);
    }

    public void updateEquipmentLocation(User actor, String equipmentId, Zone location)
            throws UnauthorizedAccessException {
        requireRole(actor, Role.ADMINISTRATOR);
        equipmentService.updateEquipmentLocation(equipmentId, location);
    }

    public boolean logEquipmentUsage(User actor, String equipmentId, double hours) throws UnauthorizedAccessException {
        requireRole(actor, Role.INSTRUCTOR, Role.ADMINISTRATOR);
        return equipmentService.logUsageHours(equipmentId, hours);
    }

    public Optional<Equipment> findEquipment(String equipmentId) {
        return equipmentService.findById(equipmentId);
    }

    public List<Equipment> getEquipmentNeedingMaintenance(User actor) throws UnauthorizedAccessException {
        requireRole(actor, Role.INSTRUCTOR, Role.ADMINISTRATOR);
        return equipmentService.getEquipmentNeedingMaintenance();
    }

    public void scheduleSession(User actor, Session session)
            throws UnauthorizedAccessException, DuplicateDataException, InvalidBookingException {
        requireRole(actor, Role.INSTRUCTOR, Role.ADMINISTRATOR);
        schedulingService.addSession(session);
    }

    public List<Session> listSessions() {
        return schedulingService.listSessions();
    }

    public Booking bookSession(User actor, Booking booking)
            throws UnauthorizedAccessException, InvalidBookingException, DuplicateDataException {
        requireSelfMember(actor, booking.getMember());
        return schedulingService.createBooking(booking);
    }

    public List<Booking> bookRecurringSession(User actor, List<Booking> bookings)
            throws UnauthorizedAccessException, InvalidBookingException, DuplicateDataException {
        for (Booking booking : bookings) {
            requireSelfMember(actor, booking.getMember());
        }
        return schedulingService.createRecurringBooking(bookings);
    }

    public void cancelBooking(User actor, String bookingId) throws UnauthorizedAccessException {
        Booking booking = schedulingService.findBookingById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("No booking found with id: " + bookingId));
        if (actor.getRole() != Role.ADMINISTRATOR && !actor.equals(booking.getMember())) {
            throw new UnauthorizedAccessException(actor.getFullName() + " may not cancel booking " + bookingId);
        }
        schedulingService.cancelBooking(bookingId);
    }

    public void reportMaintenanceIssue(User actor, MaintenanceRequest request)
            throws UnauthorizedAccessException, DuplicateDataException {
        requireRole(actor, Role.INSTRUCTOR, Role.ADMINISTRATOR);
        maintenanceService.reportIssue(request);
    }

    public void assignMaintenanceRequest(User actor, String requestId, Administrator admin, LocalDateTime assignedAt)
            throws UnauthorizedAccessException {
        requireRole(actor, Role.ADMINISTRATOR);
        maintenanceService.assignRequest(requestId, admin, assignedAt);
    }

    public void completeMaintenanceRequest(User actor, String requestId, LocalDateTime completedAt)
            throws UnauthorizedAccessException {
        requireRole(actor, Role.ADMINISTRATOR);
        maintenanceService.completeRequest(requestId, completedAt);
    }

    public List<MaintenanceRequest> listMaintenanceRequests(User actor) throws UnauthorizedAccessException {
        requireRole(actor, Role.INSTRUCTOR, Role.ADMINISTRATOR);
        return maintenanceService.listAll();
    }

    private void requireSelfMember(User actor, User bookingMember) throws UnauthorizedAccessException {
        if (actor.getRole() != Role.MEMBER || !actor.equals(bookingMember)) {
            throw new UnauthorizedAccessException(actor.getFullName() + " may only book sessions for themselves");
        }
    }

    private void requireRole(User actor, Role... allowedRoles) throws UnauthorizedAccessException {
        Objects.requireNonNull(actor, "actor must not be null");
        for (Role role : allowedRoles) {
            if (actor.getRole() == role) {
                return;
            }
        }
        throw new UnauthorizedAccessException(
                actor.getFullName() + " (" + actor.getRole() + ") is not authorized to perform this action");
    }
}
