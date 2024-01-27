package com.example.backend.domain.define.test.repository;

import com.example.backend.domain.define.test.DslTest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DslTestRepository extends JpaRepository<DslTest, Long>, DslTestRepositoryCustom {

}
