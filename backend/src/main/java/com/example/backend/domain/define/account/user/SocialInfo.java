package com.example.backend.domain.define.account.user;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.Email;
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

    @Email
    @Column(name = "GITHUB_LINK")
    private String githubLink;

    @Email
    @Column(name = "BLOG_LINK")
    private String blogLink;

    @Email
    @Column(name = "LINKEDIN_LINK")
    private String linkedInLink;

}
