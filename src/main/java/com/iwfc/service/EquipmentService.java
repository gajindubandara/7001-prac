package com.iwfc.service;

import com.iwfc.exceptions.DuplicateDataException;
import com.iwfc.model.Equipment;
import com.iwfc.repository.Repository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class EquipmentService {

    private final Repository<Equipment> equipmentRepository;

    public EquipmentService(Repository<Equipment> equipmentRepository) {
        this.equipmentRepository = Objects.requireNonNull(equipmentRepository, "equipmentRepository must not be null");
    }

    public void registerEquipment(Equipment equipment) throws DuplicateDataException {
        equipmentRepository.add(equipment);
    }

    public Optional<Equipment> findById(String equipmentId) {
        return equipmentRepository.findById(equipmentId);
    }

    public List<Equipment> listAll() {
        return equipmentRepository.findAll();
    }

    public boolean removeEquipment(String equipmentId) {
        return equipmentRepository.removeById(equipmentId);
    }

    public boolean logUsageHours(String equipmentId, double hours) {
        Equipment equipment = getRequiredEquipment(equipmentId);
        equipment.logUsageHours(hours);
        return equipment.needsMaintenanceAlert();
    }

    public List<Equipment> getEquipmentNeedingMaintenance() {
        return equipmentRepository.findAll().stream()
                .filter(Equipment::isActive)
                .filter(Equipment::needsMaintenanceAlert)
                .collect(Collectors.toList());
    }

    private Equipment getRequiredEquipment(String equipmentId) {
        return equipmentRepository.findById(equipmentId)
                .orElseThrow(() -> new IllegalArgumentException("No equipment found with id: " + equipmentId));
    }
}
