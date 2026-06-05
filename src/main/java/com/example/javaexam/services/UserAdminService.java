package com.example.javaexam.services;

import com.example.javaexam.dtos.AdminCreateUserRequest;
import com.example.javaexam.dtos.UserResponse;
import com.example.javaexam.exceptions.ApiException;
import com.example.javaexam.models.User;
import com.example.javaexam.models.enums.Role;
import com.example.javaexam.models.enums.Status;
import com.example.javaexam.repositories.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Admin user management: create accounts, change roles, activate/deactivate. */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserAdminService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CustomerService customerService;

    @Transactional
    public UserResponse create(AdminCreateUserRequest request) {
        String email = request.email().trim().toLowerCase();
        if (userRepository.existsByEmail(email)) {
            throw ApiException.conflict("An account with email '" + email + "' already exists");
        }

        User user = User.builder()
                .firstName(request.firstName().trim())
                .lastName(request.lastName().trim())
                .email(email)
                .countryCode(AuthService.normalizeCountryCode(request.countryCode()))
                .phoneNumber(request.phoneNumber().trim())
                .password(passwordEncoder.encode(request.password()))
                .role(request.role())
                .enabled(true)
                .build();
        userRepository.save(user);

        // Attach to a pre-existing customer profile with the same email, if any.
        customerService.linkUserToCustomer(user);

        log.info("Admin created user {} with role {}", email, request.role());
        return UserResponse.from(user);
    }

    @Transactional
    public UserResponse updateRole(Long id, Role role) {
        User user = getEntity(id);
        user.setRole(role);
        user.setTokenVersion(user.getTokenVersion() + 1); // invalidate tokens carrying the old role
        userRepository.save(user);
        log.info("User {} role changed to {}", id, role);
        return UserResponse.from(user);
    }

    @Transactional
    public UserResponse updateStatus(Long id, Status status) {
        User user = getEntity(id);
        user.setStatus(status);
        if (status == Status.INACTIVE) {
            user.setTokenVersion(user.getTokenVersion() + 1); // revoke the deactivated user's sessions
        }
        userRepository.save(user);
        log.info("User {} status set to {}", id, status);
        return UserResponse.from(user);
    }

    @Transactional(readOnly = true)
    public List<UserResponse> list() {
        return userRepository.findAll().stream().map(UserResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public UserResponse get(Long id) {
        return UserResponse.from(getEntity(id));
    }

    private User getEntity(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> ApiException.notFound("User not found: " + id));
    }
}
