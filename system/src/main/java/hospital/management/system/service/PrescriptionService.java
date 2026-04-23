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
public class PrescriptionService {

    private final PrescriptionRepository prescriptionRepo;
    private final AppointmentRepository appointmentRepo;

    public Prescription create(Prescription prescription) {
        log.info("Creating new prescription for appointment ID: {}", prescription.getAppointment().getAppointmentId());
        try {
            Appointment appointment = appointmentRepo.findById(prescription.getAppointment().getAppointmentId())
                    .orElseThrow(() -> {
                        log.warn("Appointment not found for prescription creation with ID: {}", prescription.getAppointment().getAppointmentId());
                        return new ResourceNotFoundException("Appointment not found");
                    });

            prescription.setAppointment(appointment);

            Prescription savedPrescription = prescriptionRepo.save(prescription);
            log.info("Prescription created successfully with ID: {} for appointment ID: {}",
                    savedPrescription.getPrescriptionId(), appointment.getAppointmentId());
            return savedPrescription;
        } catch (ResourceNotFoundException e) {
            log.error("Resource not found during prescription creation", e);
            throw e;
        } catch (Exception e) {
            log.error("Error creating prescription for appointment ID: {}", prescription.getAppointment().getAppointmentId(), e);
            throw e;
        }
    }

    public List<Prescription> getAll() {
        log.debug("Retrieving all prescriptions");
        try {
            List<Prescription> prescriptions = prescriptionRepo.findAll();
            log.info("Retrieved {} prescriptions", prescriptions.size());
            return prescriptions;
        } catch (Exception e) {
            log.error("Error retrieving all prescriptions", e);
            throw e;
        }
    }

    public Prescription getById(Long id) {
        log.debug("Retrieving prescription with ID: {}", id);
        try {
            Prescription prescription = prescriptionRepo.findById(id)
                    .orElseThrow(() -> {
                        log.warn("Prescription not found with ID: {}", id);
                        return new ResourceNotFoundException("Prescription not found");
                    });
            log.info("Prescription retrieved successfully with ID: {}", id);
            return prescription;
        } catch (ResourceNotFoundException e) {
            log.error("Prescription not found with ID: {}", id);
            throw e;
        } catch (Exception e) {
            log.error("Error retrieving prescription with ID: {}", id, e);
            throw e;
        }
    }

    public Prescription update(Long id, Prescription prescription) {
        log.info("Updating prescription with ID: {}", id);
        try {
            Prescription existing = prescriptionRepo.findById(id)
                    .orElseThrow(() -> {
                        log.warn("Prescription not found for update with ID: {}", id);
                        return new ResourceNotFoundException("Prescription not found");
                    });

            existing.setNotes(prescription.getNotes());

            Prescription updatedPrescription = prescriptionRepo.save(existing);
            log.info("Prescription updated successfully with ID: {}", id);
            return updatedPrescription;
        } catch (ResourceNotFoundException e) {
            log.error("Prescription not found for update with ID: {}", id);
            throw e;
        } catch (Exception e) {
            log.error("Error updating prescription with ID: {}", id, e);
            throw e;
        }
    }

    public void delete(Long id) {
        log.info("Deleting prescription with ID: {}", id);
        try {
            prescriptionRepo.deleteById(id);
            log.info("Prescription deleted successfully with ID: {}", id);
        } catch (Exception e) {
            log.error("Error deleting prescription with ID: {}", id, e);
            throw e;
        }
    }
}