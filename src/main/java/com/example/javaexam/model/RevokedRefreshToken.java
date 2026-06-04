package com.example.javaexam.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

/**
 * Denylist entry for a single revoked refresh token, keyed by its JWT id
 * ({@code jti}). Presence means the token must no longer be accepted — used for
 * single-device logout and for detecting reuse of a rotated refresh token.
 * Entries are purged after {@code expiresAt} by the cleanup task.
 */
@Entity
@Table(name = "revoked_refresh_tokens")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RevokedRefreshToken {

    @Id
    private String jti;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @CreationTimestamp
    @Column(name = "revoked_at", nullable = false, updatable = false)
    private LocalDateTime revokedAt;
}
