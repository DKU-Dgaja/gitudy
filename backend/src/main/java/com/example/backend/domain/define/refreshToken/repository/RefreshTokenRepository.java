package com.example.backend.domain.define.refreshToken.repository;

import com.example.backend.domain.define.refreshToken.RefreshToken;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {
    Optional<RefreshToken> findBySubject(String subject);

    void delete(RefreshToken refreshToken);
}
