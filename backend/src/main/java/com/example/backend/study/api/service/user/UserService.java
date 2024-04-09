package com.example.backend.study.api.service.user;


import com.example.backend.common.exception.ExceptionMessage;
import com.example.backend.common.exception.event.EventException;
import com.example.backend.common.exception.user.UserException;
import com.example.backend.domain.define.account.user.User;
import com.example.backend.domain.define.account.user.repository.UserRepository;
import com.example.backend.domain.define.study.member.StudyMember;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

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

        // users 비어있는경우 예외처리
        if (userIds == null || userIds.isEmpty()) {
            log.warn(">>>> {} : {} <<<<", userIds, ExceptionMessage.USER_NOT_FOUND);
            throw new EventException(ExceptionMessage.USER_NOT_FOUND);
        }

        return userRepository.findAllById(userIds).stream()
                .filter(User::isPushAlarmYn)
                .map(User::getId)
                .toList();
    }

}