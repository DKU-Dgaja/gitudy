package com.example.backend.domain.define.account.user.repository;

import com.example.backend.domain.define.account.user.User;
import com.example.backend.domain.define.account.user.constant.UserPlatformType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, UserRepositoryCustom {

    // select u from user u where u.platformId = :platformId and u.platformType = :platformType
    Optional<User> findByPlatformIdAndPlatformType(String platformId, UserPlatformType platformType);

    // 같은 Name(닉네임)이 있는지 판별한다.
    boolean existsByName(String name);

    Optional<User> findByGithubId(String githubId);

    List<User> findByGithubIdIn(List<String> githubIdList);

}
