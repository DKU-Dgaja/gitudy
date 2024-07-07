package com.example.backend.domain.define.account.user;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SocialInfo {

    @Column(name = "GITHUB_LINK")
    private String githubLink;

    @Column(name = "BLOG_LINK")
    private String blogLink;

    @Column(name = "LINKEDIN_LINK")
    private String linkedInLink;

}
