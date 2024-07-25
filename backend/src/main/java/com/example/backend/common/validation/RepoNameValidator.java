package com.example.backend.common.validation;

import com.example.backend.common.exception.ExceptionMessage;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class RepoNameValidator implements ConstraintValidator<ValidRepoName, String> {

    // 연속된 특수 문자(밑줄, 하이픈, 마침표)가 있는지 검증
    private static final String DOUBLE_SPECIAL_CHAR_REGEX = ".*[_.-]{2}.*";

    // 유효한 문자(영문자, 숫자, 마침표, 밑줄, 하이픈)만 포함하는지 검증
    private static final String VALID_CHARS_REGEX = "[a-zA-Z0-9._-]+";

    // 밑줄, 하이픈, 마침표로 끝나는지 검증
    private static final String ENDING_SPECIAL_CHAR_REGEX = ".*[_.-]$";

    @Override
    public boolean isValid(String repoName, ConstraintValidatorContext context) {
        boolean isValid = true;

        context.disableDefaultConstraintViolation();

        if (repoName == null || repoName.isEmpty()) {
            context.buildConstraintViolationWithTemplate(ExceptionMessage.STUDY_REPOSITORY_NAME_EMPTY.getText())
                    .addConstraintViolation();
            isValid = false;
        }

        if (repoName.matches(DOUBLE_SPECIAL_CHAR_REGEX)) {
            context.buildConstraintViolationWithTemplate(ExceptionMessage.STUDY_REPOSITORY_NAME_CONSECUTIVE_SPECIAL_CHARS.getText())
                    .addConstraintViolation();
            isValid = false;
        }

        if (!repoName.matches(VALID_CHARS_REGEX)) {
            context.buildConstraintViolationWithTemplate(ExceptionMessage.STUDY_REPOSITORY_NAME_INVALID_CHARS.getText())
                    .addConstraintViolation();
            isValid = false;
        }

        if (repoName.matches(ENDING_SPECIAL_CHAR_REGEX)) {
            context.buildConstraintViolationWithTemplate(ExceptionMessage.STUDY_REPOSITORY_NAME_ENDS_WITH_SPECIAL_CHAR.getText())
                    .addConstraintViolation();
            isValid = false;
        }

        return isValid;
    }
}
