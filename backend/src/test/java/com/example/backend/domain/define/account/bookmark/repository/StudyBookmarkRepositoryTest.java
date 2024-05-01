package com.example.backend.domain.define.account.bookmark.repository;

import com.example.backend.TestConfig;
import com.example.backend.auth.config.fixture.UserFixture;
import com.example.backend.domain.define.account.bookmark.StudyBookmark;
import com.example.backend.domain.define.account.bookmark.StudyBookmarkFixture;
import com.example.backend.domain.define.account.user.User;
import com.example.backend.domain.define.account.user.repository.UserRepository;
import com.example.backend.domain.define.study.info.StudyInfo;
import com.example.backend.domain.define.study.info.StudyInfoFixture;
import com.example.backend.domain.define.study.info.repository.StudyInfoRepository;
import com.example.backend.study.api.service.bookmark.response.BookmarkInfoResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


@SuppressWarnings("NonAsciiCharacters")
class StudyBookmarkRepositoryTest extends TestConfig {
    private final static int DATA_SIZE = 20;
    private final static Long LIMIT = 10L;

    @Autowired
    private StudyBookmarkRepository studyBookmarkRepository;

    @Autowired
    private StudyInfoRepository studyInfoRepository;

    @Autowired
    private UserRepository userRepository;

    @AfterEach
    void tearDown() {
        studyBookmarkRepository.deleteAllInBatch();
        studyInfoRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    @Test
    void 북마크_조회_쿼리_테스트() {
        // given
        Long cursorIdx = 25L;

        User user = userRepository.save(UserFixture.generateAuthUser());
        StudyInfo study = studyInfoRepository.save(StudyInfoFixture.createDefaultPublicStudyInfo(user.getId()));

        List<StudyBookmark> bookmarkList = StudyBookmarkFixture.createDefaultStudyBookmarkList(DATA_SIZE, user.getId(), study.getId());
        studyBookmarkRepository.saveAll(bookmarkList);

        // when
        List<BookmarkInfoResponse> bookmarkInfoList = studyBookmarkRepository.findStudyBookmarkListByUserIdJoinStudyInfo(user.getId(), cursorIdx, LIMIT);
//        System.out.println("bookmarkInfoList.size() = " + bookmarkInfoList.size());
//        for (BookmarkInfoResponse b : bookmarkInfoList) {
//            System.out.println("b.getId() = " + b.getId());
//        }

        // then
        for (BookmarkInfoResponse b : bookmarkInfoList) {
            assertTrue(b.getId() < cursorIdx);
            assertEquals(study.getId(), b.getStudyInfoWithIdResponse().getId());
            assertEquals(study.getTopic(), b.getStudyInfoWithIdResponse().getTopic());
            assertEquals(user.getId(), b.getUserInfoResponse().getUserId());
            assertEquals(user.getGithubId(), b.getUserInfoResponse().getGithubId());
        }
    }


    @Test
    void cursor가_null인_경우_북마크_조회_쿼리_테스트() {
        // given
        User user = userRepository.save(UserFixture.generateAuthUser());
        StudyInfo study = studyInfoRepository.save(StudyInfoFixture.createDefaultPublicStudyInfo(user.getId()));

        List<StudyBookmark> bookmarkList = StudyBookmarkFixture.createDefaultStudyBookmarkList(DATA_SIZE, user.getId(), study.getId());
        studyBookmarkRepository.saveAll(bookmarkList);

        // when
        List<BookmarkInfoResponse> bookmarkInfoList = studyBookmarkRepository.findStudyBookmarkListByUserIdJoinStudyInfo(user.getId(), null, LIMIT);
//        System.out.println("bookmarkInfoList.size() = " + bookmarkInfoList.size());
//        for (BookmarkInfoResponse b : bookmarkInfoList) {
//            System.out.println("b.getId() = " + b.getId());
//        }

        // then
        assertEquals(LIMIT, bookmarkInfoList.size());
        for (BookmarkInfoResponse b : bookmarkInfoList) {
            assertEquals(study.getId(), b.getStudyInfoWithIdResponse().getId());
            assertEquals(study.getTopic(), b.getStudyInfoWithIdResponse().getTopic());
            assertEquals(user.getId(), b.getUserInfoResponse().getUserId());
            assertEquals(user.getGithubId(), b.getUserInfoResponse().getGithubId());
        }
    }
}