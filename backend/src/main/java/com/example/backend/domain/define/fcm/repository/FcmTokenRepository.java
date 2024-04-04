package com.example.backend.domain.define.fcm.repository;

import com.example.backend.domain.define.fcm.FcmToken;
import org.springframework.data.repository.CrudRepository;

public interface FcmTokenRepository extends CrudRepository<FcmToken, Long> {
}
