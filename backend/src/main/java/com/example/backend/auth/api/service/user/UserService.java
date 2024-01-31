package com.example.backend.auth.api.service.user;

import com.example.backend.domain.define.account.user.User;
import com.example.backend.domain.define.account.user.constant.UserPlatformType;
import com.example.backend.domain.define.account.user.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getUserByPlatform(String platformId, UserPlatformType platformType) {

        return userRepository.findByPlatformIdAndPlatformType(platformId, platformType).orElse(null);
    }
}
