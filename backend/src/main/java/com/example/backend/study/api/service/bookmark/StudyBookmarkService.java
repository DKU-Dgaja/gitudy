package com.example.backend.study.api.service.bookmark;

import com.example.backend.domain.define.account.bookmark.repository.StudyBookmarkRepository;
import com.example.backend.study.api.service.bookmark.response.BookmarkInfoResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StudyBookmarkService {
    private final static Long MAX_LIMIT = 10L;
    private final StudyBookmarkRepository studyBookmarkRepository;

    public List<BookmarkInfoResponse> selectUserBookmarkList(Long userId, Long cursorIdx, Long limit) {
        limit = Math.min(limit, MAX_LIMIT);

        return studyBookmarkRepository.findStudyBookmarkListByUserIdJoinStudyInfo(userId, cursorIdx, limit);
    }
}
