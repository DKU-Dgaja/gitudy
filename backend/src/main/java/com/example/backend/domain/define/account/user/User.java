package com.example.backend.domain.define.account.user;

import com.example.backend.domain.define.BaseEntity;
import com.example.backend.domain.define.account.user.constant.UserPlatformType;
import com.example.backend.domain.define.account.user.constant.UserRole;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
@DynamicInsert
@NoArgsConstructor(access = AccessLevel.PROTECTED)              // 외부에서 객체 생성 못하도록 제한
@Entity(name = "USERS")                                         // "USER"는 예약어 출동 발생하므로 "USERS"로 설정
@Table(name = "USERS", uniqueConstraints = {
        @UniqueConstraint(
                name = "PLATFORM_ID_AND_PLATFORM_TYPE_UNIQUE",
                columnNames = {"PLATFORM_ID", "PLATFORM_TYPE"}
        )
})
public class User extends BaseEntity implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USER_ID")
    private Long id;                                            // 아이디 (식별자)

    @Column(name = "PLATFORM_ID")
    private String platformId;                                  // 플랫폼 아이디 (플랫폼 식별자)

    @Enumerated(EnumType.STRING)
    @Column(name = "PLATFORM_TYPE")
    @ColumnDefault(value = "'GITHUB'")
    private UserPlatformType platformType;                      // 플랫폼 타입

    @Enumerated(EnumType.STRING)
    @Column(name = "ROLE")
    @ColumnDefault(value = "'UNAUTH'")
    private UserRole role;                                      // 유저 상태 정보(역할)

    @Embedded
    private SocialInfo socialInfo;                              // 소셜 정보

    @Column(name = "GITHUB_ID")
    private String githubId;                                    // 깃허브 아이디

    @Column(name = "NAME")
    private String name;                                        // 이름

    @Column(name = "PROFILE_IMAGE_URL")
    private String profileImageUrl;                             // 프로필 사진

    @Column(name = "PUSH_ALARM_YN")
    private boolean pushAlarmYn = false;                        // 알림 수신 동의 여부

    @Column(name = "PROFILE_PUBLIC_YN")
    private boolean profilePublicYn = true;                     // 프로필 공개 여부

    @Column(name = "SCORE")
    private int score = 10;                                      // 사용자 활동 점수

    @Column(name = "POINT")
    private int point = 0;                                      // 사용자 포인트

    @Column(name = "WITHDRAWAL_REASON")
    private String withdrawalReason;                            // 탈퇴 이유

    @Builder
    public User(String platformId, UserPlatformType platformType, UserRole role, SocialInfo socialInfo, String githubId, String name, String profileImageUrl, boolean pushAlarmYn, boolean profilePublicYn, int score, int point, String withdrawalReason) {
        this.platformId = platformId;
        this.platformType = platformType;
        this.role = role;
        this.socialInfo = socialInfo;
        this.githubId = githubId;
        this.name = name;
        this.profileImageUrl = profileImageUrl;
        this.pushAlarmYn = pushAlarmYn;
        this.profilePublicYn = profilePublicYn;
        this.score = score;
        this.point = point;
        this.withdrawalReason = withdrawalReason;
    }

    // 회원가입 (UNAUTH -> USER)
    public void updateRegister(String name, boolean pushAlarmYn) {
        this.role = UserRole.USER;
        this.name = name;
        this.pushAlarmYn = pushAlarmYn;
    }

    // 회원 정보 수정 메서드
    public void updateUser(String name, String profileImageUrl, boolean profilePublicYn, SocialInfo socialInfo) {
        this.name = name;
        this.profileImageUrl = profileImageUrl;
        this.profilePublicYn = profilePublicYn;
        this.socialInfo = socialInfo;
    }

    // 푸시 알람 여부 수정 메서드
    public void updatePushAlarmYn(boolean pushAlarmEnable) {
        this.pushAlarmYn = pushAlarmEnable;
    }

    public void withdrawal(String reason) {
        this.role = UserRole.WITHDRAW;
        this.name = "탈퇴한 사용자";
        this.platformId = "DELETED" + this.getId();
        this.socialInfo = null;
        this.githubId = null;
        this.profileImageUrl = null;
        this.score = 0;
        this.point = 0;
        this.withdrawalReason = reason;
    }

    // Score 업데이트 메서드
    public void addUserScore(int score) {
        this.score = Math.max(0, this.score + score);
    }

    // Spring Security UserDetails Area
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getPassword() {
        return platformId;
    }

    @Override
    public String getUsername() {
        return platformId + "_" + platformType.name();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
