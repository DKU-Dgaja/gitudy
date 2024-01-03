package com.example.backend.auth.config.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationProviderService implements AuthenticationProvider {
    // 사용자 세부 정보를 불러오는 작업을 위임할 UserDetailsService
    @Autowired
    private JpaUserDetailsService userDetailsService;

    // 암호 인증 작업을 위임할 PasswordEncoder
    @Autowired
    private BCryptPasswordEncoder encoder;

    // 인증 논리 구현
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        // UserDetailsService로 DB에서 사용자 세부 정보 획득
        CustomUserDetails user = userDetailsService.loadUserByUsername(username);

        // encoder로 암호 검증
        return checkPassword(user, password);
    }

    private Authentication checkPassword(CustomUserDetails user, String rawPassword) {
        // 사용자 입력 비밀번호와 DB 비밀번호의 인코딩이 일치하는지 확인
        if (encoder.matches(rawPassword, user.getPassword())) {
            System.out.println("로그인 성공!");
            // authenticated이 true인, 즉 인증된 Authentication(UsernamePasswordAuthenticationToken) 반환
            return new UsernamePasswordAuthenticationToken(user.getUsername(),
                    user.getPassword(),
                    user.getAuthorities());
        } else {
            throw new BadCredentialsException(">>> Security Exception: 비밀번호가 일치하지 않습니다.");
        }
    }

    @Override
    public boolean supports(Class<?> authClass) {
        // 인증할 객체의 클래스가 지정한 클래스 혹은 그 하위 클래스인지 확인
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authClass);
    }

}