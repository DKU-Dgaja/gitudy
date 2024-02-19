package com.example.backend.domain.define.study.todo.repository;

import com.example.backend.domain.define.study.todo.mapping.StudyTodoMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudyTodoMappingRepository extends JpaRepository<StudyTodoMapping, Long> {
    Optional<StudyTodoMapping> deleteByTodoId(Long todoId);

    List<StudyTodoMapping> findByTodoId(Long todoId);
}