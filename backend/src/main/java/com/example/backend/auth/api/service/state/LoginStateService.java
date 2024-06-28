package com.example.backend.auth.api.service.state;

import com.example.backend.common.exception.ExceptionMessage;
import com.example.backend.common.exception.state.LoginStateException;
import com.example.backend.domain.define.state.LoginState;
import com.example.backend.domain.define.state.LoginStateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@RequiredArgsConstructor
public class LoginStateService {

    private final LoginStateRepository loginStateRepository;

    // Login State 생성
    public String generateLoginState() {
        LoginState loginState = LoginState.builder()
                .build();
        LoginState savedLoginState = loginStateRepository.save(loginState);
        log.info(">>>> [ Login State 생성 ]  {}", savedLoginState);

        return savedLoginState.getState();
    }

    // Login State 검증
    public boolean isValidLoginState(String loginState) {

        // 객체를 Redis에서 조회후 없는 경우 예외 처리
        LoginState findLoginState = loginStateRepository.findById(loginState)
                .orElseThrow(() -> {
                    log.warn(">>>> {} : {}", loginState, ExceptionMessage.LOGINSTATE_NOT_FOUND);
                    throw new LoginStateException(ExceptionMessage.LOGINSTATE_NOT_FOUND);
                });

        // 사용한 LoginState는 삭제
        loginStateRepository.deleteById(loginState);

        return true;
    }
}