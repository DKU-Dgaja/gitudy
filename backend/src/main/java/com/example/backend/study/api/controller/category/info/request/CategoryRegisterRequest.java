package com.example.backend.study.api.controller.category.info.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryRegisterRequest {

    @NotBlank(message = "카테고리 내용은 공백일 수 없습니다.")
    @Size(max = 10, message = "카테고리 내용은 10자를 넘을 수 없습니다.")
    private String name;               // 카테고리 name
}