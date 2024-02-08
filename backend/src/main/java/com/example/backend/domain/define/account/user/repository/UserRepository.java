package com.example.backend.domain.define.account.user.repository;

import com.example.backend.domain.define.account.user.User;
import com.example.backend.domain.define.account.user.constant.UserPlatformType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // select u from user u where u.platformId = :platformId and u.platformType = :platformType
    Optional<User> findByPlatformIdAndPlatformType(String platformId, UserPlatformType platformType);

   Optional<User> findById(Long userId);
}
