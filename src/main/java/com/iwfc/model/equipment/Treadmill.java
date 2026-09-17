package com.iwfc.model.equipment;

import com.iwfc.model.Equipment;
import com.iwfc.model.EquipmentType;
import com.iwfc.model.Zone;

public class Treadmill extends Equipment {

    private static final double MAINTENANCE_THRESHOLD_HOURS = 150.0;

    public Treadmill(String equipmentId, Zone location) {
        super(equipmentId, EquipmentType.TREADMILL, location);
    }

    @Override
    public double getMaintenanceThresholdHours() {
        return MAINTENANCE_THRESHOLD_HOURS;
    }
}
