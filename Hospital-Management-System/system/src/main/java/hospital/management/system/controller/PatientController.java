package hospital.management.system.controller;

import hospital.management.system.entity.Patient;
import hospital.management.system.service.PatientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
@Slf4j
public class PatientController {

    private final PatientService service;

    @PostMapping
    public ResponseEntity<Patient> create(@RequestBody Patient patient) {
        log.info("POST request to create patient with email: {}", patient.getEmail());
        try {
            Patient createdPatient = service.save(patient);
            log.info("Patient created successfully with ID: {}", createdPatient.getPatientId());
            return ResponseEntity.ok(createdPatient);
        } catch (Exception e) {
            log.error("Error creating patient with email: {}", patient.getEmail(), e);
            throw e;
        }
    }

    @GetMapping
    public List<Patient> getAll() {
        log.debug("GET request to retrieve all patients");
        try {
            List<Patient> patients = service.getAll();
            log.info("Retrieved {} patients", patients.size());
            return patients;
        } catch (Exception e) {
            log.error("Error retrieving all patients", e);
            throw e;
        }
    }

    @GetMapping("/{id}")
    public Patient get(@PathVariable Long id) {
        log.debug("GET request to retrieve patient with ID: {}", id);
        try {
            Patient patient = service.getById(id);
            log.info("Patient retrieved successfully with ID: {}", id);
            return patient;
        } catch (Exception e) {
            log.error("Error retrieving patient with ID: {}", id, e);
            throw e;
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Patient> update(@PathVariable Long id, @RequestBody Patient patient) {
        log.info("PUT request to update patient with ID: {}", id);
        try {
            Patient updatedPatient = service.update(id, patient);
            log.info("Patient updated successfully with ID: {}", id);
            return ResponseEntity.ok(updatedPatient);
        } catch (Exception e) {
            log.error("Error updating patient with ID: {}", id, e);
            throw e;
        }
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        log.info("DELETE request to delete patient with ID: {}", id);
        try {
            service.delete(id);
            log.info("Patient deleted successfully with ID: {}", id);
        } catch (Exception e) {
            log.error("Error deleting patient with ID: {}", id, e);
            throw e;
        }
    }
}
