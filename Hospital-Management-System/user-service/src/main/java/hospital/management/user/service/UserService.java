package hospital.management.user.service;

import hospital.management.user.dto.LoginRequest;
import hospital.management.user.dto.LoginResponse;
import hospital.management.user.dto.UserRegistrationRequest;
import hospital.management.user.entity.User;
import hospital.management.user.repository.UserRepository;
import hospital.management.user.security.JwtTokenProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public LoginResponse login(LoginRequest request) {
        log.info("Login attempt for email: {}", request.getEmail());

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found with email: " + request.getEmail()));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        String fullName = user.getFirstName() + " " + user.getLastName();
        String token = jwtTokenProvider.generateToken(
                user.getId(),
                user.getEmail(),
                "ROLE_USER",
                "USER"
        );

        log.info("User logged in successfully: {}", user.getEmail());

        return new LoginResponse(
                token,
                "Bearer",
                user.getId(),
                user.getEmail(),
                fullName,
                null,
                null,
                true
        );
    }

    public User register(UserRegistrationRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("User already exists");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());

        return userRepository.save(user);
    }

    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
    }

}

