package com.example.backend.domain.define.user.repository;

import com.example.backend.domain.define.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // select u from user u where u.email = :email
    Optional<User> findByEmail(String email);
}
