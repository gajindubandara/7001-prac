package com.iwfc.model.equipment;

import com.iwfc.model.Equipment;
import com.iwfc.model.EquipmentType;
import com.iwfc.model.Zone;

public class SpinBike extends Equipment {

    private static final double MAINTENANCE_THRESHOLD_HOURS = 200.0;

    public SpinBike(String equipmentId, Zone location) {
        super(equipmentId, EquipmentType.SPIN_BIKE, location);
    }

    @Override
    public double getMaintenanceThresholdHours() {
        return MAINTENANCE_THRESHOLD_HOURS;
    }
}
