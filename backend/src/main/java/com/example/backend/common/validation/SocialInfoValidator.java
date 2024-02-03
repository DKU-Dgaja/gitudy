package com.example.backend.common.validation;

import com.example.backend.domain.define.account.user.SocialInfo;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class SocialInfoValidator implements ConstraintValidator<ValidSocialInfo, SocialInfo> {

    private static final Pattern EMAIL_REGEX_PATTERN = Pattern.compile(
            "^([a-zA-Z0-9_\\-\\.]+)@((\\[a-zA-Z0-9\\-\\_\\.]+)|([a-zA-Z0-9\\-\\.]+))\\.([a-zA-Z]{2,5})$"
    );

    public boolean isValid(SocialInfo socialInfo, ConstraintValidatorContext context) {
        // null이면 검증 패스
        if (socialInfo == null) return true;

        // 각 필드가 올바른 이메일 형식인지 확인
        boolean githubLinkValid = isValidEmail(socialInfo.getGithubLink());
        boolean blogLinkValid = isValidEmail(socialInfo.getBlogLink());
        boolean linkedInLinkValid = isValidEmail(socialInfo.getLinkedInLink());

        // 모든 필드가 올바른 이메일 형식이어야 유효성 검사 통과
        return githubLinkValid && blogLinkValid && linkedInLinkValid;
    }

    private boolean isValidEmail(String email) {
        return email == null || EMAIL_REGEX_PATTERN.matcher(email).matches();
    }
}
