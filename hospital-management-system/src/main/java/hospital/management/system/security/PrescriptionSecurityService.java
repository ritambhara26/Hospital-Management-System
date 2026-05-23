package hospital.management.system.security;

import hospital.management.system.entity.Prescription;
import hospital.management.system.repository.PrescriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PrescriptionSecurityService {

    private final PrescriptionRepository prescriptionRepository;

    public boolean canAccessPrescription(Long prescriptionId, Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            return false;
        }

        Object principal = authentication.getPrincipal();
        if (!(principal instanceof Long)) {
            return false;
        }

        Long userId = (Long) principal;
        Prescription prescription = prescriptionRepository.findById(prescriptionId).orElse(null);

        if (prescription == null) {
            return false;
        }

        // Allow access if the user is the one the prescription was issued to
        return prescription.getUserId() != null && prescription.getUserId().equals(userId);
    }
}

