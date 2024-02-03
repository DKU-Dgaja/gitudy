package com.example.backend.common.utils;

import java.util.regex.Pattern;

public class EmailValidator {

    private static final Pattern EMAIL_REGEX_PATTERN = Pattern.compile(
            "^([a-zA-Z0-9_\\-\\.]+)@((\\[a-zA-Z0-9\\-\\_\\.]+)|([a-zA-Z0-9\\-\\.]+))\\.([a-zA-Z]{2,5})$"
    );

    public static boolean isValidEmail(String email) {
        return email == null || EMAIL_REGEX_PATTERN.matcher(email).matches();
    }
}

