package com.example.backend.study.api.service.bookmark;

import com.example.backend.common.exception.ExceptionMessage;
import com.example.backend.common.exception.bookmark.BookmarkException;
import com.example.backend.domain.define.account.bookmark.StudyBookmark;
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

    @Transactional
    public void handleBookmark(Long userId, Long studyInfoId) {
        // 북마크가 이미 등록되어있는지 확인
        boolean bookmarkExists = studyBookmarkRepository.existsStudyBookmarkByUserIdAndStudyInfoId(userId, studyInfoId);

        // 북마크가 이미 등록되어있는 경우 삭제
        if (bookmarkExists) {
            if(studyBookmarkRepository.deleteStudyBookmarkByUserIdAndStudyInfoId(userId, studyInfoId) <= 0) {
                log.error(">>>> {} <<<<", ExceptionMessage.BOOKMARK_DELETE_FAIL.getText());
                throw new BookmarkException(ExceptionMessage.BOOKMARK_DELETE_FAIL);
            };

            log.info(">>>> 북마크가 삭제되었습니다 <<<<");

        // 북마크가 등록되어있지 않은 경우 등록
        } else {
            StudyBookmark savedBookmark = studyBookmarkRepository.save(StudyBookmark.builder()
                    .userId(userId)
                    .studyInfoId(studyInfoId)
                    .build());

            log.info(">>>> 북마크가 등록되었습니다: {} <<<<", savedBookmark.getId());
        }
    }
}
