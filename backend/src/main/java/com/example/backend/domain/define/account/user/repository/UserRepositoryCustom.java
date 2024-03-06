package com.example.backend.domain.define.account.user.repository;

import com.example.backend.domain.define.account.user.User;

import java.util.List;

public interface UserRepositoryCustom {

    // StudyMember Id 리스트를 통해 스터디들의 모든 유저을 조회한다.
    List<User> findUserListByStudyMemberIdList(List<Long> studyMemberIdList);
}
