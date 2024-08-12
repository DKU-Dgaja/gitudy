package com.example.backend.study.api.service.commit;

import com.example.backend.TestConfig;
import com.example.backend.auth.config.fixture.UserFixture;
import com.example.backend.common.exception.commit.CommitException;
import com.example.backend.domain.define.account.user.User;
import com.example.backend.domain.define.account.user.repository.UserRepository;
import com.example.backend.domain.define.study.commit.StudyCommit;
import com.example.backend.domain.define.study.commit.StudyCommitFixture;
import com.example.backend.domain.define.study.commit.constant.CommitStatus;
import com.example.backend.domain.define.study.commit.repository.StudyCommitRepository;
import com.example.backend.domain.define.study.convention.repository.StudyConventionRepository;
import com.example.backend.domain.define.study.info.StudyInfo;
import com.example.backend.domain.define.study.info.StudyInfoFixture;
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

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import static com.example.backend.domain.define.study.commit.StudyCommitFixture.createDefaultStudyCommitList;
import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("NonAsciiCharacters")
class StudyCommitServiceTest extends TestConfig {
    private final static int DATA_SIZE = 5;
    private final static Long LIMIT = 5L;

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
        studyMemberRepository.deleteAllInBatch();
        studyConventionRepository.deleteAllInBatch();
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
    void 커밋_승인_성공_테스트() {
        // given
        User user = userRepository.save(UserFixture.generateAuthUser());
        StudyInfo studyInfo = studyInfoRepository.save(StudyInfoFixture.createDefaultPublicStudyInfo(user.getId()));
        studyMemberRepository.save(StudyMemberFixture.createDefaultStudyMember(user.getId(), studyInfo.getId()));

        StudyTodo studyTodo = studyTodoRepository.save(StudyTodoFixture.createStudyTodo(studyInfo.getId()));

        String commitSha = "123";

        StudyCommit savedCommit = studyCommitRepository.save(StudyCommitFixture.createDefaultStudyCommit(user.getId(), studyInfo.getId(), studyTodo.getId(), commitSha));
        // when
        studyCommitService.approveCommit(savedCommit.getId());

        // then
        StudyCommit commit = studyCommitRepository.findById(savedCommit.getId()).get();
        assertEquals(commit.getStatus(), CommitStatus.COMMIT_APPROVAL);
    }

    @Test
    void 이미_승인된_커밋에_대한_중복_승인_방지_테스트() {
        // given
        User user = userRepository.save(UserFixture.generateAuthUser());
        StudyInfo studyInfo = studyInfoRepository.save(StudyInfoFixture.createDefaultPublicStudyInfo(user.getId()));
        studyMemberRepository.save(StudyMemberFixture.createDefaultStudyMember(user.getId(), studyInfo.getId()));

        StudyTodo studyTodo = studyTodoRepository.save(StudyTodoFixture.createStudyTodo(studyInfo.getId()));

        String commitSha = "123";

        StudyCommit savedCommit = studyCommitRepository.save(
                StudyCommitFixture.createDefaultStudyCommit(user.getId(), studyInfo.getId(), studyTodo.getId(), commitSha));
        // 커밋을 승인 처리
        studyCommitService.approveCommit(savedCommit.getId());

        // when
        // 다시 동일한 커밋을 승인 처리
        studyCommitService.approveCommit(savedCommit.getId());

        // then
        StudyCommit commit = studyCommitRepository.findById(savedCommit.getId()).get();
        assertEquals(CommitStatus.COMMIT_APPROVAL, commit.getStatus());
        assertNotNull(commit.getModifiedDateTime());
    }


    @Test
    void 커밋_거절_성공_테스트() {
        // given
        User user = userRepository.save(UserFixture.generateAuthUser());
        StudyInfo studyInfo = studyInfoRepository.save(StudyInfoFixture.createDefaultPublicStudyInfo(user.getId()));
        studyMemberRepository.save(StudyMemberFixture.createDefaultStudyMember(user.getId(), studyInfo.getId()));

        StudyTodo studyTodo = studyTodoRepository.save(StudyTodoFixture.createStudyTodo(studyInfo.getId()));

        String commitSha = "123";

        String rejectionReason = "동작하지 않는 코드입니다.";

        StudyCommit savedCommit = studyCommitRepository.save(StudyCommitFixture.createDefaultStudyCommit(user.getId(), studyInfo.getId(), studyTodo.getId(), commitSha));
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