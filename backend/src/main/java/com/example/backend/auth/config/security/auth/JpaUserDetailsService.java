package com.example.backend.auth.config.security.auth;

import com.example.backend.common.exception.ExceptionMessage;
import com.example.backend.domain.define.user.User;
import com.example.backend.domain.define.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.function.Supplier;

@Slf4j
@Service
public class JpaUserDetailsService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 사용자 인스턴스(Optional) 찾아서 반환
        User u = userRepository.findByEmail(username).orElseThrow(() -> {
            log.error(">>>> [ SECURITY ERROR: {} ]", ExceptionMessage.SECURITY_USER_NOT_FOUND.getText());
            throw new SecurityException(ExceptionMessage.SECURITY_USER_NOT_FOUND.getText());
        });

        return u;
    }
}
