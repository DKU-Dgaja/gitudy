package com.example.backend.domain.define.study.member.repository;

import com.example.backend.auth.TestConfig;
import com.example.backend.domain.define.study.member.StudyMember;
import com.example.backend.domain.define.study.member.StudyMemberFixture;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;


@SuppressWarnings("NonAsciiCharacters")
class StudyMemberRepositoryTest extends TestConfig {
    @Autowired
    private StudyMemberRepository studyMemberRepository;

    @AfterEach
    void tearDown() {
        studyMemberRepository.deleteAllInBatch();
    }

    @Test
    void 해당_유저가_스터디의_활동중인_스터디원인지_확인_스터디원일_경우() {
        // given
        Long userId = 1L;
        Long studyInfoId = 1L;

        StudyMember savedMember = studyMemberRepository.save(StudyMemberFixture.createDefaultStudyMember(userId, studyInfoId));

        // when
        assertTrue(studyMemberRepository.existsStudyMemberByUserIdAndStudyInfoId(savedMember.getUserId(), savedMember.getStudyInfoId()));
    }

    @Test
    void 해당_유저가_스터디의_활동중인_스터디원인지_확인_테스트_스터디원이_아닐_경우() {
        // given
        Long userId = 1L;
        Long studyInfoId = 1L;

        StudyMember savedMember = studyMemberRepository.save(StudyMemberFixture.createStudyMemberResigned(userId, studyInfoId));

        // when
        assertFalse(studyMemberRepository.existsStudyMemberByUserIdAndStudyInfoId(savedMember.getUserId(), savedMember.getStudyInfoId()));
    }

    @Test
    void 해당_유저가_스터디장일_경우() {
        // given
        Long userId = 1L;
        Long studyInfoId = 1L;

        StudyMember savedMember = studyMemberRepository.save(StudyMemberFixture.createStudyMemberLeader(userId, studyInfoId));

        // when
        assertTrue(studyMemberRepository.isStudyLeaderByUserIdAndStudyInfoId(savedMember.getUserId(), savedMember.getStudyInfoId()));
    }

    @Test
    void 해당_유저가_스터디장이_아닐_경우() {
        // given
        Long userId = 1L;
        Long studyInfoId = 1L;

        StudyMember savedMember = studyMemberRepository.save(StudyMemberFixture.createDefaultStudyMember(userId, studyInfoId));

        // when
        assertFalse(studyMemberRepository.isStudyLeaderByUserIdAndStudyInfoId(savedMember.getUserId(), savedMember.getStudyInfoId()));
    }

}