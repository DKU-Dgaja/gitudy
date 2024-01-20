package com.example.backend.external.clients.oauth.kakao;


import com.example.backend.external.annotation.ExternalClients;
import com.example.backend.external.clients.oauth.kakao.response.KakaoTokenResponse;
import org.springframework.http.MediaType;
import org.springframework.web.service.annotation.PostExchange;

import java.net.URI;

/*
    AccessToken을 얻기 위해 설정한 token URI로 POST 요청을 보낸다.
 */
@ExternalClients(baseUrl = "oauth2.provider.kakao.token-uri")
public interface KakaoTokenClients {

    // 요청의 content Type: FORM_URLENCODED 형식 ex) key1=value1&key2=value2
    @PostExchange(contentType = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public KakaoTokenResponse getToken(URI uri);
}
