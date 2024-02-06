package com.example.backend.domain.define.study.todo.repository;

import com.example.backend.domain.define.study.todo.info.StudyTodo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudyTodoRepository extends JpaRepository<StudyTodo, Long> {

    List<StudyTodo> findByStudyInfoId(Long studyInfoId);

}
