package com.iwfc.model;

import com.iwfc.users.Administrator;
import com.iwfc.users.User;

import java.time.LocalDateTime;
import java.util.Objects;

public class MaintenanceRequest {

    private final String requestId;
    private final Equipment equipment;
    private final User reportedBy;
    private final Urgency urgency;
    private final String description;
    private final LocalDateTime reportedAt;
    private RequestStatus status;
    private Administrator assignedTo;
    private LocalDateTime assignedAt;
    private LocalDateTime completedAt;

    public MaintenanceRequest(String requestId, Equipment equipment, User reportedBy, Urgency urgency,
                               String description, LocalDateTime reportedAt) {
        this.requestId = Objects.requireNonNull(requestId, "requestId must not be null");
        this.equipment = Objects.requireNonNull(equipment, "equipment must not be null");
        this.reportedBy = Objects.requireNonNull(reportedBy, "reportedBy must not be null");
        this.urgency = Objects.requireNonNull(urgency, "urgency must not be null");
        this.description = Objects.requireNonNull(description, "description must not be null");
        this.reportedAt = Objects.requireNonNull(reportedAt, "reportedAt must not be null");
        this.status = RequestStatus.PENDING;
    }

    public String getRequestId() {
        return requestId;
    }

    public Equipment getEquipment() {
        return equipment;
    }

    public User getReportedBy() {
        return reportedBy;
    }

    public Urgency getUrgency() {
        return urgency;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getReportedAt() {
        return reportedAt;
    }

    public RequestStatus getStatus() {
        return status;
    }

    public Administrator getAssignedTo() {
        return assignedTo;
    }

    public LocalDateTime getAssignedAt() {
        return assignedAt;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void assignTo(Administrator admin, LocalDateTime assignedAt) {
        Objects.requireNonNull(admin, "admin must not be null");
        Objects.requireNonNull(assignedAt, "assignedAt must not be null");
        if (status != RequestStatus.PENDING) {
            throw new IllegalStateException("Request " + requestId + " is not pending (status: " + status + ")");
        }
        this.assignedTo = admin;
        this.assignedAt = assignedAt;
        this.status = RequestStatus.ASSIGNED;
    }

    public void complete(LocalDateTime completedAt) {
        Objects.requireNonNull(completedAt, "completedAt must not be null");
        if (status != RequestStatus.ASSIGNED) {
            throw new IllegalStateException("Request " + requestId + " is not assigned (status: " + status + ")");
        }
        this.completedAt = completedAt;
        this.status = RequestStatus.COMPLETED;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MaintenanceRequest)) {
            return false;
        }
        MaintenanceRequest that = (MaintenanceRequest) o;
        return requestId.equals(that.requestId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(requestId);
    }

    @Override
    public String toString() {
        return "MaintenanceRequest " + requestId + " [" + urgency + ", " + status + "] for "
                + equipment.getEquipmentId();
    }
}
