package com.iwfc.factory;

import com.iwfc.model.Equipment;
import com.iwfc.model.EquipmentType;
import com.iwfc.model.Zone;
import com.iwfc.model.equipment.GenericEquipment;
import com.iwfc.model.equipment.RowingMachine;
import com.iwfc.model.equipment.SpinBike;
import com.iwfc.model.equipment.Treadmill;

public final class EquipmentFactory {

    private static final double DEFAULT_GENERIC_MAINTENANCE_THRESHOLD_HOURS = 200.0;

    private EquipmentFactory() {
    }

    public static Equipment createEquipment(EquipmentType type, String equipmentId, Zone location) {
        switch (type) {
            case TREADMILL:
                return new Treadmill(equipmentId, location);
            case SPIN_BIKE:
                return new SpinBike(equipmentId, location);
            case ROWING_MACHINE:
                return new RowingMachine(equipmentId, location);
            default:
                return new GenericEquipment(equipmentId, type, location, DEFAULT_GENERIC_MAINTENANCE_THRESHOLD_HOURS);
        }
    }
}
