package com.example.backend.auth.api.service.rank.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class UserRankingResponse {

    private int score;

    private Long ranking;
}
