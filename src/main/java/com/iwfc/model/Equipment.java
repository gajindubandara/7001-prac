package com.iwfc.model;

import java.util.Objects;

public abstract class Equipment {

    private final String equipmentId;
    private final EquipmentType type;
    private Zone location;
    private EquipmentStatus status;
    private double usageHours;
    private boolean active;

    protected Equipment(String equipmentId, EquipmentType type, Zone location) {
        this.equipmentId = Objects.requireNonNull(equipmentId, "equipmentId must not be null");
        this.type = Objects.requireNonNull(type, "type must not be null");
        this.location = Objects.requireNonNull(location, "location must not be null");
        this.status = EquipmentStatus.OPERATIONAL;
        this.usageHours = 0.0;
        this.active = true;
    }

    public String getEquipmentId() {
        return equipmentId;
    }

    public EquipmentType getType() {
        return type;
    }

    public Zone getLocation() {
        return location;
    }

    public EquipmentStatus getStatus() {
        return status;
    }

    public double getUsageHours() {
        return usageHours;
    }

    public boolean isActive() {
        return active;
    }

    public abstract double getMaintenanceThresholdHours();

    public void updateLocation(Zone newLocation) {
        this.location = Objects.requireNonNull(newLocation, "newLocation must not be null");
    }

    public void updateStatus(EquipmentStatus newStatus) {
        this.status = Objects.requireNonNull(newStatus, "newStatus must not be null");
    }

    public void logUsageHours(double hours) {
        if (hours <= 0) {
            throw new IllegalArgumentException("hours must be positive");
        }
        this.usageHours += hours;
    }

    public boolean needsMaintenanceAlert() {
        return usageHours >= getMaintenanceThresholdHours();
    }

    public void deactivate() {
        this.active = false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Equipment)) {
            return false;
        }
        Equipment that = (Equipment) o;
        return equipmentId.equals(that.equipmentId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(equipmentId);
    }

    @Override
    public String toString() {
        return type + " " + equipmentId + " [" + status + "] @ " + location;
    }
}
