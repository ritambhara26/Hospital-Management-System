package hospital.management.system.controller;

import hospital.management.system.entity.Medication;
import hospital.management.system.service.MedicationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/medications")
@RequiredArgsConstructor
@Slf4j
public class MedicationController {

    private final MedicationService service;

    @PostMapping
    public ResponseEntity<Medication> create(@RequestBody Medication medication) {
        log.info("POST request to create medication: {}", medication.getName());
        try {
            Medication createdMedication = service.save(medication);
            log.info("Medication created successfully with ID: {}", createdMedication.getMedicationId());
            return ResponseEntity.ok(createdMedication);
        } catch (Exception e) {
            log.error("Error creating medication: {}", medication.getName(), e);
            throw e;
        }
    }

    @GetMapping
    public List<Medication> getAll() {
        log.debug("GET request to retrieve all medications");
        try {
            List<Medication> medications = service.getAll();
            log.info("Retrieved {} medications", medications.size());
            return medications;
        } catch (Exception e) {
            log.error("Error retrieving all medications", e);
            throw e;
        }
    }

    @GetMapping("/{id}")
    public Medication getById(@PathVariable Long id) {
        log.debug("GET request to retrieve medication with ID: {}", id);
        try {
            Medication medication = service.getById(id);
            log.info("Medication retrieved successfully with ID: {}", id);
            return medication;
        } catch (Exception e) {
            log.error("Error retrieving medication with ID: {}", id, e);
            throw e;
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Medication> update(@PathVariable Long id, @RequestBody Medication medication) {
        log.info("PUT request to update medication with ID: {}", id);
        try {
            Medication updatedMedication = service.update(id, medication);
            log.info("Medication updated successfully with ID: {}", id);
            return ResponseEntity.ok(updatedMedication);
        } catch (Exception e) {
            log.error("Error updating medication with ID: {}", id, e);
            throw e;
        }
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        log.info("DELETE request to delete medication with ID: {}", id);
        try {
            service.delete(id);
            log.info("Medication deleted successfully with ID: {}", id);
        } catch (Exception e) {
            log.error("Error deleting medication with ID: {}", id, e);
            throw e;
        }
    }
}