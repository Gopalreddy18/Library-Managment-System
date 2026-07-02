package com.aidsamentor.auth.repository;

import com.aidsamentor.auth.entity.RefreshToken;
import com.aidsamentor.user.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);

    void deleteByUser(User user);
}
