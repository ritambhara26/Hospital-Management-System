package hospital.management.system.service;

import hospital.management.system.exception.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import hospital.management.system.entity.*;
import hospital.management.system.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DoctorService {

    private final DoctorRepository repository;

    public Doctor create(Doctor doctor) {
        log.info("Creating new doctor with email: {}", doctor.getEmail());
        try {
            Doctor savedDoctor = repository.save(doctor);
            log.info("Doctor created successfully with ID: {} and specialization: {}", savedDoctor.getDoctorId(), savedDoctor.getSpecialization());
            return savedDoctor;
        } catch (Exception e) {
            log.error("Error creating doctor with email: {}", doctor.getEmail(), e);
            throw e;
        }
    }

    public List<Doctor> getAll() {
        log.debug("Retrieving all doctors");
        try {
            List<Doctor> doctors = repository.findAll();
            log.info("Retrieved {} doctors", doctors.size());
            return doctors;
        } catch (Exception e) {
            log.error("Error retrieving all doctors", e);
            throw e;
        }
    }

    public Doctor getById(Long id) {
        log.debug("Retrieving doctor with ID: {}", id);
        try {
            Doctor doctor = repository.findById(id)
                    .orElseThrow(() -> {
                        log.warn("Doctor not found with ID: {}", id);
                        return new ResourceNotFoundException("Doctor not found");
                    });
            log.info("Doctor retrieved successfully with ID: {}", id);
            return doctor;
        } catch (ResourceNotFoundException e) {
            log.error("Doctor not found with ID: {}", id);
            throw e;
        } catch (Exception e) {
            log.error("Error retrieving doctor with ID: {}", id, e);
            throw e;
        }
    }

    public Doctor update(Long id, Doctor doctor) {
        log.info("Updating doctor with ID: {}", id);
        try {
            Doctor existing = repository.findById(id)
                    .orElseThrow(() -> {
                        log.warn("Doctor not found for update with ID: {}", id);
                        return new ResourceNotFoundException("Doctor not found");
                    });

            existing.setFirstName(doctor.getFirstName());
            existing.setLastName(doctor.getLastName());
            existing.setSpecialization(doctor.getSpecialization());
            existing.setPhone(doctor.getPhone());
            existing.setEmail(doctor.getEmail());
            existing.setExperienceYears(doctor.getExperienceYears());

            Doctor updatedDoctor = repository.save(existing);
            log.info("Doctor updated successfully with ID: {}", id);
            return updatedDoctor;
        } catch (ResourceNotFoundException e) {
            log.error("Doctor not found for update with ID: {}", id);
            throw e;
        } catch (Exception e) {
            log.error("Error updating doctor with ID: {}", id, e);
            throw e;
        }
    }

    public void delete(Long id) {
        log.info("Deleting doctor with ID: {}", id);
        try {
            repository.deleteById(id);
            log.info("Doctor deleted successfully with ID: {}", id);
        } catch (Exception e) {
            log.error("Error deleting doctor with ID: {}", id, e);
            throw e;
        }
    }

    public Doctor save(Doctor doctor) {
        log.info("Saving doctor with email: {}", doctor.getEmail());
        try {
            Doctor savedDoctor = repository.save(doctor);
            log.info("Doctor saved successfully with ID: {}", savedDoctor.getDoctorId());
            return savedDoctor;
        } catch (Exception e) {
            log.error("Error saving doctor with email: {}", doctor.getEmail(), e);
            throw e;
        }
    }

}