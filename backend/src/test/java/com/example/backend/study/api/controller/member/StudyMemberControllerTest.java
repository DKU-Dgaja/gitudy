package com.example.backend.study.api.controller.member;

import com.example.backend.MockTestConfig;
import com.example.backend.TestConfig;
import com.example.backend.auth.api.controller.auth.response.UserInfoResponse;
import com.example.backend.auth.api.service.auth.AuthService;
import com.example.backend.auth.api.service.jwt.JwtService;
import com.example.backend.common.utils.TokenUtil;
import com.example.backend.domain.define.account.user.User;
import com.example.backend.domain.define.account.user.repository.UserRepository;
import com.example.backend.domain.define.study.info.StudyInfo;
import com.example.backend.domain.define.study.info.StudyInfoFixture;
import com.example.backend.domain.define.study.info.repository.StudyInfoRepository;
import com.example.backend.domain.define.study.member.StudyMember;
import com.example.backend.domain.define.study.member.StudyMemberFixture;
import com.example.backend.domain.define.study.member.repository.StudyMemberRepository;
import com.example.backend.study.api.controller.member.request.MessageRequest;
import com.example.backend.study.api.controller.member.response.StudyMemberApplyListAndCursorIdxResponse;
import com.example.backend.study.api.service.member.StudyMemberService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Map;

import static com.example.backend.auth.config.fixture.UserFixture.generateAuthUser;
import static com.example.backend.auth.config.fixture.UserFixture.generateKaKaoUser;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class StudyMemberControllerTest extends MockTestConfig {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    @MockBean
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StudyInfoRepository studyInfoRepository;

    @Autowired
    private StudyMemberRepository studyMemberRepository;

    @MockBean
    private StudyMemberService studyMemberService;

    @Autowired
    private ObjectMapper objectMapper;


    @AfterEach
    void tearDown() {
        userRepository.deleteAllInBatch();
        studyInfoRepository.deleteAllInBatch();
        studyMemberRepository.deleteAllInBatch();
    }

    @Test
    public void 스터디원_기여도별_조회_테스트() throws Exception {
        // given

        User savedUser = userRepository.save(generateAuthUser());
        Map<String, String> map = TokenUtil.createTokenMap(savedUser);
        String accessToken = jwtService.generateAccessToken(map, savedUser);
        String refreshToken = jwtService.generateRefreshToken(map, savedUser);

        StudyInfo studyInfo = StudyInfoFixture.createDefaultPublicStudyInfo(savedUser.getId());
        studyInfoRepository.save(studyInfo);

        when(authService.authenticate(any(Long.class), any(User.class))).thenReturn(UserInfoResponse.builder().build());
        when(studyMemberService.readStudyMembers(any(Long.class), any(boolean.class))).thenReturn(new ArrayList<>());


        //when , then
        mockMvc.perform(get("/member/{studyInfoId}", studyInfo.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, createAuthorizationHeader(accessToken, refreshToken)))
                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.res_code").value(200));

    }

    @Test
    public void 스터디원_강퇴_테스트() throws Exception {
        // given

        User leader = userRepository.save(generateAuthUser());
        User member = userRepository.save(generateKaKaoUser());

        Map<String, String> map = TokenUtil.createTokenMap(leader);
        String accessToken = jwtService.generateAccessToken(map, leader);
        String refreshToken = jwtService.generateRefreshToken(map, leader);

        StudyInfo studyInfo = StudyInfoFixture.createDefaultPublicStudyInfo(leader.getId());
        studyInfoRepository.save(studyInfo);

        StudyMember studyMember = StudyMemberFixture.createDefaultStudyMember(member.getId(), studyInfo.getId());
        studyMemberRepository.save(studyMember);

        when(studyMemberService.isValidateStudyLeader(any(User.class), any(Long.class))).thenReturn(UserInfoResponse.of(leader));
        doNothing().when(studyMemberService).resignStudyMember(any(Long.class), any(Long.class));

        //when , then
        mockMvc.perform(patch("/member/" + studyInfo.getId() + "/resign/" + studyMember.getUserId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, createAuthorizationHeader(accessToken, refreshToken))
                        .param("resignUserId", String.valueOf(studyMember.getUserId())))

                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.res_code").value(200))
                .andExpect(jsonPath("$.res_obj").value("Resign Member Success"));
    }


    @Test
    public void 스터디원_탈퇴_테스트() throws Exception {
        // given

        User leader = userRepository.save(generateAuthUser());
        User member = userRepository.save(generateKaKaoUser());

        Map<String, String> map = TokenUtil.createTokenMap(leader);
        String accessToken = jwtService.generateAccessToken(map, leader);
        String refreshToken = jwtService.generateRefreshToken(map, leader);

        StudyInfo studyInfo = StudyInfoFixture.createDefaultPublicStudyInfo(leader.getId());
        studyInfoRepository.save(studyInfo);

        StudyMember studyMember = StudyMemberFixture.createDefaultStudyMember(member.getId(), studyInfo.getId());
        studyMemberRepository.save(studyMember);

        when(studyMemberService.isValidateStudyMember(any(User.class), any(Long.class))).thenReturn(UserInfoResponse.of(member));
        doNothing().when(studyMemberService).resignStudyMember(any(Long.class), any(Long.class));

        //when , then
        mockMvc.perform(patch("/member/" + studyInfo.getId() + "/withdrawal")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, createAuthorizationHeader(accessToken, refreshToken)))

                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.res_code").value(200))
                .andExpect(jsonPath("$.res_obj").value("Withdrawal Member Success"));
    }

    @Test
    public void 스터디_가입_신청_테스트() throws Exception {
        // given
        User savedUser = userRepository.save(generateAuthUser());

        Map<String, String> map = TokenUtil.createTokenMap(savedUser);
        String accessToken = jwtService.generateAccessToken(map, savedUser);
        String refreshToken = jwtService.generateRefreshToken(map, savedUser);

        StudyInfo studyInfo = StudyInfoFixture.createDefaultPublicStudyInfo(savedUser.getId());
        studyInfoRepository.save(studyInfo);

        when(authService.findUserInfo(any(User.class))).thenReturn(UserInfoResponse.of(savedUser));
        doNothing().when(studyMemberService).applyStudyMember(any(UserInfoResponse.class), any(Long.class), any(String.class), any(MessageRequest.class));

        MessageRequest messageRequest = StudyMemberFixture.generateMessageRequest();

        //when , then
        mockMvc.perform(post("/member/" + studyInfo.getId() + "/apply")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(messageRequest))
                        .header(AUTHORIZATION, createAuthorizationHeader(accessToken, refreshToken)))

                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.res_code").value(200))
                .andExpect(jsonPath("$.res_obj").value("Apply StudyMember Success"));

    }

    @Test
    public void 스터디장의_가입_신청_승인_거부_테스트() throws Exception {
        // given
        User savedUser = userRepository.save(generateAuthUser());

        Map<String, String> map = TokenUtil.createTokenMap(savedUser);
        String accessToken = jwtService.generateAccessToken(map, savedUser);
        String refreshToken = jwtService.generateRefreshToken(map, savedUser);

        StudyInfo studyInfo = StudyInfoFixture.createDefaultPublicStudyInfo(savedUser.getId());
        studyInfoRepository.save(studyInfo);

        when(studyMemberService.isValidateStudyLeader(any(User.class), any(Long.class))).thenReturn(UserInfoResponse.of(savedUser));
        doNothing().when(studyMemberService).leaderApproveRefuseMember(any(Long.class), any(Long.class), any(boolean.class));

        // when, then
        mockMvc.perform(patch("/member/" + studyInfo.getId() + "/apply/" + 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, createAuthorizationHeader(accessToken, refreshToken)))

                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.res_code").value(200))
                .andExpect(jsonPath("$.res_obj").value("Apply Approve or Refuse StudyMember Success"));

    }

    @Test
    public void 스터디_가입_신청_취소_테스트() throws Exception {
        //given
        User savedUser = userRepository.save(generateAuthUser());

        Map<String, String> map = TokenUtil.createTokenMap(savedUser);
        String accessToken = jwtService.generateAccessToken(map, savedUser);
        String refreshToken = jwtService.generateRefreshToken(map, savedUser);

        StudyInfo studyInfo = StudyInfoFixture.createDefaultPublicStudyInfo(savedUser.getId());
        studyInfoRepository.save(studyInfo);

        when(authService.findUserInfo(any(User.class))).thenReturn(UserInfoResponse.of(savedUser));
        doNothing().when(studyMemberService).applyCancelStudyMember(any(UserInfoResponse.class), any(Long.class));

        //when , then
        mockMvc.perform(delete("/member/" + studyInfo.getId() + "/apply")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, createAuthorizationHeader(accessToken, refreshToken)))

                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.res_code").value(200))
                .andExpect(jsonPath("$.res_obj").value("Apply cancel StudyMember Success"));

    }


    @Test
    public void 스터디_가입_신청_목록_조회_테스트() throws Exception {
        //given
        User savedUser = userRepository.save(generateAuthUser());

        Map<String, String> map = TokenUtil.createTokenMap(savedUser);
        String accessToken = jwtService.generateAccessToken(map, savedUser);
        String refreshToken = jwtService.generateRefreshToken(map, savedUser);

        StudyInfo studyInfo = StudyInfoFixture.createDefaultPublicStudyInfo(savedUser.getId());
        studyInfoRepository.save(studyInfo);

        StudyMemberApplyListAndCursorIdxResponse response = StudyMemberApplyListAndCursorIdxResponse.builder()
                .applyList(new ArrayList<>()) // 비어 있는 가입 리스트
                .build();

        when(studyMemberService.isValidateStudyLeader(any(User.class), any(Long.class))).thenReturn(UserInfoResponse.of(savedUser));
        when(studyMemberService.applyListStudyMember(any(Long.class), any(Long.class), any(Long.class))).thenReturn(response);

        //when , then
        mockMvc.perform(get("/member/" + studyInfo.getId() + "/apply")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, createAuthorizationHeader(accessToken, refreshToken))
                        .param("cursorIdx", "1")
                        .param("limit", "5"))

                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.res_code").value(200))
                .andExpect(jsonPath("$.res_msg").value("OK"))
                .andExpect(jsonPath("$.res_obj").isNotEmpty())
                .andDo(print());
    }

    @Test
    public void cursorIdx가_null일_때_스터디_가입_신청_목록_조회_테스트() throws Exception {
        //given
        User savedUser = userRepository.save(generateAuthUser());

        Map<String, String> map = TokenUtil.createTokenMap(savedUser);
        String accessToken = jwtService.generateAccessToken(map, savedUser);
        String refreshToken = jwtService.generateRefreshToken(map, savedUser);

        StudyInfo studyInfo = StudyInfoFixture.createDefaultPublicStudyInfo(savedUser.getId());
        studyInfoRepository.save(studyInfo);

        StudyMemberApplyListAndCursorIdxResponse response = StudyMemberApplyListAndCursorIdxResponse.builder()
                .applyList(new ArrayList<>()) // 비어 있는 가입 리스트
                .build();

        when(studyMemberService.isValidateStudyLeader(any(User.class), any(Long.class))).thenReturn(UserInfoResponse.of(savedUser));
        when(studyMemberService.applyListStudyMember(any(Long.class), any(Long.class), any(Long.class))).thenReturn(response);

        //when , then
        mockMvc.perform(get("/member/" + studyInfo.getId() + "/apply")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, createAuthorizationHeader(accessToken, refreshToken))
                        .param("cursorIdx", "")
                        .param("limit", "5"))

                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.res_code").value(200))
                .andExpect(jsonPath("$.res_msg").value("OK"))
                .andExpect(jsonPath("$.res_obj").isEmpty())
                .andDo(print());
    }

    @Test
    public void 스터디_멤버에게_알림_테스트() throws Exception {
        // given
        User savedUser = userRepository.save(generateAuthUser());

        Map<String, String> map = TokenUtil.createTokenMap(savedUser);
        String accessToken = jwtService.generateAccessToken(map, savedUser);
        String refreshToken = jwtService.generateRefreshToken(map, savedUser);

        StudyInfo studyInfo = StudyInfoFixture.createDefaultPublicStudyInfo(savedUser.getId());
        studyInfoRepository.save(studyInfo);

        when(studyMemberService.isValidateStudyLeader(any(User.class), any(Long.class))).thenReturn(UserInfoResponse.of(savedUser));

        MessageRequest messageRequest = StudyMemberFixture.generateMessageRequest();

        //when , then
        mockMvc.perform(post("/member/" + studyInfo.getId() + "/notify/" + 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(messageRequest))
                        .header(AUTHORIZATION, createAuthorizationHeader(accessToken, refreshToken)))

                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.res_code").value(200))
                .andExpect(jsonPath("$.res_obj").value("Notify to StudyMember Success"));

    }
}