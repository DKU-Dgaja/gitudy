package com.example.backend.external.clients.oauth.kakao;

import com.example.backend.external.annotation.ExternalClients;
import com.example.backend.external.clients.oauth.kakao.response.KakaoProfileResponse;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.service.annotation.GetExchange;

/*
    사용자 정보를 얻기 위해 설정한 profile 요청 URI로 GET 요청을 보낸다.
 */
@ExternalClients(baseUrl = "https://kapi.kakao.com/v2/user/me")
public interface KakaoProfileClients {

    @GetExchange
    public KakaoProfileResponse getProfile(@RequestHeader(value = "Authorization") String header);

}
