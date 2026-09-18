package com.iwfc.repository;

import com.iwfc.model.Booking;
import com.iwfc.model.Equipment;
import com.iwfc.model.MaintenanceRequest;
import com.iwfc.model.Session;
import com.iwfc.users.User;

// Singleton pattern - one instance for the whole app, so every service reads and writes
// the same repositories instead of each ending up with its own private copy of the data.
public final class DataStore {

    private static final DataStore INSTANCE = new DataStore();

    private final Repository<Equipment> equipmentRepository;
    private final Repository<User> userRepository;
    private final Repository<Session> sessionRepository;
    private final Repository<Booking> bookingRepository;
    private final Repository<MaintenanceRequest> maintenanceRequestRepository;

    private DataStore() {
        this.equipmentRepository = new Repository<>(Equipment::getEquipmentId);
        this.userRepository = new Repository<>(User::getUserId);
        this.sessionRepository = new Repository<>(Session::getSessionId);
        this.bookingRepository = new Repository<>(Booking::getBookingId);
        this.maintenanceRequestRepository = new Repository<>(MaintenanceRequest::getRequestId);
    }

    public static DataStore getInstance() {
        return INSTANCE;
    }

    public Repository<Equipment> getEquipmentRepository() {
        return equipmentRepository;
    }

    public Repository<User> getUserRepository() {
        return userRepository;
    }

    public Repository<Session> getSessionRepository() {
        return sessionRepository;
    }

    public Repository<Booking> getBookingRepository() {
        return bookingRepository;
    }

    public Repository<MaintenanceRequest> getMaintenanceRequestRepository() {
        return maintenanceRequestRepository;
    }
}
