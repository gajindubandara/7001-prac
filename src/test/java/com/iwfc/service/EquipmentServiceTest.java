package com.iwfc.service;

import com.iwfc.exceptions.DuplicateDataException;
import com.iwfc.model.Equipment;
import com.iwfc.model.EquipmentStatus;
import com.iwfc.model.Zone;
import com.iwfc.model.equipment.SpinBike;
import com.iwfc.model.equipment.Treadmill;
import com.iwfc.repository.Repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EquipmentServiceTest {

    private EquipmentService equipmentService;

    @BeforeEach
    void setUp() {
        Repository<Equipment> repository = new Repository<>(Equipment::getEquipmentId);
        equipmentService = new EquipmentService(repository);
    }

    @Test
    void registerAndFindEquipment() throws DuplicateDataException {
        Treadmill treadmill = new Treadmill("EQ-1", Zone.CARDIO_ZONE);
        equipmentService.registerEquipment(treadmill);

        assertTrue(equipmentService.findById("EQ-1").isPresent());
        assertEquals(treadmill, equipmentService.findById("EQ-1").get());
    }

    @Test
    void registerEquipmentRejectsDuplicateId() throws DuplicateDataException {
        equipmentService.registerEquipment(new Treadmill("EQ-1", Zone.CARDIO_ZONE));

        assertThrows(DuplicateDataException.class,
                () -> equipmentService.registerEquipment(new Treadmill("EQ-1", Zone.CARDIO_ZONE)));
    }

    @Test
    void logUsageHoursBelowThresholdDoesNotAlert() throws DuplicateDataException {
        equipmentService.registerEquipment(new Treadmill("EQ-1", Zone.CARDIO_ZONE)); // threshold 150h

        boolean alert = equipmentService.logUsageHours("EQ-1", 100);

        assertFalse(alert);
    }

    @Test
    void logUsageHoursCrossingThresholdAlerts() throws DuplicateDataException {
        equipmentService.registerEquipment(new Treadmill("EQ-1", Zone.CARDIO_ZONE)); // threshold 150h

        boolean alert = equipmentService.logUsageHours("EQ-1", 200);

        assertTrue(alert);
    }

    @Test
    void logUsageHoursForUnknownEquipmentThrows() {
        assertThrows(IllegalArgumentException.class, () -> equipmentService.logUsageHours("missing", 10));
    }

    @Test
    void getEquipmentNeedingMaintenanceExcludesInactiveEquipment() throws DuplicateDataException {
        Treadmill deactivated = new Treadmill("EQ-1", Zone.CARDIO_ZONE);
        equipmentService.registerEquipment(deactivated);
        equipmentService.logUsageHours("EQ-1", 200);
        equipmentService.deactivateEquipment("EQ-1");

        SpinBike stillActive = new SpinBike("EQ-2", Zone.SPIN_STUDIO);
        equipmentService.registerEquipment(stillActive);
        equipmentService.logUsageHours("EQ-2", 250);

        List<Equipment> needingMaintenance = equipmentService.getEquipmentNeedingMaintenance();

        assertEquals(1, needingMaintenance.size());
        assertEquals("EQ-2", needingMaintenance.get(0).getEquipmentId());
    }

    @Test
    void deactivateEquipmentMarksItInactive() throws DuplicateDataException {
        Treadmill treadmill = new Treadmill("EQ-1", Zone.CARDIO_ZONE);
        equipmentService.registerEquipment(treadmill);

        equipmentService.deactivateEquipment("EQ-1");

        assertFalse(equipmentService.findById("EQ-1").get().isActive());
    }

    @Test
    void deactivateUnknownEquipmentThrows() {
        assertThrows(IllegalArgumentException.class, () -> equipmentService.deactivateEquipment("missing"));
    }

    @Test
    void updateEquipmentStatusChangesStatus() throws DuplicateDataException {
        Treadmill treadmill = new Treadmill("EQ-1", Zone.CARDIO_ZONE);
        equipmentService.registerEquipment(treadmill);

        equipmentService.updateEquipmentStatus("EQ-1", EquipmentStatus.FAULTY);

        assertEquals(EquipmentStatus.FAULTY, equipmentService.findById("EQ-1").get().getStatus());
    }

    @Test
    void updateEquipmentLocationChangesZone() throws DuplicateDataException {
        Treadmill treadmill = new Treadmill("EQ-1", Zone.CARDIO_ZONE);
        equipmentService.registerEquipment(treadmill);

        equipmentService.updateEquipmentLocation("EQ-1", Zone.STUDIO_A);

        assertEquals(Zone.STUDIO_A, equipmentService.findById("EQ-1").get().getLocation());
    }
}
