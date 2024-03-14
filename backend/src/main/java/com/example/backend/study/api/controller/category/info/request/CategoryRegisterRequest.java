package com.example.backend.study.api.controller.category.info.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryRegisterRequest {
    private Long userId;             // 유저 id
    private String name;               // 카테고리 name
}