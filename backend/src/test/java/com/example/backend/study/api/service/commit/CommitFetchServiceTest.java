package com.example.backend.study.api.service.commit;

import com.example.backend.MockTestConfig;
import com.example.backend.TestConfig;
import com.example.backend.auth.config.fixture.UserFixture;
import com.example.backend.domain.define.account.user.User;
import com.example.backend.domain.define.account.user.repository.UserRepository;
import com.example.backend.domain.define.study.commit.StudyCommit;
import com.example.backend.domain.define.study.commit.StudyCommitFixture;
import com.example.backend.domain.define.study.commit.constant.CommitStatus;
import com.example.backend.domain.define.study.commit.repository.StudyCommitRepository;
import com.example.backend.domain.define.study.convention.StudyConvention;
import com.example.backend.domain.define.study.convention.StudyConventionFixture;
import com.example.backend.domain.define.study.convention.repository.StudyConventionRepository;
import com.example.backend.domain.define.study.info.StudyInfo;
import com.example.backend.domain.define.study.info.StudyInfoFixture;
import com.example.backend.domain.define.study.info.constant.RepositoryInfo;
import com.example.backend.domain.define.study.info.constant.StudyStatus;
import com.example.backend.domain.define.study.info.repository.StudyInfoRepository;
import com.example.backend.domain.define.study.member.StudyMemberFixture;
import com.example.backend.domain.define.study.member.repository.StudyMemberRepository;
import com.example.backend.domain.define.study.todo.StudyTodoFixture;
import com.example.backend.domain.define.study.todo.info.StudyTodo;
import com.example.backend.domain.define.study.todo.mapping.StudyTodoMapping;
import com.example.backend.domain.define.study.todo.mapping.constant.StudyTodoStatus;
import com.example.backend.domain.define.study.todo.mapping.repository.StudyTodoMappingRepository;
import com.example.backend.domain.define.study.todo.repository.StudyTodoRepository;
import com.example.backend.study.api.service.github.GithubApiService;
import com.example.backend.study.api.service.github.response.GithubCommitResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static com.example.backend.domain.define.account.user.constant.UserPlatformType.GITHUB;
import static com.example.backend.domain.define.account.user.constant.UserRole.USER;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SuppressWarnings("NonAsciiCharacters")
public class CommitFetchServiceTest extends MockTestConfig {
    private final String REPOSITORY_OWNER = "jusung-c";
    private final String REPOSITORY_NAME = "Github-Api-Test";
    private final int PAGE_SIZE = 5;

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
    private StudyTodoMappingRepository studyTodoMappingRepository;

    @Autowired
    private StudyConventionRepository studyConventionRepository;

    @Autowired
    private StudyMemberRepository studyMemberRepository;

    @MockBean
    GithubApiService githubApiService;

    @AfterEach
    void tearDown() {
        userRepository.deleteAllInBatch();
        studyCommitRepository.deleteAllInBatch();
        studyInfoRepository.deleteAllInBatch();
        studyTodoRepository.deleteAllInBatch();
        studyInfoRepository.deleteAllInBatch();
        studyMemberRepository.deleteAllInBatch();
        studyTodoMappingRepository.deleteAllInBatch();
    }

    @Test
    void 레포지토리에서_aBc123에_해당하는_커밋_fetch_테스트() {
        // Given
        // 유저 저장
        User userA = userRepository.save(User.builder()
                .platformId("1")
                .platformType(GITHUB)
                .role(USER)
                .name("이름")
                .githubId(REPOSITORY_OWNER)
                .profileImageUrl("프로필이미지")
                .build());

        String gitId = "jjjjssssuuunngg";
        User userB = userRepository.save(User.builder()
                .platformId("2")
                .platformType(GITHUB)
                .role(USER)
                .name("이름")
                .githubId(gitId)
                .profileImageUrl("프로필이미지")
                .build());

        // 스터디 저장
        StudyInfo study = studyInfoRepository.save(StudyInfo.builder()
                .userId(userA.getId())
                .topic("topic")
                .status(StudyStatus.STUDY_PUBLIC)
                .repositoryInfo(RepositoryInfo.builder()
                        .owner(REPOSITORY_OWNER)
                        .name(REPOSITORY_NAME)
                        .branchName("main")
                        .build())
                .build());

        // 스터디원 저장
        studyMemberRepository.save(StudyMemberFixture.createDefaultStudyMember(userA.getId(), study.getId()));
        studyMemberRepository.save(StudyMemberFixture.createDefaultStudyMember(userB.getId(), study.getId()));

        // 투두 저장
        String todoCode = "aBc123";
        StudyTodo todo = StudyTodoFixture.createStudyTodo(study.getId());
        todo.updateTodoCode(todoCode);
        studyTodoRepository.save(todo);

        // 컨벤션 저장
        String conventionName = "커밋 메세지 규칙";
        String convention = "^[A-Za-z0-9]{6} \\[[A-Za-z가-힣0-9\\W]+\\] [A-Za-z가-힣]+: .+\\n?\\n?.*";
        String conventionDescription = "커밋 메세지 규칙: 투두코드6자리 + 공백(\" \") + [이름] 플랫폼 \":\" + 공백(\" \") + 문제 이름 \n" +
                "예시 1) abc123 [이주성] 백준: 크리스마스 트리 \n" +
                "예시 2) abc123 [이주성] 프로그래머스: 두 수의 곱";

        // 컨벤션 등록
        studyConventionRepository.save(StudyConvention.builder()
                .studyInfoId(study.getId())
                .name(conventionName)
                .description(conventionDescription)
                .content(convention)
                .isActive(true)
                .build());

        String A = "aBc123 [jjjjssssuuunngg] 백준: 컨벤션 지키기";
        String B = "aBc123 [jusung-c] 백준: 컨벤션 수칙 지키기";
        int expectedSize = 2;

        List<GithubCommitResponse> mockCommits = List.of(
                GithubCommitResponse.builder().sha("sha1").message(A).authorName(userA.getGithubId()).commitDate(LocalDate.now()).build(),
                GithubCommitResponse.builder().sha("sha2").message(B).authorName(userB.getGithubId()).commitDate(LocalDate.now()).build(),
                GithubCommitResponse.builder().sha("sha3").message("aBc123 컨벤션 무시하기").authorName(userA.getGithubId()).commitDate(LocalDate.now()).build(),
                GithubCommitResponse.builder().sha("sha4").message("aBc123 컨벤션 무시하기").authorName(userB.getGithubId()).commitDate(LocalDate.now()).build()
        );

        when(githubApiService.fetchCommits(any(RepositoryInfo.class), anyInt(), anyInt(), eq("aBc123")))
                .thenReturn(mockCommits);

        // When
        studyCommitService.fetchRemoteCommitsAndSave(study, todo, PAGE_SIZE);
        List<StudyCommit> allCommits = studyCommitRepository.findAll();

        // Then
        assertEquals(allCommits.size(), expectedSize);
        for (var c : allCommits) {
            assertEquals(c.getStudyInfoId(), study.getId());
            assertTrue(c.getUserId() == userA.getId()
                    || c.getUserId() == userB.getId());
        }

        assertEquals(A, allCommits.get(0).getMessage());
        assertEquals(B, allCommits.get(1).getMessage());
    }


    @Test
    void 빈_커밋_페이지_처리_테스트() {
        // given
        User user = userRepository.save(UserFixture.generateAuthUser());
        StudyInfo study = studyInfoRepository.save(StudyInfoFixture.createDefaultPublicStudyInfo(user.getId()));
        StudyTodo todo = studyTodoRepository.save(StudyTodoFixture.createStudyTodo(study.getId()));

        when(githubApiService.fetchCommits(any(RepositoryInfo.class), anyInt(), anyInt(), anyString()))
                .thenReturn(Collections.emptyList());

        // when
        studyCommitService.fetchRemoteCommitsAndSave(study, todo, PAGE_SIZE);

        // then
        // githubApiService.fetchCommits가 정확히 한 번 호출되었는지 검증
        verify(githubApiService, times(1)).fetchCommits(any(), anyInt(), anyInt(), anyString());
        // 저장소에 커밋이 저장되지 않았는지 검증
        assertTrue(studyCommitRepository.findAll().isEmpty());
    }

    @Test
    void 저장된_커밋_발견_시_조회_중단_테스트() {
        // given
        User user = userRepository.save(UserFixture.generateAuthUser());
        StudyInfo study = studyInfoRepository.save(StudyInfoFixture.generateStudyInfo(user.getId()));
        StudyTodo todo = studyTodoRepository.save(StudyTodoFixture.createStudyTodo(study.getId()));

        StudyCommit commit = studyCommitRepository.save(
                StudyCommit.builder()
                        .studyTodoId(todo.getId())
                        .studyInfoId(study.getId())
                        .userId(user.getId())
                        .commitSHA("sha")
                        .message("hi")
                        .commitDate(LocalDate.now())
                        .build());

        // 조회한 첫 번째 페이지:
        List<GithubCommitResponse> firstPage = List.of(
                GithubCommitResponse.builder().authorName(user.getGithubId()).message("hi").sha("sha").build());

        when(githubApiService.fetchCommits(any(RepositoryInfo.class), eq(0), anyInt(), anyString()))
                .thenReturn(firstPage);

        // when
        studyCommitService.fetchRemoteCommitsAndSave(study, todo, PAGE_SIZE);

        // then
        // githubApiService.fetchCommits가 정확히 한 번 호출되었는지 검증
        verify(githubApiService, times(1))
                .fetchCommits(any(RepositoryInfo.class), eq(0), anyInt(), anyString());

        // 두 번째 페이지에 대한 호출이 발생하지 않았음을 검증
        verify(githubApiService, never())
                .fetchCommits(any(RepositoryInfo.class), eq(1), anyInt(), anyString());

        assertEquals(1, studyCommitRepository.findAll().size());
    }

    @Test
    void 저장되지_않은_커밋이_있을_때_처리를_계속하는지_테스트() {
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

        // 스터디원 저장
        studyMemberRepository.save(StudyMemberFixture.createDefaultStudyMember(user.getId(), study.getId()));

        // 투두 저장
        String todoCode = "aBc123";
        StudyTodo todo = StudyTodoFixture.createStudyTodo(study.getId());
        todo.updateTodoCode(todoCode);
        studyTodoRepository.save(todo);

        // 컨벤션 저장
        String conventionName = "커밋 메세지 규칙";
        String convention = "^[A-Za-z0-9]{6} \\[[A-Za-z가-힣0-9\\W]+\\] [A-Za-z가-힣]+: .+\\n?\\n?.*";
        String conventionDescription = "커밋 메세지 규칙: 투두코드6자리 + 공백(\" \") + [이름] 플랫폼 \":\" + 공백(\" \") + 문제 이름 \n" +
                "예시 1) abc123 [이주성] 백준: 크리스마스 트리 \n" +
                "예시 2) abc123 [이주성] 프로그래머스: 두 수의 곱";

        // 컨벤션 등록
        studyConventionRepository.save(StudyConvention.builder()
                .studyInfoId(study.getId())
                .name(conventionName)
                .description(conventionDescription)
                .content(convention)
                .isActive(true)
                .build());

        // 커밋 저장
        StudyCommit savedCommit = studyCommitRepository.save(StudyCommit.builder()
                .studyInfoId(study.getId())
                .studyTodoId(todo.getId())
                .userId(user.getId())
                .message("aBc123 [jusung-c] 백준: 컨벤션 수칙 지키기")
                .commitDate(LocalDate.now())
                .status(CommitStatus.COMMIT_APPROVAL)
                .commitSHA("sha")
                .build());

        String A = "aBc123 [jusung-c] 백준: 컨벤션 지키기";

        // 첫 페이지: 이미 저장된 커밋, 새로운 커밋
        List<GithubCommitResponse> firstPage = List.of(
                GithubCommitResponse.builder().authorName(user.getGithubId()).message(A).commitDate(LocalDate.now()).sha("sha1").build(),
                GithubCommitResponse.builder().authorName(user.getGithubId()).message(savedCommit.getMessage()).commitDate(LocalDate.now()).sha("sha").build()
        );

        when(githubApiService.fetchCommits(any(RepositoryInfo.class), eq(0), anyInt(), anyString()))
                .thenReturn(firstPage);
        when(githubApiService.fetchCommits(any(RepositoryInfo.class), eq(1), anyInt(), anyString()))
                .thenReturn(Collections.emptyList());

        // when
        studyCommitService.fetchRemoteCommitsAndSave(study, todo, PAGE_SIZE);

        // then
        List<StudyCommit> allCommits = studyCommitRepository.findAll();
        assertEquals(2, allCommits.size());
    }

    @Test
    void 컨벤션_불일치_커밋_처리_테스트() {
        // Given
        User user = userRepository.save(UserFixture.generateAuthUser());
        StudyInfo study = studyInfoRepository.save(StudyInfoFixture.generateStudyInfo(user.getId()));
        StudyTodo todo = studyTodoRepository.save(StudyTodoFixture.createStudyTodo(study.getId()));
        studyConventionRepository.save(StudyConventionFixture.createStudyDefaultConvention(study.getId()));

        List<GithubCommitResponse> invalidConvention = List.of(StudyCommitFixture.createGithubCommitResponse(user.getGithubId()));

        // 깃허브 페이지 조회
        when(githubApiService.fetchCommits(any(), eq(0), anyInt(), anyString()))
                .thenReturn(invalidConvention);

        when(githubApiService.fetchCommits(any(), eq(1), anyInt(), anyString()))
                .thenReturn(Collections.emptyList());

        // When
        studyCommitService.fetchRemoteCommitsAndSave(study, todo, PAGE_SIZE);

        // Then
        assertTrue(studyCommitRepository.findAll().isEmpty());
    }

    @Test
    void 존재하지_않는_사용자거나_스터디_멤버가_아닌_사용자의_커밋은_무시() {
        // given
        String nonExistGithubId = "nonExistUser";
        String nonMemberGithubId = "nonMemberUser";

        User userA = userRepository.save(User.builder()
                .platformId("1")
                .platformType(GITHUB)
                .role(USER)
                .name("이름")
                .githubId(nonMemberGithubId)
                .profileImageUrl("프로필이미지")
                .build());

        // 스터디 저장
        StudyInfo study = studyInfoRepository.save(StudyInfo.builder()
                .userId(userA.getId())
                .topic("topic")
                .status(StudyStatus.STUDY_PUBLIC)
                .repositoryInfo(RepositoryInfo.builder()
                        .owner(REPOSITORY_OWNER)
                        .name(REPOSITORY_NAME)
                        .branchName("main")
                        .build())
                .build());

        // 투두 저장
        String todoCode = "aBc123";
        StudyTodo todo = StudyTodoFixture.createStudyTodo(study.getId());
        todo.updateTodoCode(todoCode);
        studyTodoRepository.save(todo);

        // 컨벤션 저장
        String conventionName = "커밋 메세지 규칙";
        String convention = "^[A-Za-z0-9]{6} \\[[A-Za-z가-힣0-9\\W]+\\] [A-Za-z가-힣]+: .+\\n?\\n?.*";
        String conventionDescription = "커밋 메세지 규칙: 투두코드6자리 + 공백(\" \") + [이름] 플랫폼 \":\" + 공백(\" \") + 문제 이름 \n" +
                "예시 1) abc123 [이주성] 백준: 크리스마스 트리 \n" +
                "예시 2) abc123 [이주성] 프로그래머스: 두 수의 곱";

        // 컨벤션 등록
        studyConventionRepository.save(StudyConvention.builder()
                .studyInfoId(study.getId())
                .name(conventionName)
                .description(conventionDescription)
                .content(convention)
                .isActive(true)
                .build());

        String A = "aBc123 [jjjjssssuuunngg] 백준: 컨벤션 지키기";
        String B = "aBc123 [jusung-c] 백준: 컨벤션 수칙 지키기";

        List<GithubCommitResponse> mockCommits = List.of(
                GithubCommitResponse.builder().sha("sha1").message(A).authorName(userA.getGithubId()).commitDate(LocalDate.now()).build(),
                GithubCommitResponse.builder().sha("sha2").message(B).authorName(nonExistGithubId).commitDate(LocalDate.now()).build(),
                GithubCommitResponse.builder().sha("sha3").message("aBc123 컨벤션 무시하기").authorName(userA.getGithubId()).commitDate(LocalDate.now()).build(),
                GithubCommitResponse.builder().sha("sha4").message("aBc123 컨벤션 무시하기").authorName(nonExistGithubId).commitDate(LocalDate.now()).build()
        );

        when(githubApiService.fetchCommits(any(), eq(0), anyInt(), eq(todoCode)))
                .thenReturn(mockCommits);

        when(githubApiService.fetchCommits(any(), eq(1), anyInt(), eq(todoCode)))
                .thenReturn(Collections.emptyList());

        // when
        studyCommitService.fetchRemoteCommitsAndSave(study, todo, PAGE_SIZE);
        List<StudyCommit> allCommits = studyCommitRepository.findAll();

        // then
        assertTrue(allCommits.isEmpty());
    }

    @Test
    void 비활성_스터디_멤버의_커밋_무시_테스트() {
        // given
        String inActiveGithubId = "inActive";

        User activeUser = userRepository.save(UserFixture.generateAuthUser());
        User inActiveUser = userRepository.save(UserFixture.generateAuthUserByGithubId(inActiveGithubId));

        StudyInfo study = studyInfoRepository.save(StudyInfoFixture.generateStudyInfo(activeUser.getId()));

        studyMemberRepository.save(StudyMemberFixture.createStudyMemberLeader(activeUser.getId(), study.getId()));
        studyMemberRepository.save(StudyMemberFixture.createStudyMemberWaiting(inActiveUser.getId(), study.getId()));

        // 투두 저장
        String todoCode = "aBc123";
        StudyTodo todo = StudyTodoFixture.createStudyTodo(study.getId());
        todo.updateTodoCode(todoCode);
        studyTodoRepository.save(todo);

        // 컨벤션 저장
        String conventionName = "커밋 메세지 규칙";
        String convention = "^[A-Za-z0-9]{6} \\[[A-Za-z가-힣0-9\\W]+\\] [A-Za-z가-힣]+: .+\\n?\\n?.*";
        String conventionDescription = "커밋 메세지 규칙: 투두코드6자리 + 공백(\" \") + [이름] 플랫폼 \":\" + 공백(\" \") + 문제 이름 \n" +
                "예시 1) abc123 [이주성] 백준: 크리스마스 트리 \n" +
                "예시 2) abc123 [이주성] 프로그래머스: 두 수의 곱";

        // 컨벤션 등록
        studyConventionRepository.save(StudyConvention.builder()
                .studyInfoId(study.getId())
                .name(conventionName)
                .description(conventionDescription)
                .content(convention)
                .isActive(true)
                .build());

        // 활성 멤버 커밋
        String activeMessage = "aBc123 [activeUser] 백준: 활성 멤버 커밋";
        GithubCommitResponse activeCommit = GithubCommitResponse.builder()
                .authorName(activeUser.getGithubId())
                .message(activeMessage)
                .commitDate(LocalDate.now())
                .sha("sha")
                .build();

        // 비활성 멤버 커밋
        String inActiveMessage = "aBc123 [inactiveUser] 백준: 비활성 멤버 커밋";
        GithubCommitResponse inActiveCommit = GithubCommitResponse.builder()
                .authorName(inActiveUser.getGithubId())
                .message(inActiveMessage)
                .commitDate(LocalDate.now())
                .sha("sshhaa")
                .build();

        when(githubApiService.fetchCommits(any(RepositoryInfo.class), eq(0), anyInt(), anyString()))
                .thenReturn(List.of(activeCommit, inActiveCommit));

        when(githubApiService.fetchCommits(any(RepositoryInfo.class), eq(1), anyInt(), anyString()))
                .thenReturn(Collections.emptyList());

        // when
        studyCommitService.fetchRemoteCommitsAndSave(study, todo, PAGE_SIZE);

        // then
        List<StudyCommit> allCommits = studyCommitRepository.findAll();
        assertEquals(1, allCommits.size());

        StudyCommit commit = allCommits.get(0);
        assertEquals(commit.getUserId(), activeUser.getId());
        assertEquals(CommitStatus.COMMIT_APPROVAL, commit.getStatus());

    }

    @Test
    void 마감일_당일_커밋_처리_테스트() {
        // given
        User user = userRepository.save(UserFixture.generateAuthUser());
        StudyInfo study = studyInfoRepository.save(StudyInfoFixture.generateStudyInfo(user.getId()));
        studyMemberRepository.save(StudyMemberFixture.createStudyMemberLeader(user.getId(), study.getId()));

        // 투두 저장
        String todoCode = "aBc123";
        StudyTodo todo = StudyTodoFixture.createStudyTodo(study.getId());
        todo.updateTodoCode(todoCode);
        studyTodoRepository.save(todo);

        // 컨벤션 저장
        String conventionName = "커밋 메세지 규칙";
        String convention = "^[A-Za-z0-9]{6} \\[[A-Za-z가-힣0-9\\W]+\\] [A-Za-z가-힣]+: .+\\n?\\n?.*";
        String conventionDescription = "커밋 메세지 규칙: 투두코드6자리 + 공백(\" \") + [이름] 플랫폼 \":\" + 공백(\" \") + 문제 이름 \n" +
                "예시 1) abc123 [이주성] 백준: 크리스마스 트리 \n" +
                "예시 2) abc123 [이주성] 프로그래머스: 두 수의 곱";

        // 컨벤션 등록
        studyConventionRepository.save(StudyConvention.builder()
                .studyInfoId(study.getId())
                .name(conventionName)
                .description(conventionDescription)
                .content(convention)
                .isActive(true)
                .build());

        String message = "aBc123 [이주성] 백준: 활성 멤버 커밋";
        GithubCommitResponse commit = GithubCommitResponse.builder()
                .authorName(user.getGithubId())
                .message(message)
                .commitDate(LocalDate.now())
                .sha("sha")
                .build();

        when(githubApiService.fetchCommits(any(RepositoryInfo.class), eq(0), anyInt(), anyString()))
                .thenReturn(List.of(commit));

        when(githubApiService.fetchCommits(any(RepositoryInfo.class), eq(1), anyInt(), anyString()))
                .thenReturn(Collections.emptyList());

        // when
        studyCommitService.fetchRemoteCommitsAndSave(study, todo, PAGE_SIZE);

        // then
        List<StudyCommit> allCommits = studyCommitRepository.findAll();
        assertEquals(1, allCommits.size());
        assertEquals(message, allCommits.get(0).getMessage());
        assertEquals(CommitStatus.COMMIT_APPROVAL, allCommits.get(0).getStatus());
    }

    @Test
    public void 마감일_지난_커밋으로_투두_매핑_상태_변경_테스트() {
        // given
        User userA = userRepository.save(UserFixture.generateAuthUser());
        User userB = userRepository.save(UserFixture.generateKaKaoUser());
        StudyInfo study = studyInfoRepository.save(StudyInfoFixture.generateStudyInfo(userA.getId()));
        studyMemberRepository.save(StudyMemberFixture.createStudyMemberLeader(userA.getId(), study.getId()));
        studyMemberRepository.save(StudyMemberFixture.createStudyMemberLeader(userB.getId(), study.getId()));

        // 투두 저장
        String todoCode = "aBc123";
        StudyTodo todo = StudyTodoFixture.createStudyTodo(study.getId());
        todo.updateTodoCode(todoCode);
        studyTodoRepository.save(todo);

        // 미완료 투두 매핑 생성
        studyTodoMappingRepository.save(StudyTodoMapping.builder()
                .todoId(todo.getId())
                .userId(userA.getId())
                .status(StudyTodoStatus.TODO_INCOMPLETE)
                .build());

        studyTodoMappingRepository.save(StudyTodoMapping.builder()
                .todoId(todo.getId())
                .userId(userB.getId())
                .status(StudyTodoStatus.TODO_INCOMPLETE)
                .build());

        // 컨벤션 저장
        String conventionName = "커밋 메세지 규칙";
        String convention = "^[A-Za-z0-9]{6} \\[[A-Za-z가-힣0-9\\W]+\\] [A-Za-z가-힣]+: .+\\n?\\n?.*";
        String conventionDescription = "커밋 메세지 규칙: 투두코드6자리 + 공백(\" \") + [이름] 플랫폼 \":\" + 공백(\" \") + 문제 이름 \n" +
                "예시 1) abc123 [이주성] 백준: 크리스마스 트리 \n" +
                "예시 2) abc123 [이주성] 프로그래머스: 두 수의 곱";

        // 컨벤션 등록
        studyConventionRepository.save(StudyConvention.builder()
                .studyInfoId(study.getId())
                .name(conventionName)
                .description(conventionDescription)
                .content(convention)
                .isActive(true)
                .build());

        String lateMsg = "aBc123 [이주성] 백준: 마감일 지난 커밋";
        String msg = "aBc123 [이주성] 백준: 마감일 지킨 커밋";

        GithubCommitResponse lateCommit = GithubCommitResponse.builder()
                .authorName(userA.getGithubId())
                .message(lateMsg)
                .commitDate(LocalDate.now().plusDays(1))   // 마감일 지남
                .sha("sha1")
                .build();

        GithubCommitResponse commit = GithubCommitResponse.builder()
                .authorName(userB.getGithubId())
                .message(msg)
                .commitDate(LocalDate.now().minusDays(1))   // 마감일 지킴
                .sha("sha2")
                .build();

        when(githubApiService.fetchCommits(any(RepositoryInfo.class), eq(0), anyInt(), anyString()))
                .thenReturn(List.of(lateCommit, commit));

        when(githubApiService.fetchCommits(any(RepositoryInfo.class), eq(1), anyInt(), anyString()))
                .thenReturn(Collections.emptyList());

        // when
        studyCommitService.fetchRemoteCommitsAndSave(study, todo, PAGE_SIZE);

        // then
        List<StudyCommit> allCommit = studyCommitRepository.findAll();
        List<StudyTodoMapping> allTodoMapping = studyTodoMappingRepository.findByTodoId(todo.getId());

        assertEquals(2, allCommit.size());
        assertEquals(2, allTodoMapping.size());
        for (var mapping : allTodoMapping) {
            if (mapping.getUserId() == userA.getId()) {
                assertEquals(StudyTodoStatus.TODO_OVERDUE, mapping.getStatus());
            } else {
                assertEquals(StudyTodoStatus.TODO_COMPLETE, mapping.getStatus());
            }
        }

    }

    @Test
    void 여러_페이지_커밋_리스트_처리_테스트() {
        // Given
        User userA = userRepository.save(User.builder()
                .platformId("1")
                .platformType(GITHUB)
                .role(USER)
                .name("이름")
                .githubId(REPOSITORY_OWNER)
                .profileImageUrl("프로필이미지")
                .build());

        // 스터디 저장
        StudyInfo study = studyInfoRepository.save(StudyInfo.builder()
                .userId(userA.getId())
                .topic("topic")
                .status(StudyStatus.STUDY_PUBLIC)
                .repositoryInfo(RepositoryInfo.builder()
                        .owner(REPOSITORY_OWNER)
                        .name(REPOSITORY_NAME)
                        .branchName("main")
                        .build())
                .build());

        studyMemberRepository.save(StudyMemberFixture.createDefaultStudyMember(userA.getId(), study.getId()));

        // 투두 저장
        String todoCode = "aBc123";
        StudyTodo todo = StudyTodoFixture.createStudyTodo(study.getId());
        todo.updateTodoCode(todoCode);
        studyTodoRepository.save(todo);

        // 컨벤션 저장
        String conventionName = "커밋 메세지 규칙";
        String convention = "^[A-Za-z0-9]{6} \\[[A-Za-z가-힣0-9\\W]+\\] [A-Za-z가-힣]+: .+\\n?\\n?.*";
        String conventionDescription = "커밋 메세지 규칙: 투두코드6자리 + 공백(\" \") + [이름] 플랫폼 \":\" + 공백(\" \") + 문제 이름 \n" +
                "예시 1) abc123 [이주성] 백준: 크리스마스 트리 \n" +
                "예시 2) abc123 [이주성] 프로그래머스: 두 수의 곱";

        // 컨벤션 등록
        studyConventionRepository.save(StudyConvention.builder()
                .studyInfoId(study.getId())
                .name(conventionName)
                .description(conventionDescription)
                .content(convention)
                .isActive(true)
                .build());

        String A = "aBc123 [jusung-c] 백준: 컨벤션 지키기";
        String B = "aBc123 [jusung-c] 백준: 컨벤션 수칙 지키기";
        int expectedSize = 2;

        List<GithubCommitResponse> firstPage = List.of(
                GithubCommitResponse.builder().sha("sha1").message(A).authorName(userA.getGithubId()).commitDate(LocalDate.now()).build(),
                GithubCommitResponse.builder().sha("sha2").message("aBc123 컨벤션 무시하기").authorName(userA.getGithubId()).commitDate(LocalDate.now()).build()
        );

        List<GithubCommitResponse> secondPage = List.of(
                GithubCommitResponse.builder().sha("sha3").message(B).authorName(userA.getGithubId()).commitDate(LocalDate.now()).build(),
                GithubCommitResponse.builder().sha("sha4").message("aBc123 컨벤션 무시하기").authorName(userA.getGithubId()).commitDate(LocalDate.now()).build()
        );

        when(githubApiService.fetchCommits(any(RepositoryInfo.class), eq(0), anyInt(), anyString()))
                .thenReturn(firstPage);
        when(githubApiService.fetchCommits(any(RepositoryInfo.class), eq(1), anyInt(), anyString()))
                .thenReturn(secondPage);
        when(githubApiService.fetchCommits(any(RepositoryInfo.class), eq(2), anyInt(), anyString()))
                .thenReturn(Collections.emptyList());

        // when
        studyCommitService.fetchRemoteCommitsAndSave(study, todo, PAGE_SIZE);
        List<StudyCommit> allCommits = studyCommitRepository.findAll();

        // then
        // 첫 번째 페이지와 두 번째 페이지의 커밋이 모두 처리되었는지 확인
        verify(githubApiService, times(1)).fetchCommits(any(RepositoryInfo.class), eq(0), anyInt(), anyString());
        verify(githubApiService, times(1)).fetchCommits(any(RepositoryInfo.class), eq(1), anyInt(), anyString());
        // 세 번째 페이지 조회를 시도했는지 확인
        verify(githubApiService, times(1)).fetchCommits(any(RepositoryInfo.class), eq(2), anyInt(), anyString());

        assertEquals(allCommits.size(), expectedSize);
        for (var c : allCommits) {
            assertEquals(c.getStudyInfoId(), study.getId());
            assertSame(c.getUserId(), userA.getId());
        }

        assertEquals(A, allCommits.get(0).getMessage());
        assertEquals(B, allCommits.get(1).getMessage());
    }

    @Test
    void 저장된_커밋이_중복_저장되지_않는지_테스트() {
        // given
        User user = userRepository.save(UserFixture.generateAuthUser());
        StudyInfo study = studyInfoRepository.save(StudyInfoFixture.generateStudyInfo(user.getId()));
        StudyTodo todo = studyTodoRepository.save(StudyTodoFixture.createStudyTodo(study.getId()));

        StudyCommit commit = studyCommitRepository.save(
                StudyCommit.builder()
                        .studyTodoId(todo.getId())
                        .studyInfoId(study.getId())
                        .userId(user.getId())
                        .commitSHA("sha")
                        .message("hi")
                        .commitDate(LocalDate.now())
                        .build());

        // 조회한 첫 번째 페이지:
        List<GithubCommitResponse> firstPage = List.of(
                GithubCommitResponse.builder().authorName(user.getGithubId()).message("hi").sha("sha").build());

        when(githubApiService.fetchCommits(any(RepositoryInfo.class), eq(0), anyInt(), anyString()))
                .thenReturn(firstPage);

        // when
        studyCommitService.fetchRemoteCommitsAndSave(study, todo, PAGE_SIZE);

        // then
        // githubApiService.fetchCommits가 정확히 한 번 호출되었는지 검증
        verify(githubApiService, times(1))
                .fetchCommits(any(RepositoryInfo.class), eq(0), anyInt(), anyString());

        // 두 번째 페이지에 대한 호출이 발생하지 않았음을 검증
        verify(githubApiService, never())
                .fetchCommits(any(RepositoryInfo.class), eq(1), anyInt(), anyString());

        assertEquals(1, studyCommitRepository.findAll().size());
    }

}
