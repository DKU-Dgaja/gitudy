package com.example.backend.domain.define.account.bookmark.repository;

import com.example.backend.domain.define.account.bookmark.StudyBookmark;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StudyBookmarkRepository extends JpaRepository<StudyBookmark, Long>, StudyBookmarkRepositoryCustom {
    Optional<StudyBookmark> findStudyBookmarkByUserIdAndStudyInfoId(Long userId, Long studyInfoId);

    int deleteStudyBookmarkByUserIdAndStudyInfoId(Long userId, Long studyInfoId);

    boolean existsStudyBookmarkByUserIdAndStudyInfoId(Long userId, Long studyInfoId);
}
