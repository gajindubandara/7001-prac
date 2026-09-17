package com.iwfc.model.equipment;

import com.iwfc.model.Equipment;
import com.iwfc.model.EquipmentType;
import com.iwfc.model.Zone;

public class RowingMachine extends Equipment {

    private static final double MAINTENANCE_THRESHOLD_HOURS = 250.0;

    public RowingMachine(String equipmentId, Zone location) {
        super(equipmentId, EquipmentType.ROWING_MACHINE, location);
    }

    @Override
    public double getMaintenanceThresholdHours() {
        return MAINTENANCE_THRESHOLD_HOURS;
    }
}
