
package com.example.backend.study.api.event.controller;


import com.example.backend.auth.api.controller.auth.response.UserInfoResponse;
import com.example.backend.auth.api.service.auth.AuthService;
import com.example.backend.domain.define.account.user.User;
import com.example.backend.study.api.event.controller.response.UserNoticeList;
import com.example.backend.study.api.event.service.NoticeService;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/notice")
public class NoticeController {

    private final NoticeService noticeService;
    private final AuthService authService;

    // 알림목록 조회
    @ApiResponse(responseCode = "200", description = "알림 목록 조회 성공", content = @Content(schema = @Schema(implementation = UserNoticeList.class)))
    @GetMapping("")
    public ResponseEntity<List<UserNoticeList>> readNoticeList(@AuthenticationPrincipal User user,
                                                               @RequestParam(name = "cursorTime", required = false) LocalDateTime time,
                                                               @Min(value = 1, message = "Limit cannot be less than 1") @RequestParam(name = "limit", defaultValue = "5") Long limit) {

        UserInfoResponse userInfo = authService.findUserInfo(user);
        return ResponseEntity.ok().body(noticeService.ReadNoticeList(userInfo, time, limit));
    }


    // 특정알림 삭제
    @ApiResponse(responseCode = "200", description = "특정 알림 삭제 성공")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotice(@AuthenticationPrincipal User user,
                                          @PathVariable(name = "id") String id) {

        authService.findUserInfo(user);

        noticeService.DeleteNotice(id);

        return ResponseEntity.ok().build();
    }

    // 알림 모두 삭제
    @ApiResponse(responseCode = "200", description = "모든 알림 삭제 성공")
    @DeleteMapping("")
    public ResponseEntity<Void> deleteNoticeAll(@AuthenticationPrincipal User user) {

        UserInfoResponse userInfo = authService.findUserInfo(user);

        noticeService.DeleteNoticeAll(userInfo.getUserId());

        return ResponseEntity.ok().build();
    }
}
