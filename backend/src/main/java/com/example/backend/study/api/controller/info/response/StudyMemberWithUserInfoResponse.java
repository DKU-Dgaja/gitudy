package com.example.backend.study.api.controller.info.response;

import com.example.backend.study.api.service.info.response.UserNameAndProfileImageResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudyMemberWithUserInfoResponse {
    private Long studyInfoId;

    private UserNameAndProfileImageResponse userNameAndProfileImageResponseList;
}
