package com.example.backend.domain.define.study.todo.repository;

import com.example.backend.domain.define.study.todo.info.StudyTodo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface StudyTodoRepository extends JpaRepository<StudyTodo, Long> , StudyTodoRepositoryCustom{
    Optional<StudyTodo> findByIdAndStudyInfoId(Long todoId, Long studyInfoId);

    List<StudyTodo> findByStudyInfoId(Long studyInfoId);
    List<StudyTodo> findByStudyInfoIdAndTodoDateBetween(Long studyInfoId, LocalDate startDate, LocalDate endDate);

    List<StudyTodo> findByStudyInfoIdAndTodoDateAfter(Long studyInfoId, LocalDate date);

}