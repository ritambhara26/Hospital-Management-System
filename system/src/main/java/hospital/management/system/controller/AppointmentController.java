package hospital.management.system.controller;

import hospital.management.system.entity.Appointment;
import hospital.management.system.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
@Slf4j
public class AppointmentController {

    private final AppointmentService service;

    @PostMapping
    public ResponseEntity<Appointment> create(@RequestBody Appointment appointment) {
        log.info("POST request to create appointment");
        try {
            Appointment createdAppointment = service.create(appointment);
            log.info("Appointment created successfully with ID: {}", createdAppointment.getAppointmentId());
            return ResponseEntity.ok(createdAppointment);
        } catch (Exception e) {
            log.error("Error creating appointment", e);
            throw e;
        }
    }

    @GetMapping
    public List<Appointment> getAll() {
        log.debug("GET request to retrieve all appointments");
        try {
            List<Appointment> appointments = service.getAll();
            log.info("Retrieved {} appointments", appointments.size());
            return appointments;
        } catch (Exception e) {
            log.error("Error retrieving all appointments", e);
            throw e;
        }
    }

    @GetMapping("/{id}")
    public Appointment getById(@PathVariable Long id) {
        log.debug("GET request to retrieve appointment with ID: {}", id);
        try {
            Appointment appointment = service.getById(id);
            log.info("Appointment retrieved successfully with ID: {}", id);
            return appointment;
        } catch (Exception e) {
            log.error("Error retrieving appointment with ID: {}", id, e);
            throw e;
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Appointment> update(@PathVariable Long id, @RequestBody Appointment appointment) {
        log.info("PUT request to update appointment with ID: {}", id);
        try {
            Appointment updatedAppointment = service.update(id, appointment);
            log.info("Appointment updated successfully with ID: {}", id);
            return ResponseEntity.ok(updatedAppointment);
        } catch (Exception e) {
            log.error("Error updating appointment with ID: {}", id, e);
            throw e;
        }
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        log.info("DELETE request to delete appointment with ID: {}", id);
        try {
            service.delete(id);
            log.info("Appointment deleted successfully with ID: {}", id);
        } catch (Exception e) {
            log.error("Error deleting appointment with ID: {}", id, e);
            throw e;
        }
    }
}