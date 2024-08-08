package com.example.backend.study.api.service.category.info.response;

import com.example.backend.domain.define.study.category.info.StudyCategory;
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

    public static CategoryResponse of(StudyCategory category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }
}