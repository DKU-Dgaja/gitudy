package com.example.backend.domain.define.account.social;

import com.example.backend.domain.define.BaseEntity;
import com.example.backend.domain.define.account.social.constant.SocialType;
import com.example.backend.domain.define.account.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity(name = "SOCIAL_ACCOUNT_LINK")
public class SocialAccountLink extends BaseEntity {
    @Id
    @Column(name = "SOCIAL_ACCOUNT_LINK_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;                                    // 아이디

    @Column(name = "USER_ID", nullable = false)
    private Long userid;                                // 사용자 ID

    @Enumerated(EnumType.STRING)
    @Column(name = "SOCIAL_TYPE")
    private SocialType socialType;                      // 소셜 플랫폼 타입

    @Column(name = "LINK", nullable = false)
    private String link;                                // 소셜 링크 url

    @Builder
    public SocialAccountLink(Long userid, SocialType socialType, String link) {
        this.userid = userid;
        this.socialType = socialType;
        this.link = link;
    }
}
