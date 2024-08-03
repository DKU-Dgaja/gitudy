package com.example.backend.domain.define.study.commit.constant;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class LikeCountTest {
    @Test
    void 좋아요_수행_테스트() {
        // given
        LikeCount likeCount = LikeCount.createDefault();

        // when
        likeCount.like();

        // then
        assertEquals(1, likeCount.getLikeCount());
    }

}