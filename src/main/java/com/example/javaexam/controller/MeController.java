package com.example.javaexam.controller;

import com.example.javaexam.dto.MessageResponse;
import com.example.javaexam.model.User;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Example protected endpoints demonstrating how roles guard access.
 *
 * <ul>
 *   <li>{@code GET /api/me} — any authenticated user; returns their profile.</li>
 *   <li>{@code GET /api/admin/ping} — restricted to {@code ROLE_ADMIN} (by URL
 *       rule in SecurityConfig and method security here).</li>
 * </ul>
 */
@RestController
@RequestMapping("/api")
public class MeController {

    @GetMapping("/me")
    public Map<String, Object> currentUser(@AuthenticationPrincipal User user) {
        Map<String, Object> profile = new LinkedHashMap<>();
        profile.put("id", user.getId());
        profile.put("firstName", user.getFirstName());
        profile.put("lastName", user.getLastName());
        profile.put("email", user.getEmail());
        profile.put("role", user.getRole());
        profile.put("enabled", user.isEnabled());
        return profile;
    }

    @GetMapping("/admin/ping")
    @PreAuthorize("hasRole('ADMIN')")
    public MessageResponse adminOnly() {
        return new MessageResponse("Hello ADMIN — this endpoint requires ROLE_ADMIN.");
    }
}
