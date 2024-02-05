package com.example.backend.domain.define.account.bookmark.repository;

import com.example.backend.domain.define.account.bookmark.StudyBookmark;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudyBookmarkRepository extends JpaRepository<StudyBookmark, Long>, StudyBookmarkRepositoryCustom {
}
