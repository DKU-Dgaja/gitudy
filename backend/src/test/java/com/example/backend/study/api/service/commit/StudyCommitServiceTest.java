package com.example.backend.study.api.service.commit;

import com.example.backend.auth.TestConfig;
import com.example.backend.auth.config.fixture.UserFixture;
import com.example.backend.common.exception.ExceptionMessage;
import com.example.backend.common.exception.commit.CommitException;
import com.example.backend.common.exception.user.UserException;
import com.example.backend.domain.define.account.user.User;
import com.example.backend.domain.define.account.user.repository.UserRepository;
import com.example.backend.domain.define.study.commit.StudyCommit;
import com.example.backend.domain.define.study.commit.StudyCommitFixture;
import com.example.backend.domain.define.study.commit.repository.StudyCommitRepository;
import com.example.backend.domain.define.study.convention.StudyConvention;
import com.example.backend.domain.define.study.convention.StudyConventionFixture;
import com.example.backend.domain.define.study.convention.repository.StudyConventionRepository;
import com.example.backend.domain.define.study.info.StudyInfo;
import com.example.backend.domain.define.study.info.StudyInfoFixture;
import com.example.backend.domain.define.study.info.constant.RepositoryInfo;
import com.example.backend.domain.define.study.info.constant.StudyPeriodType;
import com.example.backend.domain.define.study.info.constant.StudyStatus;
import com.example.backend.domain.define.study.info.repository.StudyInfoRepository;
import com.example.backend.domain.define.study.todo.StudyTodoFixture;
import com.example.backend.domain.define.study.todo.info.StudyTodo;
import com.example.backend.domain.define.study.todo.repository.StudyTodoRepository;
import com.example.backend.study.api.service.commit.response.CommitInfoResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import static com.example.backend.domain.define.account.user.constant.UserPlatformType.GITHUB;
import static com.example.backend.domain.define.account.user.constant.UserRole.USER;
import static com.example.backend.domain.define.study.commit.StudyCommitFixture.createDefaultStudyCommitList;
import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("NonAsciiCharacters")
class StudyCommitServiceTest extends TestConfig {
    private final static int DATA_SIZE = 10;
    private final static Long LIMIT = 10L;
    private final String REPOSITORY_OWNER = "jusung-c";
    private final String REPOSITORY_NAME = "Github-Api-Test";

    @Autowired
    private StudyCommitService studyCommitService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StudyCommitRepository studyCommitRepository;

    @Autowired
    private StudyInfoRepository studyInfoRepository;

    @Autowired
    private StudyTodoRepository studyTodoRepository;

    @Autowired
    private StudyConventionRepository studyConventionRepository;

    @AfterEach
    void tearDown() {
        userRepository.deleteAllInBatch();
        studyCommitRepository.deleteAllInBatch();
        studyInfoRepository.deleteAllInBatch();
        studyTodoRepository.deleteAllInBatch();
        studyInfoRepository.deleteAllInBatch();
    }

    @Test
    void 커서가_null이_아닌_경우_마이_커밋_조회_테스트() {
        // given
        Random random = new Random();
        Long cursorIdx = random.nextLong(LIMIT) + 1L;

        Set<Integer> usedValues = new HashSet<>();

        List<StudyCommit> commitList = createDefaultStudyCommitList(DATA_SIZE, 1L, 1L, 1L, usedValues);
        studyCommitRepository.saveAll(commitList);

        // when
        List<CommitInfoResponse> commitInfoList = studyCommitService
                .selectUserCommitList(1L, null, cursorIdx, LIMIT);
//        System.out.println("cursorIdx = " + cursorIdx);
//        System.out.println("LIMIT = " + LIMIT);
//
//        for (CommitInfoResponse commit : commitInfoList) {
//            System.out.println("commit.getId() = " + commit.getId());
//        }

//        assertEquals(cursorIdx <= LIMIT ? cursorIdx-1 : LIMIT, commitInfoList.size());
        for (CommitInfoResponse commit : commitInfoList) {
            assertTrue(commit.getId() < cursorIdx);
        }
    }

    @Test
    void 커서가_null인_경우_마이_커밋_조회_테스트() {
        // given
        Set<Integer> usedValues = new HashSet<>();

        List<StudyCommit> commitList = createDefaultStudyCommitList(DATA_SIZE, 1L, 1L, 1L, usedValues);
        studyCommitRepository.saveAll(commitList);

        // when
        List<CommitInfoResponse> commitInfoList = studyCommitService.selectUserCommitList(1L, null, null, LIMIT);
//        List<CommitInfoResponse> content = commitInfoPage.getContent();
//        for (CommitInfoResponse c : content) {
//            System.out.println("c.getId() = " + c.getId());
//        }

        assertEquals(LIMIT, commitInfoList.size());
    }

    @Test
    void 커밋_상세_조회_성공_테스트() {
        // given
        Long studyId = 1L;
        Long userId = 1L;
        Long studyTodoId = 1L;
        String commitSha = "123";

        StudyCommit savedCommit = studyCommitRepository.save(StudyCommitFixture.createDefaultStudyCommit(userId, studyId, studyTodoId, commitSha));

        // when
        CommitInfoResponse commitInfoResponse = studyCommitService.getCommitDetailsById(savedCommit.getId());

        // then
        assertEquals(savedCommit.getId(), commitInfoResponse.getId());
        assertEquals(commitSha, commitInfoResponse.getCommitSHA());
    }

    @Test
    void 커밋_상세_조회_실패_테스트() {
        // given
        Long commitID = 1L;
//        StudyCommit savedCommit = studyCommitRepository.save(StudyCommitFixture.createDefaultStudyCommit(commitSha));

        // when & then
        assertThrows(CommitException.class, () -> {
            studyCommitService.getCommitDetailsById(commitID);
        });
    }

    @Test
    void 레포지토리에서_커밋_fetch_성공_테스트() {
        // given
        // 유저 저장
        User user = userRepository.save(User.builder()
                .platformId("1")
                .platformType(GITHUB)
                .role(USER)
                .name("이름")
                .githubId(REPOSITORY_OWNER)
                .profileImageUrl("프로필이미지")
                .build());

        // 스터디 저장
        StudyInfo study = studyInfoRepository.save(StudyInfo.builder()
                .userId(user.getId())
                .topic("topic")
                .status(StudyStatus.STUDY_PUBLIC)
                .repositoryInfo(RepositoryInfo.builder()
                        .owner(REPOSITORY_OWNER)
                        .name(REPOSITORY_NAME)
                        .branchName("main")
                        .build())
                .build());

        // 투두 저장
        StudyTodo todo = studyTodoRepository.save(StudyTodoFixture.createStudyTodo(study.getId()));

        // 컨벤션 저장
        String conventionName = "커밋 메세지 규칙";
        String convention = "^\\[[A-Za-z가-힣0-9]+\\] [A-Za-z가-힣]+: .+$";
        String conventionDescription = "커밋 메세지 규칙: [이름] 플랫폼 \":\" + \" \" + 문제 이름 \n" +
                "예시 1) [이주성] 백준: 크리스마스 트리 \n" +
                "예시 2) [이주성] 프로그래머스: 두 수의 곱";

        studyConventionRepository.save(StudyConvention.builder()
                .studyInfoId(1L)
                .name(conventionName)
                .description(conventionDescription)
                .content(convention)
                .isActive(true)
                .build());

        // 현재 "백준 1234 풀기" 폴더에 저장되어 있는 테스트 커밋 5개, 이 중 컨벤션을 지킨 커밋은 1개
        // [이주성] 백준 1234번 크리스마스 트리
        // [이주성] 백준: 1234번 크리스마스 트리 -> 컨벤션 지킨 유일한 커밋
        // Create test
        // 백준 1234번 풀기 directory
        // TODO(백준 1234번 풀기)가 생성되었습니다.
        int expectedSize = 1;
        String expectedMsg = "[이주성] 백준: 1234번 크리스마스 트리";

        // when
        studyCommitService.fetchRemoteCommitsAndSave(study, todo);
        List<StudyCommit> allCommits = studyCommitRepository.findAll();

        // then
        assertEquals(allCommits.size(), expectedSize);
        for (var c : allCommits) {
            assertEquals(c.getStudyInfoId(), study.getUserId());
            assertEquals(c.getUserId(), user.getId());
        }
        assertEquals(allCommits.get(0).getMessage(), expectedMsg);
    }

}