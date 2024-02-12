package com.example.backend.domain.define.study.info;

import static com.example.backend.domain.define.study.info.constant.StudyStatus.STUDY_PUBLIC;

public class StudyInfoFixture {

    public static StudyInfo createDefaultPublicStudyInfo(Long userId) {
        return StudyInfo.builder()
                .userId(userId)
                .topic("토픽")
                .status(STUDY_PUBLIC)
                .build();
    }
}
