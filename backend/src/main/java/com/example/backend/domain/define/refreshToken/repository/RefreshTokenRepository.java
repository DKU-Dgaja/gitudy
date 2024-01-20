package com.example.backend.domain.define.refreshToken.repository;

import com.example.backend.domain.define.refreshToken.RefreshToken;
import org.springframework.data.repository.CrudRepository;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {
}
