package com.iwfc.model.equipment;

import com.iwfc.model.Equipment;
import com.iwfc.model.EquipmentType;
import com.iwfc.model.Zone;

public class GenericEquipment extends Equipment {

    private final double maintenanceThresholdHours;

    public GenericEquipment(String equipmentId, EquipmentType type, Zone location, double maintenanceThresholdHours) {
        super(equipmentId, type, location);
        if (maintenanceThresholdHours <= 0) {
            throw new IllegalArgumentException("maintenanceThresholdHours must be positive");
        }
        this.maintenanceThresholdHours = maintenanceThresholdHours;
    }

    @Override
    public double getMaintenanceThresholdHours() {
        return maintenanceThresholdHours;
    }
}
