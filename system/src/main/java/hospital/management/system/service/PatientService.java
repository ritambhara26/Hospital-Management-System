package hospital.management.system.service;

import hospital.management.system.entity.*;
import hospital.management.system.exception.ResourceNotFoundException;
import hospital.management.system.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PatientService {

    private final PatientRepository repository;

    public Patient save(Patient patient) {
        log.info("Saving new patient with email: {}", patient.getEmail());
        try {
            Patient savedPatient = repository.save(patient);
            log.info("Patient saved successfully with ID: {}", savedPatient.getPatientId());
            return savedPatient;
        } catch (Exception e) {
            log.error("Error saving patient with email: {}", patient.getEmail(), e);
            throw e;
        }
    }

    public List<Patient> getAll() {
        log.debug("Retrieving all patients");
        try {
            List<Patient> patients = repository.findAll();
            log.info("Retrieved {} patients", patients.size());
            return patients;
        } catch (Exception e) {
            log.error("Error retrieving all patients", e);
            throw e;
        }
    }

    public Patient getById(Long id) {
        log.debug("Retrieving patient with ID: {}", id);
        try {
            Patient patient = repository.findById(id)
                    .orElseThrow(() -> {
                        log.warn("Patient not found with ID: {}", id);
                        return new ResourceNotFoundException("Patient not found");
                    });
            log.info("Patient retrieved successfully with ID: {}", id);
            return patient;
        } catch (ResourceNotFoundException e) {
            log.error("Patient not found with ID: {}", id);
            throw e;
        } catch (Exception e) {
            log.error("Error retrieving patient with ID: {}", id, e);
            throw e;
        }
    }

    public Patient update(Long id, Patient patient) {
        log.info("Updating patient with ID: {}", id);
        try {
            Patient existing = repository.findById(id)
                    .orElseThrow(() -> {
                        log.warn("Patient not found for update with ID: {}", id);
                        return new ResourceNotFoundException("Patient not found");
                    });

            existing.setFirstName(patient.getFirstName());
            existing.setLastName(patient.getLastName());
            existing.setEmail(patient.getEmail());
            existing.setPhone(patient.getPhone());
            existing.setDob(patient.getDob());
            existing.setGender(patient.getGender());

            Patient updatedPatient = repository.save(existing);
            log.info("Patient updated successfully with ID: {}", id);
            return updatedPatient;
        } catch (ResourceNotFoundException e) {
            log.error("Patient not found for update with ID: {}", id);
            throw e;
        } catch (Exception e) {
            log.error("Error updating patient with ID: {}", id, e);
            throw e;
        }
    }

    public void delete(Long id) {
        log.info("Deleting patient with ID: {}", id);
        try {
            repository.deleteById(id);
            log.info("Patient deleted successfully with ID: {}", id);
        } catch (Exception e) {
            log.error("Error deleting patient with ID: {}", id, e);
            throw e;
        }
    }
}