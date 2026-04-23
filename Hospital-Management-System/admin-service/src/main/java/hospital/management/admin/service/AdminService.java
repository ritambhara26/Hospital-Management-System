package hospital.management.admin.service;

import hospital.management.admin.dto.AdminCreationRequest;
import hospital.management.admin.dto.AdminLoginRequest;
import hospital.management.admin.dto.AdminLoginResponse;
import hospital.management.admin.entity.Admin;
import hospital.management.admin.repository.AdminRepository;
import hospital.management.admin.security.JwtTokenProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AdminService {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();


    public AdminLoginResponse login(AdminLoginRequest request) {
        log.info("Admin login attempt for email: {}", request.getEmail());

        Admin admin = adminRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Admin not found with email: " + request.getEmail()));

        if (!passwordEncoder.matches(request.getPassword(), admin.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        String token = jwtTokenProvider.generateToken(
                admin.getId(),
                admin.getEmail(),
                "ROLE_ADMIN",
                "ADMIN"
        );

        log.info("Admin logged in successfully: {}", admin.getEmail());

        return new AdminLoginResponse(
                token,
                "Bearer",
                admin.getId(),
                admin.getEmail(),
                admin.getFirstName(),
                admin.getLastName()
        );
    }

    public Admin createAdmin(AdminCreationRequest request) {
        log.info("Creating admin account for email: {}", request.getEmail());

        if (adminRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Admin already exists with email: " + request.getEmail());
        }

        Admin admin = new Admin();
        admin.setEmail(request.getEmail());
        admin.setPassword(passwordEncoder.encode(request.getPassword()));
        admin.setFirstName(request.getFirstName());
        admin.setLastName(request.getLastName());

        Admin savedAdmin = adminRepository.save(admin);
        log.info("Admin created successfully: {}", savedAdmin.getEmail());

        return savedAdmin;
    }


    public Admin getAdminById(Long adminId) {
        return adminRepository.findById(adminId)
                .orElseThrow(() -> new RuntimeException("Admin not found with id: " + adminId));
    }


    public Admin getAdminByEmail(String email) {
        return adminRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Admin not found with email: " + email));
    }


    public Admin updateAdmin(Long adminId, AdminCreationRequest request) {
        log.info("Updating admin with id: {}", adminId);

        Admin admin = getAdminById(adminId);

        if (request.getFirstName() != null) {
            admin.setFirstName(request.getFirstName());
        }

        if (request.getLastName() != null) {
            admin.setLastName(request.getLastName());
        }

        if (request.getPassword() != null) {
            admin.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        return adminRepository.save(admin);
    }
}