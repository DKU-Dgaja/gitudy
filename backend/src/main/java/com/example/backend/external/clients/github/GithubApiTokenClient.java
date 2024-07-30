package com.example.backend.external.clients.github;

import com.example.backend.external.annotation.ExternalClients;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.service.annotation.PatchExchange;

@ExternalClients(baseUrl = "https://api.github.com")
public interface GithubApiTokenClient {

    @PatchExchange("/applications/{clientId}/token")
    String resetGithubApiToken(
            @PathVariable("clientId") String clientId,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
            @RequestHeader("X-GitHub-Api-Version") String apiVersion,
            @RequestHeader(HttpHeaders.CONTENT_TYPE) String contentType,
            @RequestHeader(HttpHeaders.ACCEPT) String accept,
            @RequestBody String requestBody
    );
}
