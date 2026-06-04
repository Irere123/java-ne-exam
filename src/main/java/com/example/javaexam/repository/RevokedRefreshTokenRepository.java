package com.example.javaexam.repository;

import com.example.javaexam.model.RevokedRefreshToken;
import java.time.LocalDateTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RevokedRefreshTokenRepository extends JpaRepository<RevokedRefreshToken, String> {

    /** {@code existsById(jti)} is inherited and used for reuse detection. */

    @Modifying
    @Query("DELETE FROM RevokedRefreshToken t WHERE t.expiresAt < :now")
    int deleteAllExpired(@Param("now") LocalDateTime now);
}
