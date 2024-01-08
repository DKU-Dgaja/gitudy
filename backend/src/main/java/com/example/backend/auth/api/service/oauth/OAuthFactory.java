package com.example.backend.auth.api.service.oauth;

import com.example.backend.auth.api.service.oauth.adapter.OAuthAdapter;
import com.example.backend.auth.api.service.oauth.builder.OAuthURLBuilder;
import lombok.Builder;
import lombok.Getter;

@Getter
public class OAuthFactory {

    private OAuthURLBuilder oAuthURLBuilder;
    private OAuthAdapter oAuthAdapter;

    @Builder
    private OAuthFactory(OAuthURLBuilder oAuthURLBuilder, OAuthAdapter oAuthAdapter) {
        this.oAuthURLBuilder = oAuthURLBuilder;
        this.oAuthAdapter = oAuthAdapter;
    }
}
