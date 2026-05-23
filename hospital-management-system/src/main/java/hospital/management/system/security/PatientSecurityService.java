package hospital.management.system.security;

import hospital.management.system.entity.Patient;
import hospital.management.system.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PatientSecurityService {

    private final PatientRepository patientRepository;

    public boolean canAccessPatient(Long patientId, Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            return false;
        }

        Object principal = authentication.getPrincipal();
        if (!(principal instanceof Long)) {
            return false;
        }

        Long userId = (Long) principal;
        Patient patient = patientRepository.findById(patientId).orElse(null);

        if (patient == null) {
            return false;
        }

        // Allow access if the user is the patient owner
        return patient.getUserId() != null && patient.getUserId().equals(userId);
    }
}

