package com.example.backend.domain.define.study.commit.constant;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LikeCount {
    private static final long DEFAULT_LIKE_COUNT = 0L;

    @Column(name = "LIKE_COUNT", nullable = false)
    private Long likeCount;

    public static LikeCount createDefault() {
        return new LikeCount(DEFAULT_LIKE_COUNT);
    }

    // 좋아요 요청시
    public void like() {
        this.likeCount++;
    }
}
