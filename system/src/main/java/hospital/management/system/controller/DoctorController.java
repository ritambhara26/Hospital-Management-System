package hospital.management.system.controller;

import hospital.management.system.entity.Doctor;
import hospital.management.system.service.DoctorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/doctors")
@RequiredArgsConstructor
@Slf4j
public class DoctorController {

    private final DoctorService service;

    @PostMapping
    public ResponseEntity<Doctor> create(@Valid @RequestBody Doctor doctor) {
        log.info("POST request to create doctor with email: {}", doctor.getEmail());
        try {
            Doctor createdDoctor = service.create(doctor);
            log.info("Doctor created successfully with ID: {}", createdDoctor.getDoctorId());
            return ResponseEntity.ok(createdDoctor);
        } catch (Exception e) {
            log.error("Error creating doctor with email: {}", doctor.getEmail(), e);
            throw e;
        }
    }

    @GetMapping
    public List<Doctor> getAll() {
        log.debug("GET request to retrieve all doctors");
        try {
            List<Doctor> doctors = service.getAll();
            log.info("Retrieved {} doctors", doctors.size());
            return doctors;
        } catch (Exception e) {
            log.error("Error retrieving all doctors", e);
            throw e;
        }
    }

    @GetMapping("/{id}")
    public Doctor get(@PathVariable Long id) {
        log.debug("GET request to retrieve doctor with ID: {}", id);
        try {
            Doctor doctor = service.getById(id);
            log.info("Doctor retrieved successfully with ID: {}", id);
            return doctor;
        } catch (Exception e) {
            log.error("Error retrieving doctor with ID: {}", id, e);
            throw e;
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Doctor> update(@PathVariable Long id,
                                         @Valid @RequestBody Doctor doctor) {
        log.info("PUT request to update doctor with ID: {}", id);
        try {
            doctor.setDoctorId(id);
            Doctor updatedDoctor = service.update(id, doctor);
            log.info("Doctor updated successfully with ID: {}", id);
            return ResponseEntity.ok(updatedDoctor);
        } catch (Exception e) {
            log.error("Error updating doctor with ID: {}", id, e);
            throw e;
        }
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        log.info("DELETE request to delete doctor with ID: {}", id);
        try {
            service.delete(id);
            log.info("Doctor deleted successfully with ID: {}", id);
        } catch (Exception e) {
            log.error("Error deleting doctor with ID: {}", id, e);
            throw e;
        }
    }
}