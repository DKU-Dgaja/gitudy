package com.example.backend.domain.define.study.StudyCategory;

import com.example.backend.domain.define.study.category.info.StudyCategory;

import java.security.SecureRandom;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class StudyCategoryFixture {
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final Random RANDOM = new SecureRandom();
    public static final int CATEGORY_SIZE = 3;
    private static final int CATEGORY_RANDOM_CHAR_SIZE = 3;

    public static StudyCategory createDefaultPublicStudyCategory(String name) {
        return StudyCategory.builder()
                .name(name)
                .build();
    }

    public static List<StudyCategory> createDefaultPublicStudyCategories(int size) {
        List<StudyCategory> studyCategories = IntStream.range(0, size)
                .mapToObj(i -> createDefaultPublicStudyCategory(generateRandomString(CATEGORY_RANDOM_CHAR_SIZE)))
                .collect(Collectors.toList());
        return studyCategories;
    }

    private static String generateRandomString(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int randomIndex = RANDOM.nextInt(CHARACTERS.length());
            char randomChar = CHARACTERS.charAt(randomIndex);
            sb.append(randomChar);
        }
        return sb.toString();
    }
}
