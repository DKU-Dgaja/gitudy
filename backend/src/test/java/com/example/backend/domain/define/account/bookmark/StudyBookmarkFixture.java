package com.example.backend.domain.define.account.bookmark;

import java.util.ArrayList;
import java.util.List;

public class StudyBookmarkFixture {

    // 테스트용 북마크 생성 메서드
    public static StudyBookmark createDefaultStudyBookmark(Long userId, Long studyInfoId) {
        return StudyBookmark.builder()
                .studyInfoId(studyInfoId)
                .userId(userId)
                .build();
    }

    // 테스트용 북마크 리스트 생성 메서드
    public static List<StudyBookmark> createDefaultStudyBookmarkList(int count, Long userId, Long studyInfoId) {
        List<StudyBookmark> studyBookmarks = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            studyBookmarks.add(createDefaultStudyBookmark(userId, studyInfoId));
        }
        return studyBookmarks;
    }
}
