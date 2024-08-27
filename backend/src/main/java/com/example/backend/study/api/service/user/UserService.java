package com.example.backend.study.api.service.user;


import com.example.backend.common.exception.ExceptionMessage;
import com.example.backend.common.exception.user.UserException;
import com.example.backend.domain.define.account.user.User;
import com.example.backend.domain.define.account.user.repository.UserRepository;
import com.example.backend.domain.define.study.member.StudyMember;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User findUserByStudyMemberOrThrowException(StudyMember studyMember) {
        return userRepository.findById(studyMember.getUserId())
                .orElseThrow(() -> {
                    log.warn(">>>> {} : {} <<<<", studyMember.getUserId(), ExceptionMessage.USER_NOT_FOUND);
                    return new UserException(ExceptionMessage.USER_NOT_FOUND);
                });
    }

    public User findUserByPlatformIdAndPlatformTypeOrThrowException(User user) {
        return userRepository.findByPlatformIdAndPlatformType(user.getPlatformId(), user.getPlatformType())
                .orElseThrow(() -> {
                    log.warn(">>>> {},{} : {} <<<<", user.getPlatformId(), user.getPlatformType(), ExceptionMessage.USER_NOT_FOUND);
                    return new UserException(ExceptionMessage.USER_NOT_FOUND);
                });
    }

    public User findUserByIdOrThrowException(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn(">>>> {} : {} <<<<", userId, ExceptionMessage.USER_NOT_FOUND);
                    return new UserException(ExceptionMessage.USER_NOT_FOUND);
                });
    }


    // isPushAlarmYn가 true인 사용자의 ID 목록을 반환하는 메서드
    public List<Long> findIsPushAlarmYsByIdsOrThrowException(List<Long> userIds) {

        return userRepository.findAllById(userIds).stream()
                .filter(User::isPushAlarmYn)
                .map(User::getId)
                .toList();
    }

}