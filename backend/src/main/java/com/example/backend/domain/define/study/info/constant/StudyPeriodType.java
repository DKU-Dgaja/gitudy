package com.example.backend.domain.define.study.info.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum StudyPeriodType {
    STUDY_PERIOD_EVERYDAY("매일"),
    STUDY_PERIOD_WEEK("매주 특정 요일"),
    STUDY_PERIOD_NONE("주기 없음");;

    private final String text;
}
