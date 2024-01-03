package com.example.backend.auth.config.security;

import com.example.backend.domain.define.user.User;
import com.example.backend.domain.define.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.function.Supplier;

@Service
public class JpaUserDetailsService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public CustomUserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 예외 인스턴스를 만들기 위한 공급자 선언
        Supplier<UsernameNotFoundException> exceptionSupplier = () -> new UsernameNotFoundException(">>>> Security Exception: 해당 사용자를 찾을 수 없습니다.");

        // 사용자 인스턴스(Optional) 찾아서 반환
        User u = userRepository.findUserByName(username).orElseThrow(exceptionSupplier);

        // 찾은 사용자를 UserDetails 타입으로 데코레이트해서 반환
        return new CustomUserDetails(u);
    }
}
