package com.example.backend;

import com.example.backend.auth.api.service.auth.AuthService;
import com.example.backend.auth.api.service.rank.RankingService;
import com.example.backend.study.api.event.service.FcmService;
import com.example.backend.study.api.event.service.NoticeService;
import com.example.backend.study.api.service.bookmark.StudyBookmarkService;
import com.example.backend.study.api.service.category.info.CategoryService;
import com.example.backend.study.api.service.comment.commit.CommitCommentService;
import com.example.backend.study.api.service.comment.study.StudyCommentService;
import com.example.backend.study.api.service.commit.StudyCommitService;
import com.example.backend.study.api.service.convention.StudyConventionService;
import com.example.backend.study.api.service.info.StudyInfoService;
import com.example.backend.study.api.service.member.StudyMemberService;
import com.example.backend.study.api.service.todo.StudyTodoService;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@TestConfiguration
public class CommonMockConfig {
    @MockBean
    private AuthService authService;

    @MockBean
    private StudyInfoService studyInfoService;

    @MockBean
    private RankingService rankingService;

    @MockBean
    private StudyBookmarkService studyBookmarkService;

    @MockBean
    private CategoryService categoryService;

    @MockBean
    private CommitCommentService commitCommentService;

    @MockBean
    private StudyMemberService studyMemberService;

    @MockBean
    private StudyCommentService studyCommentService;

    @MockBean
    private StudyCommitService studyCommitService;

    @MockBean
    private StudyConventionService studyConventionService;

    @MockBean
    private StudyTodoService studyTodoService;

    @MockBean
    private FcmService fcmService;

    @MockBean
    private NoticeService noticeService;
}