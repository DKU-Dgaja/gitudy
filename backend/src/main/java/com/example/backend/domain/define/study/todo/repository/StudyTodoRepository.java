package com.example.backend.domain.define.study.todo.repository;

import com.example.backend.domain.define.study.todo.info.StudyTodo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudyTodoRepository extends JpaRepository<StudyTodo, Long> {
}