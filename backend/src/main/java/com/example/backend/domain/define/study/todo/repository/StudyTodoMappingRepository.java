package com.example.backend.domain.define.study.todo.repository;

import com.example.backend.domain.define.study.todo.mapping.StudyTodoMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudyTodoMappingRepository extends JpaRepository<StudyTodoMapping, Long> {
    List<StudyTodoMapping> findByUserId(Long userId);
}