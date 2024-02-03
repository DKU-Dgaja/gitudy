package com.example.backend.common.validation;

import com.example.backend.domain.define.account.user.SocialInfo;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import static com.example.backend.common.utils.EmailValidator.isValidEmail;

public class SocialInfoValidator implements ConstraintValidator<ValidSocialInfo, SocialInfo> {
    public boolean isValid(SocialInfo socialInfo, ConstraintValidatorContext context) {
        // 각 필드가 올바른 이메일 형식인지 확인
        boolean githubLinkValid = isValidEmail(socialInfo.getGithubLink());
        boolean blogLinkValid = isValidEmail(socialInfo.getBlogLink());
        boolean linkedInLinkValid = isValidEmail(socialInfo.getLinkedInLink());

        // 모든 필드가 올바른 이메일 형식이어야 유효성 검사 통과
        return githubLinkValid && blogLinkValid && linkedInLinkValid;
    }
}
