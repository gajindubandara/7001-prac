package com.iwfc.app;

import com.iwfc.exceptions.IWFCException;
import com.iwfc.facade.IWFCFacade;
import com.iwfc.model.Booking;
import com.iwfc.model.Equipment;
import com.iwfc.model.EquipmentStatus;
import com.iwfc.model.EquipmentType;
import com.iwfc.model.MaintenanceRequest;
import com.iwfc.model.Session;
import com.iwfc.model.SessionType;
import com.iwfc.model.Urgency;
import com.iwfc.model.Zone;
import com.iwfc.model.session.GenericSession;
import com.iwfc.model.session.HiitSession;
import com.iwfc.model.session.PilatesSession;
import com.iwfc.model.session.YogaSession;
import com.iwfc.factory.EquipmentFactory;
import com.iwfc.users.Administrator;
import com.iwfc.users.Instructor;
import com.iwfc.users.Member;
import com.iwfc.users.Role;
import com.iwfc.users.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

class ConsoleMenu {

    private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final double DEFAULT_GENERIC_SESSION_CAPACITY = 10;

    private final IWFCFacade facade;
    private final List<User> users;
    private final Scanner scanner;

    ConsoleMenu(IWFCFacade facade, List<User> users) {
        this.facade = facade;
        this.users = users;
        this.scanner = new Scanner(System.in);
    }

    void run() {
        System.out.println("Welcome to the IWFC Fitness Centre system.");
        boolean running = true;
        while (running) {
            User actor = login();
            if (actor == null) {
                running = false;
                continue;
            }
            runSessionFor(actor);
        }
        System.out.println("Goodbye.");
    }

    private User login() {
        System.out.println();
        System.out.println("== Login ==");
        for (int i = 0; i < users.size(); i++) {
            User user = users.get(i);
            System.out.println("  " + (i + 1) + ". " + user.getFullName() + " (" + user.getRole() + ")");
        }
        System.out.println("  0. Exit application");
        int choice;
        try {
            choice = readInt("Choose a user: ");
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid choice.");
            return login();
        }
        if (choice == 0) {
            return null;
        }
        if (choice < 1 || choice > users.size()) {
            System.out.println("Invalid choice.");
            return login();
        }
        return users.get(choice - 1);
    }

    private void runSessionFor(User actor) {
        boolean loggedIn = true;
        System.out.println();
        System.out.println(actor.getDashboardSummary());
        while (loggedIn) {
            switch (actor.getRole()) {
                case ADMINISTRATOR:
                    loggedIn = administratorMenu(actor);
                    break;
                case INSTRUCTOR:
                    loggedIn = instructorMenu(actor);
                    break;
                case MEMBER:
                    loggedIn = memberMenu(actor);
                    break;
                default:
                    loggedIn = false;
            }
        }
    }

    private boolean administratorMenu(User actor) {
        System.out.println();
        System.out.println("== Administrator menu (" + actor.getFullName() + ") ==");
        System.out.println("  1. Register new equipment");
        System.out.println("  2. Remove equipment");
        System.out.println("  3. Deactivate equipment");
        System.out.println("  4. Edit equipment (status/location)");
        System.out.println("  5. View equipment needing maintenance");
        System.out.println("  6. View all maintenance requests");
        System.out.println("  7. Assign a maintenance request");
        System.out.println("  8. Complete a maintenance request");
        System.out.println("  9. Cancel any booking");
        System.out.println("  10. View all sessions");
        System.out.println("  11. Register new user account");
        System.out.println("  12. View all user accounts");
        System.out.println("  0. Logout");
        try {
            int choice = readInt("Choose an option: ");
            switch (choice) {
                case 1:
                    registerEquipment(actor);
                    break;
                case 2:
                    removeEquipment(actor);
                    break;
                case 3:
                    deactivateEquipment(actor);
                    break;
                case 4:
                    editEquipment(actor);
                    break;
                case 5:
                    viewEquipmentNeedingMaintenance(actor);
                    break;
                case 6:
                    viewMaintenanceRequests(actor);
                    break;
                case 7:
                    assignMaintenanceRequest(actor);
                    break;
                case 8:
                    completeMaintenanceRequest(actor);
                    break;
                case 9:
                    cancelBooking(actor);
                    break;
                case 10:
                    viewSessions();
                    break;
                case 11:
                    registerUser(actor);
                    break;
                case 12:
                    viewUsers(actor);
                    break;
                case 0:
                    return false;
                default:
                    System.out.println("Invalid choice.");
            }
        } catch (IWFCException | IllegalArgumentException | IllegalStateException e) {
            System.out.println("Action failed: " + e.getMessage());
        }
        return true;
    }

    private boolean instructorMenu(User actor) {
        System.out.println();
        System.out.println("== Instructor menu (" + actor.getFullName() + ") ==");
        System.out.println("  1. Schedule a new session");
        System.out.println("  2. Log equipment usage hours");
        System.out.println("  3. Report a maintenance issue");
        System.out.println("  4. View equipment needing maintenance");
        System.out.println("  5. View all sessions");
        System.out.println("  0. Logout");
        try {
            int choice = readInt("Choose an option: ");
            switch (choice) {
                case 1:
                    scheduleSession((Instructor) actor);
                    break;
                case 2:
                    logEquipmentUsage(actor);
                    break;
                case 3:
                    reportMaintenanceIssue(actor);
                    break;
                case 4:
                    viewEquipmentNeedingMaintenance(actor);
                    break;
                case 5:
                    viewSessions();
                    break;
                case 0:
                    return false;
                default:
                    System.out.println("Invalid choice.");
            }
        } catch (IWFCException | IllegalArgumentException | IllegalStateException e) {
            System.out.println("Action failed: " + e.getMessage());
        }
        return true;
    }

    private boolean memberMenu(User actor) {
        System.out.println();
        System.out.println("== Member menu (" + actor.getFullName() + ") ==");
        System.out.println("  1. View all sessions");
        System.out.println("  2. Book a session");
        System.out.println("  3. Cancel my booking");
        System.out.println("  0. Logout");
        try {
            int choice = readInt("Choose an option: ");
            switch (choice) {
                case 1:
                    viewSessions();
                    break;
                case 2:
                    bookSession(actor);
                    break;
                case 3:
                    cancelBooking(actor);
                    break;
                case 0:
                    return false;
                default:
                    System.out.println("Invalid choice.");
            }
        } catch (IWFCException | IllegalArgumentException | IllegalStateException e) {
            System.out.println("Action failed: " + e.getMessage());
        }
        return true;
    }

    private void registerUser(User actor) throws IWFCException {
        Role role = chooseEnum(Role.class, "role");
        String userId = readLine("User id: ");
        String fullName = readLine("Full name: ");
        String email = readLine("Email: ");
        User newUser = createUser(role, userId, fullName, email);
        facade.registerUser(actor, newUser);
        users.add(newUser);
        System.out.println("Registered: " + newUser);
    }

    private void viewUsers(User actor) throws IWFCException {
        printList("User accounts", facade.listUsers(actor));
    }

    // Didn't bother with a factory class here like we did for equipment - Role only has
    // 3 values and they map 1:1 onto the 3 User subclasses, so a plain switch is enough.
    private User createUser(Role role, String userId, String fullName, String email) {
        switch (role) {
            case ADMINISTRATOR:
                return new Administrator(userId, fullName, email);
            case INSTRUCTOR:
                return new Instructor(userId, fullName, email);
            default:
                return new Member(userId, fullName, email);
        }
    }

    private void registerEquipment(User actor) throws IWFCException {
        EquipmentType type = chooseEnum(EquipmentType.class, "equipment type");
        String equipmentId = readLine("Equipment id: ");
        Zone zone = chooseEnum(Zone.class, "zone");
        Equipment equipment = EquipmentFactory.createEquipment(type, equipmentId, zone);
        facade.registerEquipment(actor, equipment);
        System.out.println("Registered: " + equipment);
    }

    private void removeEquipment(User actor) throws IWFCException {
        String equipmentId = readLine("Equipment id to remove: ");
        facade.removeEquipment(actor, equipmentId);
        System.out.println("Removed equipment " + equipmentId);
    }

    private void deactivateEquipment(User actor) throws IWFCException {
        String equipmentId = readLine("Equipment id to deactivate: ");
        facade.deactivateEquipment(actor, equipmentId);
        System.out.println("Deactivated equipment " + equipmentId);
    }

    private void editEquipment(User actor) throws IWFCException {
        String equipmentId = readLine("Equipment id to edit: ");
        System.out.println("Edit: 1. Status  2. Location");
        int choice = readInt("Choice: ");
        switch (choice) {
            case 1:
                EquipmentStatus status = chooseEnum(EquipmentStatus.class, "status");
                facade.updateEquipmentStatus(actor, equipmentId, status);
                System.out.println("Updated status for " + equipmentId);
                break;
            case 2:
                Zone zone = chooseEnum(Zone.class, "zone");
                facade.updateEquipmentLocation(actor, equipmentId, zone);
                System.out.println("Updated location for " + equipmentId);
                break;
            default:
                System.out.println("Invalid choice.");
        }
    }

    private void viewEquipmentNeedingMaintenance(User actor) throws IWFCException {
        List<Equipment> equipment = facade.getEquipmentNeedingMaintenance(actor);
        printList("Equipment needing maintenance", equipment);
    }

    private void viewMaintenanceRequests(User actor) throws IWFCException {
        List<MaintenanceRequest> requests = facade.listMaintenanceRequests(actor);
        printList("Maintenance requests", requests);
    }

    private void assignMaintenanceRequest(User actor) throws IWFCException {
        String requestId = readLine("Request id: ");
        facade.assignMaintenanceRequest(actor, requestId, (Administrator) actor, LocalDateTime.now());
        System.out.println("Assigned request " + requestId);
    }

    private void completeMaintenanceRequest(User actor) throws IWFCException {
        String requestId = readLine("Request id: ");
        facade.completeMaintenanceRequest(actor, requestId, LocalDateTime.now());
        System.out.println("Completed request " + requestId);
    }

    private void scheduleSession(Instructor instructor) throws IWFCException {
        SessionType type = chooseEnum(SessionType.class, "session type");
        String sessionId = readLine("Session id: ");
        Zone zone = chooseEnum(Zone.class, "zone");
        LocalDateTime startTime = readDateTime("Start time (yyyy-MM-dd HH:mm): ");
        int durationMinutes = readInt("Duration (minutes): ");
        Session session = createSession(type, sessionId, instructor, zone, startTime, durationMinutes);
        facade.scheduleSession(instructor, session);
        System.out.println("Scheduled: " + session);
    }

    private void logEquipmentUsage(User actor) throws IWFCException {
        String equipmentId = readLine("Equipment id: ");
        double hours = readDouble("Hours to log: ");
        boolean alertTriggered = facade.logEquipmentUsage(actor, equipmentId, hours);
        System.out.println(alertTriggered ? "Logged. Maintenance threshold reached!" : "Logged.");
    }

    private void reportMaintenanceIssue(User actor) throws IWFCException {
        String equipmentId = readLine("Equipment id: ");
        Equipment equipment = facade.findEquipment(equipmentId)
                .orElseThrow(() -> new IllegalArgumentException("No equipment found with id: " + equipmentId));
        Urgency urgency = chooseEnum(Urgency.class, "urgency");
        String description = readLine("Description: ");
        MaintenanceRequest request = new MaintenanceRequest(
                shortId("MR"), equipment, actor, urgency, description, LocalDateTime.now());
        facade.reportMaintenanceIssue(actor, request);
        System.out.println("Reported: " + request);
    }

    private void cancelBooking(User actor) throws IWFCException {
        String bookingId = readLine("Booking id: ");
        facade.cancelBooking(actor, bookingId);
        System.out.println("Cancelled booking " + bookingId);
    }

    private void viewSessions() {
        printList("Sessions", facade.listSessions());
    }

    private void bookSession(User actor) throws IWFCException {
        String sessionId = readLine("Session id: ");
        Session session = facade.listSessions().stream()
                .filter(s -> s.getSessionId().equals(sessionId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No session found with id: " + sessionId));
        int recurrenceWeeks = readInt("Recurring weekly for how many weeks (0 = one-off): ");
        Booking booking = new Booking(shortId("BK"), (Member) actor, session,
                LocalDateTime.now(), recurrenceWeeks);
        facade.bookSession(actor, booking);
        System.out.println("Booked: " + booking);
    }

    private Session createSession(SessionType type, String sessionId, Instructor instructor, Zone zone,
                                    LocalDateTime startTime, int durationMinutes) {
        switch (type) {
            case HIIT:
                return new HiitSession(sessionId, instructor, zone, startTime, durationMinutes);
            case YOGA:
                return new YogaSession(sessionId, instructor, zone, startTime, durationMinutes);
            case PILATES:
                return new PilatesSession(sessionId, instructor, zone, startTime, durationMinutes);
            default:
                return new GenericSession(sessionId, type, instructor, zone, startTime, durationMinutes,
                        (int) DEFAULT_GENERIC_SESSION_CAPACITY);
        }
    }

    private <T extends Enum<T>> T chooseEnum(Class<T> enumType, String label) {
        T[] values = enumType.getEnumConstants();
        System.out.println("Select " + label + ":");
        for (int i = 0; i < values.length; i++) {
            System.out.println("  " + (i + 1) + ". " + values[i]);
        }
        int choice = readInt("Choice: ");
        if (choice < 1 || choice > values.length) {
            throw new IllegalArgumentException("Invalid " + label + " choice");
        }
        return values[choice - 1];
    }

    private void printList(String title, List<?> items) {
        System.out.println(title + ":");
        if (items.isEmpty()) {
            System.out.println("  (none)");
            return;
        }
        for (Object item : items) {
            System.out.println("  - " + item);
        }
    }

    private String readLine(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    private int readInt(String prompt) {
        try {
            return Integer.parseInt(readLine(prompt));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Expected a whole number");
        }
    }

    private double readDouble(String prompt) {
        try {
            return Double.parseDouble(readLine(prompt));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Expected a number");
        }
    }

    private LocalDateTime readDateTime(String prompt) {
        try {
            return LocalDateTime.parse(readLine(prompt), DATE_TIME_FORMAT);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Expected a date/time like 2026-09-21 09:00");
        }
    }

    private static String shortId(String prefix) {
        return prefix + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
