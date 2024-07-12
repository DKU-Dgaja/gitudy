package com.example.backend.external.config;

import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class GitHubConfig {
    @Value("${github.api.token}")
    private String token;

    @Bean
    public GitHub gitHub() throws IOException {
        return new GitHubBuilder().withOAuthToken(token).build();
    }
}
