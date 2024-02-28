package com.example.backend.study.api.service.member;


import com.example.backend.common.exception.ExceptionMessage;
import com.example.backend.common.exception.member.MemberException;
import com.example.backend.common.exception.user.UserException;
import com.example.backend.domain.define.account.user.User;
import com.example.backend.domain.define.account.user.repository.UserRepository;
import com.example.backend.domain.define.study.member.repository.StudyMemberRepository;
import com.example.backend.study.api.controller.member.response.StudyMembersResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StudyMemberService {

    private final UserRepository userRepository;
    private final StudyMemberRepository studyMemberRepository;

    // 스터디장 검증 메서드
    public void isValidateStudyLeader(User userPrincipal, Long studyInfoId) {

        // platformId와 platformType을 이용하여 User 객체 조회
        User user = userRepository.findByPlatformIdAndPlatformType(userPrincipal.getPlatformId(), userPrincipal.getPlatformType()).orElseThrow(() -> {
            log.warn(">>>> {},{} : {} <<<<", userPrincipal.getPlatformId(), userPrincipal.getPlatformType(), ExceptionMessage.USER_NOT_FOUND);
            return new UserException(ExceptionMessage.USER_NOT_FOUND);
        });

        // 스터디장인지 확인
        if (!studyMemberRepository.isStudyLeaderByUserIdAndStudyInfoId(user.getId(), studyInfoId)) {
            throw new MemberException(ExceptionMessage.STUDY_MEMBER_NOT_LEADER);
        }

    }

    // 스터디 멤버인지 검증
    public void isValidateStudyMember(User userPrincipal, Long studyInfoId) {

        // platformId와 platformType을 이용하여 User 객체 조회
        User user = userRepository.findByPlatformIdAndPlatformType(userPrincipal.getPlatformId(), userPrincipal.getPlatformType()).orElseThrow(() -> {
            log.warn(">>>> {},{} : {} <<<<", userPrincipal.getPlatformId(), userPrincipal.getPlatformType(), ExceptionMessage.USER_NOT_FOUND);
            return new UserException(ExceptionMessage.USER_NOT_FOUND);
        });

        // 스터디 멤버인지확인
        if (!studyMemberRepository.existsStudyMemberByUserIdAndStudyInfoId(user.getId(), studyInfoId)) {
            throw new MemberException(ExceptionMessage.STUDY_NOT_MEMBER);
        }
    }


    // 스터디에 속한 스터디원 조회 (기여도별)
    public List<StudyMembersResponse> readStudyMembers(Long studyInfoId) {

        return studyMemberRepository.findStudyMembersByStudyInfoIdOrderByScore(studyInfoId);
    }

}
