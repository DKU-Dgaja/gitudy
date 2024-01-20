package com.example.backend.domain.define.state;

import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface LoginStateRepository extends CrudRepository<LoginStateRepository, UUID> {
}
