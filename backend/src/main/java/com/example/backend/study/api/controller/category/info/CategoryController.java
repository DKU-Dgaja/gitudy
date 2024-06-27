package com.example.backend.study.api.controller.category.info;

import com.example.backend.auth.api.service.auth.AuthService;
import com.example.backend.common.response.JsonResult;
import com.example.backend.domain.define.account.user.User;
import com.example.backend.study.api.controller.category.info.request.CategoryRegisterRequest;
import com.example.backend.study.api.controller.category.info.request.CategoryUpdateRequest;
import com.example.backend.study.api.controller.category.info.response.CategoryListAndCursorIdxResponse;
import com.example.backend.study.api.service.category.info.CategoryService;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/category")
public class CategoryController {
    private final AuthService authService;
    private final CategoryService categoryService;

    @ApiResponse(responseCode = "200", description = "카테고리 등록 성공")
    @PostMapping("")
    public JsonResult<?> registerCategory(@AuthenticationPrincipal User user,
                                          @Valid @RequestBody CategoryRegisterRequest categoryRegisterRequest) {
        authService.findUserInfo(user);
        categoryService.registerCategory(categoryRegisterRequest);
        return JsonResult.successOf("Category Register Success.");
    }

    @ApiResponse(responseCode = "200", description = "카테고리 수정 성공")
    @PatchMapping("/{categoryId}")
    public JsonResult<?> updateStudyComment(@AuthenticationPrincipal User user,
                                            @PathVariable(name = "categoryId") Long categoryId,
                                            @Valid @RequestBody CategoryUpdateRequest categoryUpdateRequest) {
        authService.findUserInfo(user);
        categoryService.updateCategory(categoryUpdateRequest, categoryId);
        return JsonResult.successOf("Category update Success");
    }

    @ApiResponse(responseCode = "200", description = "카테고리 삭제 성공")
    @DeleteMapping("/{categoryId}")
    public JsonResult<?> deleteStudyComment(@AuthenticationPrincipal User user,
                                            @PathVariable(name = "categoryId") Long categoryId
    ) {
        authService.findUserInfo(user);
        categoryService.deleteCategory(categoryId);
        return JsonResult.successOf("Category deleted successfully");
    }

    @ApiResponse(responseCode = "200",
            description = "카테고리 조회 성공",
            content = @Content(schema = @Schema(implementation = CategoryListAndCursorIdxResponse.class)))
    @GetMapping("/{studyInfoId}")
    public JsonResult<?> StudyCommentList(@AuthenticationPrincipal User user,
                                          @PathVariable(name = "studyInfoId") Long studyInfoId,
                                          @Min(value = 0, message = "Cursor index cannot be negative") @RequestParam(name = "cursorIdx") Long cursorIdx,
                                          @Min(value = 1, message = "Limit cannot be less than 1") @RequestParam(name = "limit", defaultValue = "5") Long limit) {

        authService.findUserInfo(user);
        CategoryListAndCursorIdxResponse response = categoryService.selectCategoryList(studyInfoId, cursorIdx, limit);

        return JsonResult.successOf(response);
    }
}