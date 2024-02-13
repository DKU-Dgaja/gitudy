package com.example.backend.study.api.controller.commit;

import com.example.backend.common.response.JsonResult;
import com.example.backend.study.api.service.commit.StudyCommitService;
import com.example.backend.study.api.service.commit.response.CommitInfoResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/commits")
public class StudyCommitController {
    private final StudyCommitService studyCommitService;

    @ApiResponse(responseCode = "200", description = "커밋 상세 페이지 조회 성공",
            content = @Content(schema = @Schema(implementation = CommitInfoResponse.class)))
    @GetMapping("{commitId}")
    public JsonResult<?> commitDetails(@PathVariable(name = "commitId") Long commitId) {

        return JsonResult.successOf(studyCommitService.getCommitDetailsById(commitId));
    }
}
