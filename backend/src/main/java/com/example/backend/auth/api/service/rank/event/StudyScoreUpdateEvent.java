package com.example.backend.auth.api.service.rank.event;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudyScoreUpdateEvent {

    private Long studyInfoId;

    private double score;
}
