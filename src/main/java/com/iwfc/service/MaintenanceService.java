package com.iwfc.service;

import com.iwfc.exceptions.DuplicateDataException;
import com.iwfc.model.MaintenanceRequest;
import com.iwfc.notification.NotificationService;
import com.iwfc.repository.Repository;
import com.iwfc.users.Administrator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class MaintenanceService {

    private final Repository<MaintenanceRequest> maintenanceRequestRepository;
    private final NotificationService notificationService;

    public MaintenanceService(Repository<MaintenanceRequest> maintenanceRequestRepository,
                               NotificationService notificationService) {
        this.maintenanceRequestRepository =
                Objects.requireNonNull(maintenanceRequestRepository, "maintenanceRequestRepository must not be null");
        this.notificationService = Objects.requireNonNull(notificationService, "notificationService must not be null");
    }

    public void reportIssue(MaintenanceRequest request) throws DuplicateDataException {
        maintenanceRequestRepository.add(request);
        notificationService.notifyObservers("New " + request.getUrgency() + " maintenance request "
                + request.getRequestId() + " reported for " + request.getEquipment().getEquipmentId());
    }

    public void assignRequest(String requestId, Administrator admin, LocalDateTime assignedAt) {
        MaintenanceRequest request = getRequiredRequest(requestId);
        request.assignTo(admin, assignedAt);
        notificationService.notifyObservers("Maintenance request " + requestId + " assigned to "
                + admin.getFullName());
    }

    public void completeRequest(String requestId, LocalDateTime completedAt) {
        MaintenanceRequest request = getRequiredRequest(requestId);
        request.complete(completedAt);
        notificationService.notifyObservers("Maintenance request " + requestId + " completed");
    }

    public Optional<MaintenanceRequest> findById(String requestId) {
        return maintenanceRequestRepository.findById(requestId);
    }

    public List<MaintenanceRequest> listAll() {
        return maintenanceRequestRepository.findAll();
    }

    private MaintenanceRequest getRequiredRequest(String requestId) {
        return maintenanceRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("No maintenance request found with id: " + requestId));
    }
}
