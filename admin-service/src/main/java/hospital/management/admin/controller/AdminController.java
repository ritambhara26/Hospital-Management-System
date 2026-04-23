package hospital.management.admin.controller;

import hospital.management.admin.dto.AdminCreationRequest;


import hospital.management.admin.dto.AdminLoginRequest;
import hospital.management.admin.dto.AdminLoginResponse;
import hospital.management.admin.entity.Admin;
import hospital.management.admin.service.AdminService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
@RestController
@RequestMapping("/api/admin")
@Slf4j
public class AdminController {

    @Autowired
    private AdminService adminService;


    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody AdminLoginRequest request) {
        log.info("Admin login attempt for email: {}", request.getEmail());

        AdminLoginResponse response = adminService.login(request);

        return ResponseEntity.ok(response);
    }


    @PostMapping("/create")
    public ResponseEntity<?> createAdmin(@Valid @RequestBody AdminCreationRequest request) {
        log.info("Creating admin with email: {}", request.getEmail());

        Admin admin = adminService.createAdmin(request);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Admin created successfully");
        response.put("adminId", admin.getId());
        response.put("email", admin.getEmail());
        response.put("first_name", admin.getFirstName());
        response.put("last_name", admin.getLastName());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @GetMapping("/{adminId}")
    public ResponseEntity<?> getAdminById(@PathVariable Long adminId) {
        log.info("Fetching admin with ID: {}", adminId);

        Admin admin = adminService.getAdminById(adminId);

        return ResponseEntity.ok(admin);
    }


    @GetMapping("/email/{email}")
    public ResponseEntity<?> getAdminByEmail(@PathVariable String email) {
        log.info("Fetching admin with email: {}", email);

        Admin admin = adminService.getAdminByEmail(email);

        return ResponseEntity.ok(admin);
    }


    @PutMapping("/{adminId}")
    public ResponseEntity<?> updateAdmin(
            @PathVariable Long adminId,
            @Valid @RequestBody AdminCreationRequest request
    ) {
        log.info("Updating admin with ID: {}", adminId);

        Admin updatedAdmin = adminService.updateAdmin(adminId, request);

        return ResponseEntity.ok(updatedAdmin);
    }


    @GetMapping("/health")
    public ResponseEntity<?> health() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "service", "admin-service"
        ));
    }
}