package com.example.javaexam.repository;

import com.example.javaexam.model.User;
import com.example.javaexam.model.VerificationToken;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {

    Optional<VerificationToken> findByToken(String token);

    Optional<VerificationToken> findFirstByUserOrderByCreatedAtDesc(User user);
}
