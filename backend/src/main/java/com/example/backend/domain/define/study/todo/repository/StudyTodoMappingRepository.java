package com.example.backend.domain.define.study.todo.repository;

import com.example.backend.domain.define.study.todo.mapping.StudyTodoMapping;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StudyTodoMappingRepository extends JpaRepository<StudyTodoMapping, Long> {

      List<StudyTodoMapping> findByTodoId(Long todoId);

      Optional<StudyTodoMapping> findByTodoIdAndUserId(Long todoId, Long userId);
      

}
