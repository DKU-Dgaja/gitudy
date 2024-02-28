package com.example.backend.study.api.service.member;


import com.example.backend.common.exception.ExceptionMessage;
import com.example.backend.common.exception.GitudyException;
import com.example.backend.common.exception.member.MemberException;
import com.example.backend.common.exception.user.UserException;
import com.example.backend.domain.define.account.user.User;
import com.example.backend.domain.define.account.user.repository.UserRepository;
import com.example.backend.domain.define.study.info.constant.StudyStatus;
import com.example.backend.domain.define.study.info.repository.StudyInfoRepository;
import com.example.backend.domain.define.study.member.StudyMember;
import com.example.backend.domain.define.study.member.repository.StudyMemberRepository;
import com.example.backend.study.api.controller.member.response.StudyMembersResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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


    // 스터디에 속한 스터디원들 조회
    public List<StudyMembersResponse> readStudyMembers(Long studyInfoId, StudyStatus studyStatus, User user) {

        List<StudyMember> studyMembers = studyMemberRepository.findByStudyInfoId(studyInfoId);

        // 공개 스터디인 경우
        if (studyStatus == StudyStatus.STUDY_PUBLIC) {
            return studyMembers.stream()
                    .map(member -> StudyMembersResponse.builder()
                            .userId(member.getId())
                            .role(member.getRole())
                            .status(member.getStatus())
                            .score(member.getScore())
                            .build())
                    .collect(Collectors.toList());

        } else if (studyStatus == StudyStatus.STUDY_PRIVATE) // 비공개 스터디인 경우
        {
            try {
                // 스터디원인지 확인
                isValidateStudyMember(user, studyInfoId);

            } catch (GitudyException e) {

                // 비공개 스터디이고 스터디원이 아닐때
                log.warn(">>>> {},{} : {} <<<<", studyInfoId, user.getId(), ExceptionMessage.STUDY_NOT_MEMBER.getText());
                throw new MemberException(ExceptionMessage.STUDY_NOT_MEMBER);
            }

            // 비공개 스터디이지만 스터디원인 경우
            return studyMembers.stream()
                .map(member -> StudyMembersResponse.builder()
                        .userId(member.getId())
                        .role(member.getRole())
                        .status(member.getStatus())
                        .score(member.getScore())
                        .build())
                .collect(Collectors.toList());

        } else { // 삭제 스터디인 경우

            log.warn(">>>> {} : {} <<<<", studyInfoId, ExceptionMessage.STUDY_NOT_FOUND.getText());
            throw new MemberException(ExceptionMessage.STUDY_NOT_FOUND);
        }

    }


    // Map<USER_ID, USER> 생성하는 메소드
    private static Map<Long, User> getUserMap(List<User> userList) {
        Map<Long, User> userMap = new HashMap<>();
        for (User user : userList) {
            userMap.put(user.getId(), user);
        }
        return userMap;
    }


}
