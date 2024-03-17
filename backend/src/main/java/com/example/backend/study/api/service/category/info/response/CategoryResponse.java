package com.example.backend.study.api.service.category.info.response;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
@Getter
@ToString
public class CategoryResponse {
    private Long id;
    private String name;

    @Builder
    public CategoryResponse(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}