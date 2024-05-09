
package com.example.backend.study.api.event.controller;


import com.example.backend.auth.api.controller.auth.response.UserInfoResponse;
import com.example.backend.auth.api.service.auth.AuthService;
import com.example.backend.common.response.JsonResult;
import com.example.backend.domain.define.account.user.User;
import com.example.backend.study.api.event.service.NoticeService;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/notice")
public class NoticeController {

    private final NoticeService noticeService;
    private final AuthService authService;

    // 알림목록 조회

    @GetMapping("")
    public JsonResult<?> readNoticeList(@AuthenticationPrincipal User user,
                                        @RequestParam(name = "cursorTime", required = false) LocalDateTime time,
                                        @Min(value = 1, message = "Limit cannot be less than 1") @RequestParam(name = "limit", defaultValue = "5") Long limit) {

        UserInfoResponse userInfo = authService.findUserInfo(user);
        return JsonResult.successOf(noticeService.ReadNoticeList(userInfo, time, limit));
    }

}
