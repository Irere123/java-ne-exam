package com.example.javaexam.services;

import com.example.javaexam.repositories.PasswordResetTokenRepository;
import com.example.javaexam.repositories.RevokedRefreshTokenRepository;
import com.example.javaexam.repositories.VerificationTokenRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Periodically purges expired tokens so the denylist and one-time-token tables
 * don't grow without bound. Interval is {@code app.cleanup.interval-ms}; the
 * first run is delayed by that same interval so it doesn't fire at startup.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TokenCleanupService {

    private final RevokedRefreshTokenRepository revokedRefreshTokenRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final VerificationTokenRepository verificationTokenRepository;

    @Scheduled(initialDelayString = "${app.cleanup.interval-ms}", fixedRateString = "${app.cleanup.interval-ms}")
    @Transactional
    public void purgeExpiredTokens() {
        LocalDateTime now = LocalDateTime.now();
        int revoked = revokedRefreshTokenRepository.deleteAllExpired(now);
        int resets = passwordResetTokenRepository.deleteAllExpired(now);
        int verifications = verificationTokenRepository.deleteAllExpired(now);

        if (revoked + resets + verifications > 0) {
            log.info("Token cleanup removed {} revoked refresh, {} password-reset, {} verification tokens",
                    revoked, resets, verifications);
        }
    }
}
