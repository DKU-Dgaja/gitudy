package com.example.backend.domain.define.rank;

import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.redis.core.RedisHash;

@Getter
@ToString
@RedisHash(value = "study_ranking")
@NoArgsConstructor
@Builder
public class StudyRanking {

    @Id
    private Long studyInfoId;

    private int score;

    public StudyRanking(Long studyInfoId, int score) {
        this.studyInfoId = studyInfoId;
        this.score = score;
    }
}