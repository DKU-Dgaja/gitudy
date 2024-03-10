package com.example.backend.study.api.controller.convention.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StudyConventionUpdateRequest {

    @NotBlank(message = "컨벤션 이름은 공백일 수 없습니다.")
    @Size(max = 20, message = "이름 20자 이내")
    private String name;   // 컨벤션 이름

    @Size(max = 50, message = "설명 50자 이내")
    private String description; // 컨벤션 설명

    @NotBlank(message = "컨벤션 내용은 공백일 수 없습니다.")
    @Size(max = 40, message = "내용 40자 이내")
    private String content;  // 컨벤션 내용(정규식)

    @Builder.Default
    private boolean active = true; // 컨벤션 적용 여부
}
