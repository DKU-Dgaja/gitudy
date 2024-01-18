package com.example.backend.external.clients.oauth.google.response;


import lombok.Getter;
import lombok.NoArgsConstructor;

/*
    Google에 Access Token을 요청 후 반환받을 DTO
 */
@Getter
@NoArgsConstructor
public class GoogleTokenResponse {
    private String access_token;


    public GoogleTokenResponse(String access_token) {
        this.access_token = access_token;
    }

}