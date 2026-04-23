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
public class AppointmentService {

    private final AppointmentRepository appointmentRepo;
    private final PatientRepository patientRepo;
    private final DoctorRepository doctorRepo;

    public Appointment create(Appointment appointment) {
        log.info("Creating new appointment for patient ID: {} and doctor ID: {}",
                appointment.getPatient().getPatientId(), appointment.getDoctor().getDoctorId());
        try {
            Patient patient = patientRepo.findById(appointment.getPatient().getPatientId())
                    .orElseThrow(() -> {
                        log.warn("Patient not found for appointment creation with ID: {}", appointment.getPatient().getPatientId());
                        return new ResourceNotFoundException("Patient not found");
                    });

            Doctor doctor = doctorRepo.findById(appointment.getDoctor().getDoctorId())
                    .orElseThrow(() -> {
                        log.warn("Doctor not found for appointment creation with ID: {}", appointment.getDoctor().getDoctorId());
                        return new ResourceNotFoundException("Doctor not found");
                    });

            appointment.setPatient(patient);
            appointment.setDoctor(doctor);
            appointment.setAppointmentDate(java.time.LocalDateTime.now());
            appointment.setStatus("Scheduled");

            Appointment savedAppointment = appointmentRepo.save(appointment);
            log.info("Appointment created successfully with ID: {} for patient: {} and doctor: {}",
                    savedAppointment.getAppointmentId(), patient.getEmail(), doctor.getEmail());
            return savedAppointment;
        } catch (ResourceNotFoundException e) {
            log.error("Resource not found during appointment creation", e);
            throw e;
        } catch (Exception e) {
            log.error("Error creating appointment for patient ID: {} and doctor ID: {}",
                    appointment.getPatient().getPatientId(), appointment.getDoctor().getDoctorId(), e);
            throw e;
        }
    }

    public List<Appointment> getAll() {
        log.debug("Retrieving all appointments");
        try {
            List<Appointment> appointments = appointmentRepo.findAll();
            log.info("Retrieved {} appointments", appointments.size());
            return appointments;
        } catch (Exception e) {
            log.error("Error retrieving all appointments", e);
            throw e;
        }
    }

    public Appointment getById(Long id) {
        log.debug("Retrieving appointment with ID: {}", id);
        try {
            Appointment appointment = appointmentRepo.findById(id)
                    .orElseThrow(() -> {
                        log.warn("Appointment not found with ID: {}", id);
                        return new ResourceNotFoundException("Appointment not found");
                    });
            log.info("Appointment retrieved successfully with ID: {}", id);
            return appointment;
        } catch (ResourceNotFoundException e) {
            log.error("Appointment not found with ID: {}", id);
            throw e;
        } catch (Exception e) {
            log.error("Error retrieving appointment with ID: {}", id, e);
            throw e;
        }
    }

    public Appointment update(Long id, Appointment appointment) {
        log.info("Updating appointment with ID: {} to status: {}", id, appointment.getStatus());
        try {
            Appointment existing = appointmentRepo.findById(id)
                    .orElseThrow(() -> {
                        log.warn("Appointment not found for update with ID: {}", id);
                        return new ResourceNotFoundException("Appointment not found");
                    });

            existing.setStatus(appointment.getStatus());

            Appointment updatedAppointment = appointmentRepo.save(existing);
            log.info("Appointment updated successfully with ID: {} to status: {}", id, appointment.getStatus());
            return updatedAppointment;
        } catch (ResourceNotFoundException e) {
            log.error("Appointment not found for update with ID: {}", id);
            throw e;
        } catch (Exception e) {
            log.error("Error updating appointment with ID: {}", id, e);
            throw e;
        }
    }

    public void delete(Long id) {
        log.info("Deleting appointment with ID: {}", id);
        try {
            appointmentRepo.deleteById(id);
            log.info("Appointment deleted successfully with ID: {}", id);
        } catch (Exception e) {
            log.error("Error deleting appointment with ID: {}", id, e);
            throw e;
        }
    }
}