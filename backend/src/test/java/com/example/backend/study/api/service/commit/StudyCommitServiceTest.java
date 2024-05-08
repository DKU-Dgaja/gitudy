package com.example.backend.study.api.service.commit;

import com.example.backend.TestConfig;
import com.example.backend.common.exception.commit.CommitException;
import com.example.backend.domain.define.account.user.User;
import com.example.backend.domain.define.account.user.repository.UserRepository;
import com.example.backend.domain.define.study.commit.StudyCommit;
import com.example.backend.domain.define.study.commit.StudyCommitFixture;
import com.example.backend.domain.define.study.commit.constant.CommitStatus;
import com.example.backend.domain.define.study.commit.repository.StudyCommitRepository;
import com.example.backend.domain.define.study.convention.StudyConvention;
import com.example.backend.domain.define.study.convention.repository.StudyConventionRepository;
import com.example.backend.domain.define.study.info.StudyInfo;
import com.example.backend.domain.define.study.info.constant.RepositoryInfo;
import com.example.backend.domain.define.study.info.constant.StudyStatus;
import com.example.backend.domain.define.study.info.repository.StudyInfoRepository;
import com.example.backend.domain.define.study.member.StudyMemberFixture;
import com.example.backend.domain.define.study.member.repository.StudyMemberRepository;
import com.example.backend.domain.define.study.todo.StudyTodoFixture;
import com.example.backend.domain.define.study.todo.info.StudyTodo;
import com.example.backend.domain.define.study.todo.repository.StudyTodoRepository;
import com.example.backend.study.api.service.commit.response.CommitInfoResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

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

    @Autowired
    private StudyMemberRepository studyMemberRepository;

    @AfterEach
    void tearDown() {
        userRepository.deleteAllInBatch();
        studyCommitRepository.deleteAllInBatch();
        studyInfoRepository.deleteAllInBatch();
        studyTodoRepository.deleteAllInBatch();
        studyInfoRepository.deleteAllInBatch();
        studyMemberRepository.deleteAllInBatch();
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
    void 레포지토리에서_aBc123에_해당하는_커밋_fetch_성공_테스트() {
        // given
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

        // 레포지토리의 총 10개의 커밋 중 컨벤션 지킨 것 5개 (순서대로)
        /*
                aBc123 [jjjjssssuuunngg] 백준: 컨벤션 지키기
                qwe321 [jusung-c] 백준: 컨벤션 지키기
                aBc123 [jusung-c] 백준: 컨벤션 수칙 지키기
                qwe321 [jusung-c] 프로그래머스: 컨벤션 지키기
                aBc123 [jusung-c] 백준: 크리스마스 트리
        */

        // 그 중 aBc123 투두에 해당하는 커밋 3개
        /*
                aBc123 [jjjjssssuuunngg] 백준: 컨벤션 지키기
                aBc123 [jusung-c] 백준: 컨벤션 수칙 지키기
                aBc123 [jusung-c] 백준: 크리스마스 트리
        */
        int expectedSize = 3;

        String A = "aBc123 [jjjjssssuuunngg] 백준: 컨벤션 지키기";
        String B = "aBc123 [jusung-c] 백준: 컨벤션 수칙 지키기";
        String C = "aBc123 [jusung-c] 백준: 크리스마스 트리";

        // when
        studyCommitService.fetchRemoteCommitsAndSave(study, todo);

        // 저장된 커밋들 중 컨벤션을 지킨 APPROVAL 상태의 커밋만 필터링
        List<StudyCommit> allCommits = studyCommitRepository.findByStudyTodoId(todo.getId())
                .stream()
                .filter(commit -> commit.getStatus() == CommitStatus.COMMIT_APPROVAL)
                .toList();
//        System.out.println("allCommits.size() = " + allCommits.size());
//        for (var c : allCommits) {
//            System.out.println(c.getMessage());
//        }

        // then
        assertEquals(allCommits.size(), expectedSize);
        for (var c : allCommits) {
            assertEquals(c.getStudyInfoId(), study.getId());
            assertTrue(c.getUserId() == userA.getId()
                    || c.getUserId() == userB.getId());
        }

        assertEquals(A, allCommits.get(0).getMessage());
        assertEquals(B, allCommits.get(1).getMessage());
        assertEquals(C, allCommits.get(2).getMessage());

    }

    @Test
    void 커밋_승인_성공_테스트() {
        // given
        Long studyId = 1L;
        Long userId = 1L;
        Long studyTodoId = 1L;
        String commitSha = "123";

        StudyCommit savedCommit = studyCommitRepository.save(StudyCommitFixture.createDefaultStudyCommit(userId, studyId, studyTodoId, commitSha));

        // when
        studyCommitService.approveCommit(savedCommit.getId());

        // then
        StudyCommit commit = studyCommitRepository.findById(savedCommit.getId()).get();
        assertEquals(commit.getStatus(), CommitStatus.COMMIT_APPROVAL);
    }

    @Test
    void 커밋_거절_성공_테스트() {
        // given
        Long studyId = 1L;
        Long userId = 1L;
        Long studyTodoId = 1L;
        String commitSha = "123";

        String rejectionReason = "동작하지 않는 코드입니다.";

        StudyCommit savedCommit = studyCommitRepository.save(StudyCommitFixture.createDefaultStudyCommit(userId, studyId, studyTodoId, commitSha));

        // when
        studyCommitService.rejectCommit(savedCommit.getId(), rejectionReason);

        // then
        StudyCommit commit = studyCommitRepository.findById(savedCommit.getId()).get();
        assertEquals(commit.getStatus(), CommitStatus.COMMIT_REJECTION);
        assertEquals(commit.getRejectionReason(), rejectionReason);
    }

    @Test
    void 대기중인_커밋_리스트_조회_테스트() {
        // given
        Long studyId = 1L;
        Long userId = 1L;
        Long studyTodoId = 1L;
        Set<Integer> usedValues = new HashSet<>();

        List<StudyCommit> waitingCommits = StudyCommitFixture.createWaitingStudyCommitList(15, userId, studyId, studyTodoId, usedValues);
        List<StudyCommit> commits = StudyCommitFixture.createDefaultStudyCommitList(10, userId, studyId, studyTodoId, usedValues);

        studyCommitRepository.saveAll(waitingCommits);
        studyCommitRepository.saveAll(commits);

        // when
        List<CommitInfoResponse> waintingList = studyCommitService.selectWaitingCommit(studyId);

        // then
        assertEquals(waintingList.size(), waitingCommits.size());
        for (var a : waintingList) {
            assertEquals(a.getStatus(), CommitStatus.COMMIT_WAITING);
        }

    }

}