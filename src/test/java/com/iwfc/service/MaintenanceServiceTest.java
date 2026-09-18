package com.iwfc.service;

import com.iwfc.exceptions.DuplicateDataException;
import com.iwfc.model.MaintenanceRequest;
import com.iwfc.model.RequestStatus;
import com.iwfc.model.Urgency;
import com.iwfc.model.Zone;
import com.iwfc.model.equipment.Treadmill;
import com.iwfc.notification.NotificationService;
import com.iwfc.repository.Repository;
import com.iwfc.users.Administrator;
import com.iwfc.users.Member;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MaintenanceServiceTest {

    private MaintenanceService maintenanceService;
    private List<String> capturedMessages;
    private Treadmill treadmill;
    private Member reporter;
    private Administrator admin;

    @BeforeEach
    void setUp() {
        Repository<MaintenanceRequest> repository = new Repository<>(MaintenanceRequest::getRequestId);
        NotificationService notificationService = new NotificationService();
        capturedMessages = new ArrayList<>();
        notificationService.subscribe(capturedMessages::add);
        maintenanceService = new MaintenanceService(repository, notificationService);
        treadmill = new Treadmill("EQ-1", Zone.CARDIO_ZONE);
        reporter = new Member("ME-1", "Sam Member", "sam@iwfc.com");
        admin = new Administrator("AD-1", "Ada Admin", "ada@iwfc.com");
    }

    @Test
    void reportIssueStoresRequestAndNotifiesObservers() throws DuplicateDataException {
        MaintenanceRequest request = new MaintenanceRequest("MR-1", treadmill, reporter, Urgency.HIGH,
                "Belt slipping", LocalDateTime.now());

        maintenanceService.reportIssue(request);

        assertTrue(maintenanceService.findById("MR-1").isPresent());
        assertEquals(1, capturedMessages.size());
        assertTrue(capturedMessages.get(0).contains("MR-1"));
    }

    @Test
    void reportIssueRejectsDuplicateId() throws DuplicateDataException {
        maintenanceService.reportIssue(new MaintenanceRequest("MR-1", treadmill, reporter, Urgency.LOW,
                "Loose bolt", LocalDateTime.now()));

        assertThrows(DuplicateDataException.class, () -> maintenanceService.reportIssue(
                new MaintenanceRequest("MR-1", treadmill, reporter, Urgency.LOW, "Loose bolt", LocalDateTime.now())));
    }

    @Test
    void assignRequestTransitionsToAssignedAndNotifies() throws DuplicateDataException {
        maintenanceService.reportIssue(new MaintenanceRequest("MR-1", treadmill, reporter, Urgency.MEDIUM,
                "Display flickering", LocalDateTime.now()));

        maintenanceService.assignRequest("MR-1", admin, LocalDateTime.now());

        assertEquals(RequestStatus.ASSIGNED, maintenanceService.findById("MR-1").get().getStatus());
        assertEquals(2, capturedMessages.size());
    }

    @Test
    void completeRequestTransitionsToCompletedAndNotifies() throws DuplicateDataException {
        maintenanceService.reportIssue(new MaintenanceRequest("MR-1", treadmill, reporter, Urgency.LOW,
                "Squeaky wheel", LocalDateTime.now()));
        maintenanceService.assignRequest("MR-1", admin, LocalDateTime.now());

        maintenanceService.completeRequest("MR-1", LocalDateTime.now());

        assertEquals(RequestStatus.COMPLETED, maintenanceService.findById("MR-1").get().getStatus());
        assertEquals(3, capturedMessages.size());
    }

    @Test
    void assigningNonPendingRequestThrows() throws DuplicateDataException {
        maintenanceService.reportIssue(new MaintenanceRequest("MR-1", treadmill, reporter, Urgency.LOW,
                "Squeaky wheel", LocalDateTime.now()));
        maintenanceService.assignRequest("MR-1", admin, LocalDateTime.now());

        assertThrows(IllegalStateException.class,
                () -> maintenanceService.assignRequest("MR-1", admin, LocalDateTime.now()));
    }

    @Test
    void lookingUpUnknownRequestForAssignmentThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> maintenanceService.assignRequest("missing", admin, LocalDateTime.now()));
    }
}
