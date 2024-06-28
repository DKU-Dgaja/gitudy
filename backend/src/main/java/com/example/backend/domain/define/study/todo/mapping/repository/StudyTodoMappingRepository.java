package com.example.backend.domain.define.study.todo.mapping.repository;

import com.example.backend.domain.define.study.todo.mapping.StudyTodoMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudyTodoMappingRepository extends JpaRepository<StudyTodoMapping, Long>, StudyTodoMappingRepositoryCustom {
    Optional<StudyTodoMapping> deleteByTodoId(Long todoId);

    List<StudyTodoMapping> findByTodoId(Long todoId);

    List<StudyTodoMapping> findByUserId(Long userId);
}