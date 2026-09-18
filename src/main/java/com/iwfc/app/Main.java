package com.iwfc.app;

import com.iwfc.facade.IWFCFacade;
import com.iwfc.notification.ConsoleNotificationObserver;
import com.iwfc.notification.NotificationService;
import com.iwfc.repository.DataStore;
import com.iwfc.service.EquipmentService;
import com.iwfc.service.MaintenanceService;
import com.iwfc.service.SchedulingService;
import com.iwfc.users.User;

import java.util.List;

public final class Main {

    private Main() {
    }

    public static void main(String[] args) {
        DataStore dataStore = DataStore.getInstance();

        EquipmentService equipmentService = new EquipmentService(dataStore.getEquipmentRepository());
        SchedulingService schedulingService =
                new SchedulingService(dataStore.getSessionRepository(), dataStore.getBookingRepository());
        NotificationService notificationService = new NotificationService();
        MaintenanceService maintenanceService =
                new MaintenanceService(dataStore.getMaintenanceRequestRepository(), notificationService);
        IWFCFacade facade = new IWFCFacade(equipmentService, schedulingService, maintenanceService);

        try {
            List<User> users = SeedData.load(dataStore, facade);
            for (User user : users) {
                notificationService.subscribe(new ConsoleNotificationObserver(user));
            }
            new ConsoleMenu(facade, users).run();
        } catch (Exception e) {
            System.err.println("Fatal error during startup: " + e.getMessage());
        }
    }
}
