package hospital.management.system.security;

import hospital.management.system.entity.Appointment;
import hospital.management.system.repository.AppointmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AppointmentSecurityService {

    private final AppointmentRepository appointmentRepository;

    public boolean canAccessAppointment(Long appointmentId, Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            return false;
        }

        Object principal = authentication.getPrincipal();
        if (!(principal instanceof Long)) {
            return false;
        }

        Long userId = (Long) principal;
        Appointment appointment = appointmentRepository.findById(appointmentId).orElse(null);

        if (appointment == null) {
            return false;
        }

        // Allow access if the user is the one who created the appointment
        return appointment.getUserId() != null && appointment.getUserId().equals(userId);
    }

    public boolean canUpdateAppointment(Long appointmentId, Authentication authentication) {
        return canAccessAppointment(appointmentId, authentication);
    }
}

