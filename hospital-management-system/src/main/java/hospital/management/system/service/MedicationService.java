package hospital.management.system.service;


import hospital.management.system.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import hospital.management.system.entity.*;
import hospital.management.system.repository.*;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MedicationService {

    private final MedicationRepository repository;

    public Medication save(Medication medication) {
        log.info("Saving medication: {}", medication.getName());
        try {
            Medication savedMedication = repository.save(medication);
            log.info("Medication saved successfully with ID: {} and name: {}", savedMedication.getMedicationId(), savedMedication.getName());
            return savedMedication;
        } catch (Exception e) {
            log.error("Error saving medication: {}", medication.getName(), e);
            throw e;
        }
    }

    public List<Medication> getAll() {
        log.debug("Retrieving all medications");
        try {
            List<Medication> medications = repository.findAll();
            log.info("Retrieved {} medications", medications.size());
            return medications;
        } catch (Exception e) {
            log.error("Error retrieving all medications", e);
            throw e;
        }
    }

    public Medication getById(Long id) {
        log.debug("Retrieving medication with ID: {}", id);
        try {
            Medication medication = repository.findById(id)
                    .orElseThrow(() -> {
                        log.warn("Medication not found with ID: {}", id);
                        return new ResourceNotFoundException("Medication not found");
                    });
            log.info("Medication retrieved successfully with ID: {}", id);
            return medication;
        } catch (ResourceNotFoundException e) {
            log.error("Medication not found with ID: {}", id);
            throw e;
        } catch (Exception e) {
            log.error("Error retrieving medication with ID: {}", id, e);
            throw e;
        }
    }

    public Medication update(Long id, Medication medication) {
        log.info("Updating medication with ID: {}", id);
        try {
            Medication existing = repository.findById(id)
                    .orElseThrow(() -> {
                        log.warn("Medication not found for update with ID: {}", id);
                        return new ResourceNotFoundException("Medication not found");
                    });

            existing.setName(medication.getName());
            existing.setPrice(medication.getPrice());

            Medication updatedMedication = repository.save(existing);
            log.info("Medication updated successfully with ID: {} to name: {}", id, medication.getName());
            return updatedMedication;
        } catch (ResourceNotFoundException e) {
            log.error("Medication not found for update with ID: {}", id);
            throw e;
        } catch (Exception e) {
            log.error("Error updating medication with ID: {}", id, e);
            throw e;
        }
    }

    public void delete(Long id) {
        log.info("Deleting medication with ID: {}", id);
        try {
            repository.deleteById(id);
            log.info("Medication deleted successfully with ID: {}", id);
        } catch (Exception e) {
            log.error("Error deleting medication with ID: {}", id, e);
            throw e;
        }
    }
}