package com.example.backend.domain.define.user;

import com.example.backend.domain.define.user.constant.UserPlatformType;
import com.example.backend.domain.define.user.constant.UserRole;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)              // 외부에서 객체 생성 못하도록 제한
@Entity(name = "USERS")                                         // "USER"는 예약어 출동 발생하므로 "USERS"로 설정
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USER_ID")
    private Long id;                                            // 아이디 (식별자)

    @Column(name = "PLATFORM_ID")
    private String platformId;                                  // 플랫폼 아이디 (플랫폼 식별자)

    @Enumerated(EnumType.STRING)
    @Column(name = "PLATFORM_TYPE")
    @ColumnDefault(value = "'KAKAO'")
    private UserPlatformType platformType;                      // 플랫폼 타입

    @Enumerated(EnumType.STRING)
    @Column(name = "ROLE")
    @ColumnDefault(value = "'UNAUTH'")
    private UserRole role;                                      // 유저 상태 정보(역할)

    @Column(name = "NAME")
    private String name;                                        // 이름

    @Column(name = "EMAIL", unique = true)
    private String email;                                       // 이메일

    @Column(name = "PHONE_NUMBER", unique = true)
    private String phoneNumber;                                 // 전화번호

    @Column(name = "PROFILE_IMAGE_URL")
    private String profileImageUrl;                             // 프로필 사진

    @Column(name = "PUSH_ALARM_YN")
    private boolean pushAlarmYn = false;                        // 알림 수신 동의 여부

    @Builder
    private User(String platformId,
                 UserPlatformType platformType,
                 UserRole role,
                 String name,
                 String email,
                 String phoneNumber,
                 String profileImageUrl,
                 boolean pushAlarmYn) {
        this.platformId = platformId;
        this.platformType = platformType;
        this.role = role;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.profileImageUrl = profileImageUrl;
        this.pushAlarmYn = pushAlarmYn;
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
        return email;
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
