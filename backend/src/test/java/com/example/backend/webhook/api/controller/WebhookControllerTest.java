package com.example.backend.webhook.api.controller;

import com.example.backend.MockTestConfig;
import com.example.backend.auth.api.service.jwt.JwtService;
import com.example.backend.common.utils.TokenUtil;
import com.example.backend.domain.define.account.user.User;
import com.example.backend.webhook.api.controller.request.WebhookPayload;
import com.example.backend.webhook.api.service.WebhookService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static com.example.backend.auth.config.fixture.UserFixture.generateAdminUser;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class WebhookControllerTest extends MockTestConfig {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WebhookService webhookService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtService jwtService;

    @Test
    void 웹훅_처리_컨트롤러_성공_테스트() throws Exception {
        // given
        User admin = generateAdminUser();

        Map<String, String> map = TokenUtil.createTokenMap(admin);
        String accessToken = jwtService.generateAccessToken(map, admin);

        doNothing().when(webhookService).handleCommit(any(WebhookPayload.class));

        // when
        mockMvc.perform(post("/webhook/commit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, createAuthorizationHeader(accessToken))
                        .content(objectMapper.writeValueAsString(WebhookPayload.builder().build())))

                // then
                .andExpect(status().isCreated());

    }

    @Test
    void 웹훅_처리_컨트롤러_실패_테스트() throws Exception {
        // given
        User admin = generateAdminUser();

        Map<String, String> map = TokenUtil.createTokenMap(admin);
        String accessToken = jwtService.generateAccessToken(map, admin);

        // 레포지토리 형식 미준수
        WebhookPayload payload = WebhookPayload.builder().repositoryFullName("invalid").build();

        doNothing().when(webhookService).handleCommit(any(WebhookPayload.class));

        // when
        mockMvc.perform(post("/webhook/commit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, createAuthorizationHeader(accessToken))
                        .content(objectMapper.writeValueAsString(payload)))

                // then
                .andExpect(status().isBadRequest());

    }
}