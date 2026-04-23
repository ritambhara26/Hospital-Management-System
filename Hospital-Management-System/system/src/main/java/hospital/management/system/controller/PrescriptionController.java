package hospital.management.system.controller;

import hospital.management.system.entity.Prescription;
import hospital.management.system.service.PrescriptionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/prescriptions")
@RequiredArgsConstructor
@Slf4j
public class PrescriptionController {

    private final PrescriptionService service;

    @PostMapping
    public Prescription create(@RequestBody Prescription prescription) {
        log.info("POST request to create prescription");
        try {
            Prescription createdPrescription = service.create(prescription);
            log.info("Prescription created successfully with ID: {}", createdPrescription.getPrescriptionId());
            return createdPrescription;
        } catch (Exception e) {
            log.error("Error creating prescription", e);
            throw e;
        }
    }

    @GetMapping
    public List<Prescription> getAll() {
        log.debug("GET request to retrieve all prescriptions");
        try {
            List<Prescription> prescriptions = service.getAll();
            log.info("Retrieved {} prescriptions", prescriptions.size());
            return prescriptions;
        } catch (Exception e) {
            log.error("Error retrieving all prescriptions", e);
            throw e;
        }
    }

    @GetMapping("/{id}")
    public Prescription getById(@PathVariable Long id) {
        log.debug("GET request to retrieve prescription with ID: {}", id);
        try {
            Prescription prescription = service.getById(id);
            log.info("Prescription retrieved successfully with ID: {}", id);
            return prescription;
        } catch (Exception e) {
            log.error("Error retrieving prescription with ID: {}", id, e);
            throw e;
        }
    }

    @PutMapping("/{id}")
    public Prescription update(@PathVariable Long id, @RequestBody Prescription prescription) {
        log.info("PUT request to update prescription with ID: {}", id);
        try {
            Prescription updatedPrescription = service.update(id, prescription);
            log.info("Prescription updated successfully with ID: {}", id);
            return updatedPrescription;
        } catch (Exception e) {
            log.error("Error updating prescription with ID: {}", id, e);
            throw e;
        }
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        log.info("DELETE request to delete prescription with ID: {}", id);
        try {
            service.delete(id);
            log.info("Prescription deleted successfully with ID: {}", id);
        } catch (Exception e) {
            log.error("Error deleting prescription with ID: {}", id, e);
            throw e;
        }
    }
}