package com.example.backend.study.api.controller.category.info.response;

import com.example.backend.study.api.service.category.info.response.CategoryResponse;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class CategoryListAndCursorIdxResponse {
    private List<CategoryResponse> CategoryResponseList;
    private Long cursorIdx;

    @Builder
    public CategoryListAndCursorIdxResponse(List<CategoryResponse> categoryNames, Long cursorIdx) {
        this.CategoryResponseList = categoryNames;
        this.cursorIdx = cursorIdx;
    }

    public void getNextCursorIdx() {
        cursorIdx = CategoryResponseList == null || CategoryResponseList.isEmpty() ?
                0L : CategoryResponseList.get(CategoryResponseList.size() - 1).getId();
    }
}