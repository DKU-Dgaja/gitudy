package com.example.backend.study.api.service.bookmark;

import com.example.backend.TestConfig;
import com.example.backend.auth.config.fixture.UserFixture;
import com.example.backend.domain.define.account.bookmark.StudyBookmark;
import com.example.backend.domain.define.account.bookmark.StudyBookmarkFixture;
import com.example.backend.domain.define.account.bookmark.repository.StudyBookmarkRepository;
import com.example.backend.domain.define.account.user.User;
import com.example.backend.domain.define.account.user.repository.UserRepository;
import com.example.backend.domain.define.study.info.StudyInfo;
import com.example.backend.domain.define.study.info.StudyInfoFixture;
import com.example.backend.domain.define.study.info.repository.StudyInfoRepository;
import com.example.backend.study.api.controller.bookmark.response.IsMyBookmarkResponse;
import com.example.backend.study.api.service.bookmark.response.BookmarkInfoResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("NonAsciiCharacters")
class StudyBookmarkServiceTest extends TestConfig {
    private final static int DATA_SIZE = 10;
    private final static Long LIMIT = 5L;

    @Autowired
    private StudyBookmarkService studyBookmarkService;

    @Autowired
    private StudyBookmarkRepository studyBookmarkRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StudyInfoRepository studyInfoRepository;

    @AfterEach
    void tearDown() {
        studyBookmarkRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
        studyInfoRepository.deleteAllInBatch();
    }

    @Test
    void 커서가_null이_아닌_경우_마이_북마크_조회_테스트_1() {
        // given
        Long cursorIdx = 5L;

        User user = userRepository.save(UserFixture.generateAuthUser());
        StudyInfo study = studyInfoRepository.save(StudyInfoFixture.createDefaultPublicStudyInfo(user.getId()));

        List<StudyBookmark> bookmarkList = StudyBookmarkFixture.createDefaultStudyBookmarkList(DATA_SIZE, user.getId(), study.getId());
        studyBookmarkRepository.saveAll(bookmarkList);

        // when
        List<BookmarkInfoResponse> bookInfoList = studyBookmarkService.selectUserBookmarkList(user.getId(), cursorIdx, LIMIT);
//        System.out.println("bookInfoList.size() = " + bookInfoList.size());
//        for (BookmarkInfoResponse bookmark : bookInfoList) {
//            System.out.println("bookmark.getId() = " + bookmark.getId());
//        }

        for (BookmarkInfoResponse commit : bookInfoList) {
            assertTrue(commit.getId() < cursorIdx);
        }
    }

    @Test
    void 커서가_null이_아닌_경우_마이_북마크_조회_테스트_2() {
        // given
        Long cursorIdx = 15L;

        User user = userRepository.save(UserFixture.generateAuthUser());
        StudyInfo study = studyInfoRepository.save(StudyInfoFixture.createDefaultPublicStudyInfo(user.getId()));

        List<StudyBookmark> bookmarkList = StudyBookmarkFixture.createDefaultStudyBookmarkList(DATA_SIZE, user.getId(), study.getId());
        studyBookmarkRepository.saveAll(bookmarkList);

        // when
        List<BookmarkInfoResponse> bookInfoList = studyBookmarkService.selectUserBookmarkList(user.getId(), cursorIdx, LIMIT);
//        System.out.println("bookInfoList.size() = " + bookInfoList.size());
//        for (BookmarkInfoResponse bookmark : bookInfoList) {
//            System.out.println("bookmark.getId() = " + bookmark.getId());
//        }

        for (BookmarkInfoResponse commit : bookInfoList) {
            assertTrue(commit.getId() < cursorIdx);
        }
    }

    @Test
    void 커서가_null인_경우_마이_북마크_조회_테스트() {
        // given
        User user = userRepository.save(UserFixture.generateAuthUser());
        StudyInfo study = studyInfoRepository.save(StudyInfoFixture.createDefaultPublicStudyInfo(user.getId()));

        List<StudyBookmark> bookmarkList = StudyBookmarkFixture.createDefaultStudyBookmarkList(DATA_SIZE, user.getId(), study.getId());
        studyBookmarkRepository.saveAll(bookmarkList);

        // when
        List<BookmarkInfoResponse> bookInfoList = studyBookmarkService.selectUserBookmarkList(user.getId(), null, LIMIT);
//        System.out.println("bookInfoList.size() = " + bookInfoList.size());
//        for (BookmarkInfoResponse bookmark : bookInfoList) {
//            System.out.println("bookmark.getId() = " + bookmark.getId());
//        }

        assertEquals(LIMIT, bookInfoList.size());
    }

    @Test
    void 북마크에_테이블에_여러_사용자_여러_스터디_데이터가_많은_경우_잘_동작하는지_테스트() {
        // given
        User userA = userRepository.save(UserFixture.generateAuthUser());
        User userB = userRepository.save(UserFixture.generateGoogleUser());

        StudyInfo studyA = studyInfoRepository.save(StudyInfoFixture.createDefaultPublicStudyInfo(userA.getId()));
        StudyInfo studyB = studyInfoRepository.save(StudyInfoFixture.createDefaultPublicStudyInfo(userB.getId()));

        List<StudyBookmark> bookmarkListA_1 = StudyBookmarkFixture.createDefaultStudyBookmarkList(DATA_SIZE, userA.getId(), studyA.getId());
        studyBookmarkRepository.saveAll(bookmarkListA_1);
        List<StudyBookmark> bookmarkListB_1 = StudyBookmarkFixture.createDefaultStudyBookmarkList(DATA_SIZE, userB.getId(), studyB.getId());
        studyBookmarkRepository.saveAll(bookmarkListB_1);
        List<StudyBookmark> bookmarkListA_2 = StudyBookmarkFixture.createDefaultStudyBookmarkList(DATA_SIZE, userA.getId(), studyA.getId());
        studyBookmarkRepository.saveAll(bookmarkListA_2);
        List<StudyBookmark> bookmarkListB_2 = StudyBookmarkFixture.createDefaultStudyBookmarkList(DATA_SIZE, userB.getId(), studyB.getId());
        studyBookmarkRepository.saveAll(bookmarkListB_2);


        List<BookmarkInfoResponse> bookInfoList = studyBookmarkService.selectUserBookmarkList(userB.getId(),  null, LIMIT);
        System.out.println("bookInfoList.size() = " + bookInfoList.size());
        for (BookmarkInfoResponse bookmark : bookInfoList) {
            System.out.println("bookmark.getId() = " + bookmark.getId());
            System.out.println("bookmark.getStudyInfoResponse() = " + bookmark.getStudyInfoWithIdResponse());
            System.out.println("bookmark.getUserInfoResponse() = " + bookmark.getUserInfoResponse());
        }

        assertEquals(LIMIT, bookInfoList.size());
    }

    @Test
    void 북마크가_이미_등록된_경우_삭제_성공_테스트() {
        // given
        Long userId = 1L;
        Long studyInfoId = 1L;

        studyBookmarkRepository.save(StudyBookmarkFixture.createDefaultStudyBookmark(userId, studyInfoId));

        // when
        studyBookmarkService.handleBookmark(userId, studyInfoId);
        StudyBookmark bookmark = studyBookmarkRepository.findStudyBookmarkByUserIdAndStudyInfoId(userId, studyInfoId).orElse(null);

        // then
        assertNull(bookmark);
    }

    @Test
    void 북마크가_미등록_상태인_경우_등록_성공_테스트() {
        // given
        Long userId = 1L;
        Long studyInfoId = 1L;

        // when
        studyBookmarkService.handleBookmark(userId, studyInfoId);
        StudyBookmark bookmark = studyBookmarkRepository.findStudyBookmarkByUserIdAndStudyInfoId(userId, studyInfoId).orElse(null);

        // then
        assertNotNull(bookmark);
        assertEquals(bookmark.getUserId(), userId);
        assertEquals(bookmark.getStudyInfoId(), studyInfoId);
    }

    @Test
    void 북마크_인지_조회_성공_테스트() {
        // given
        Long userId = 1L;
        Long studyInfoId = 1L;
        boolean expectedResponse = true;

        studyBookmarkRepository.save(StudyBookmarkFixture.createDefaultStudyBookmark(userId, studyInfoId));

        // when
        IsMyBookmarkResponse response = studyBookmarkService.getIsMyBookMark(userId, studyInfoId);

        // then
        assertEquals(response.isMyBookmark(), expectedResponse);
    }

    @Test
    void 북마크_인지_조회_실패_테스트() {
        // given
        Long userId = 1L;
        Long studyInfoId = 1L;
        boolean expectedResponse = false;

        // when
        IsMyBookmarkResponse response = studyBookmarkService.getIsMyBookMark(userId, studyInfoId);

        // then
        assertEquals(response.isMyBookmark(), expectedResponse);
    }
}