package com.example.backend.study.api.service.todo;

import com.example.backend.MockTestConfig;
import com.example.backend.common.exception.ExceptionMessage;
import com.example.backend.common.exception.todo.TodoException;
import com.example.backend.domain.define.account.user.User;
import com.example.backend.domain.define.account.user.repository.UserRepository;
import com.example.backend.domain.define.fcm.listener.TodoRegisterListener;
import com.example.backend.domain.define.study.commit.StudyCommit;
import com.example.backend.domain.define.study.commit.StudyCommitFixture;
import com.example.backend.domain.define.study.commit.repository.StudyCommitRepository;
import com.example.backend.domain.define.study.github.GithubApiToken;
import com.example.backend.domain.define.study.info.StudyInfo;
import com.example.backend.domain.define.study.info.StudyInfoFixture;
import com.example.backend.domain.define.study.info.constant.RepositoryInfo;
import com.example.backend.domain.define.study.info.repository.StudyInfoRepository;
import com.example.backend.domain.define.study.member.StudyMember;
import com.example.backend.domain.define.study.member.StudyMemberFixture;
import com.example.backend.domain.define.study.member.repository.StudyMemberRepository;
import com.example.backend.domain.define.study.todo.StudyTodoFixture;
import com.example.backend.domain.define.study.todo.event.TodoRegisterMemberEvent;
import com.example.backend.domain.define.study.todo.info.StudyTodo;
import com.example.backend.domain.define.study.todo.mapping.StudyTodoMapping;
import com.example.backend.domain.define.study.todo.mapping.constant.StudyTodoStatus;
import com.example.backend.domain.define.study.todo.mapping.repository.StudyTodoMappingRepository;
import com.example.backend.domain.define.study.todo.repository.StudyTodoRepository;
import com.example.backend.study.api.controller.todo.request.StudyTodoRequest;
import com.example.backend.study.api.controller.todo.request.StudyTodoUpdateRequest;
import com.example.backend.study.api.controller.todo.response.*;
import com.example.backend.study.api.service.commit.response.CommitInfoResponse;
import com.example.backend.study.api.service.github.GithubApiService;
import com.example.backend.study.api.service.github.GithubApiTokenService;
import com.example.backend.study.api.service.member.StudyMemberService;
import com.google.firebase.messaging.FirebaseMessagingException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDate;
import java.util.*;

import static com.example.backend.auth.config.fixture.UserFixture.*;
import static com.example.backend.domain.define.study.commit.StudyCommitFixture.createDefaultStudyCommitList;
import static com.example.backend.domain.define.study.todo.mapping.constant.StudyTodoStatus.TODO_INCOMPLETE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

public class StudyTodoServiceTest extends MockTestConfig {

    @Autowired
    private StudyTodoRepository studyTodoRepository;

    @Autowired
    private StudyTodoMappingRepository studyTodoMappingRepository;

    @Autowired
    private StudyInfoRepository studyInfoRepository;

    @Autowired
    private StudyTodoService studyTodoService;

    @Autowired
    private StudyMemberService studyMemberService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StudyMemberRepository studyMemberRepository;

    @Autowired
    private StudyCommitRepository studyCommitRepository;

    @MockBean
    private TodoRegisterListener todoRegisterMemberListener;

    @MockBean
    private GithubApiTokenService githubApiTokenService;

    @MockBean
    private GithubApiService githubApiService;

    public final static String expectedTitle = "백준 1234번 풀기";
    public final static String expectedDetail = "오늘 자정까지 풀고 제출한다";
    public final static String expectedTodoLink = "https://www.acmicpc.net/";
    public final static LocalDate expectedTodoDate = LocalDate.now();
    public final static StudyTodoStatus expectedStatus = TODO_INCOMPLETE;
    public final static Long CursorIdx = null;
    public final static Long Limit = 3L;

    @AfterEach
    void tearDown() {
        studyTodoMappingRepository.deleteAllInBatch();
        studyTodoRepository.deleteAllInBatch();
        studyInfoRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
        studyMemberRepository.deleteAllInBatch();
        studyCommitRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("Todo 등록 테스트")
    public void registerTodo() {
        //given
        User leader = userRepository.save(generateAuthUser());
        User activeMember = userRepository.save(generateGoogleUser());
        User withdrawalMember = userRepository.save(generateKaKaoUser());

        StudyInfo studyInfo = StudyInfoFixture.createDefaultPublicStudyInfo(leader.getId());
        studyInfoRepository.save(studyInfo);

        studyMemberRepository.saveAll(List.of(
                StudyMemberFixture.createStudyMemberLeader(leader.getId(), studyInfo.getId()),
                StudyMemberFixture.createDefaultStudyMember(activeMember.getId(), studyInfo.getId()),
                StudyMemberFixture.createStudyMemberWithdrawal(withdrawalMember.getId(), studyInfo.getId())
        ));

        StudyTodoRequest request = StudyTodoFixture.generateStudyTodoRequest();

        when(githubApiTokenService.getToken(anyLong())).thenReturn(new GithubApiToken("test", leader.getId()));
        doNothing().when(githubApiService).createTodoFolder(any(String.class), any(StudyTodo.class), any(RepositoryInfo.class));

        //when
        studyMemberService.isValidateStudyLeader(leader, studyInfo.getId());
        studyTodoService.registerStudyTodo(request, studyInfo.getId());

        //then
        // StudyTodo
        List<StudyTodo> studyTodos = studyTodoRepository.findAll();
        assertNotNull(studyTodos);
        StudyTodo savedStudyTodo = studyTodos.get(0);
        assertEquals(studyInfo.getId(), savedStudyTodo.getStudyInfoId());

        List<StudyTodoMapping> mappings = studyTodoMappingRepository.findAll();
        // 활동중인 멤버에게 할당되었는지 확인
        assertTrue(mappings.stream()
                .anyMatch(mappingMember -> mappingMember.getUserId().equals(activeMember.getId())));
        // 비활동중인 멤버에게 할당되지 않았는지 확인
        assertFalse(mappings.stream()
                .anyMatch(mappingMember -> mappingMember.getUserId().equals(withdrawalMember.getId())));

    }

    @Test
    @DisplayName("Todo 등록 테스트 - 알림 true일 때")
    public void Todo_register_notify_true_test() throws FirebaseMessagingException {
        //given
        User leader = userRepository.save(generateAuthUserPushAlarmY());
        User user1 = userRepository.save(generateAuthUserPushAlarmYs("1"));
        User user2 = userRepository.save(generateAuthUserPushAlarmYs("2"));
        User user3 = userRepository.save(generateAuthUserPushAlarmNs("3"));

        StudyInfo studyInfo = StudyInfoFixture.createDefaultPublicStudyInfo(leader.getId());
        studyInfoRepository.save(studyInfo);

        StudyTodoRequest request = StudyTodoFixture.generateStudyTodoRequest();

        studyMemberRepository.saveAll(List.of(
                StudyMemberFixture.createStudyMemberLeader(leader.getId(), studyInfo.getId()),
                StudyMemberFixture.createDefaultStudyMember(user1.getId(), studyInfo.getId()),
                StudyMemberFixture.createDefaultStudyMember(user2.getId(), studyInfo.getId()),
                StudyMemberFixture.createDefaultStudyMember(user3.getId(), studyInfo.getId())
        ));

        when(githubApiTokenService.getToken(anyLong())).thenReturn(new GithubApiToken("test", leader.getId()));
        doNothing().when(githubApiService).createTodoFolder(any(String.class), any(StudyTodo.class), any(RepositoryInfo.class));

        //when
        studyMemberService.isValidateStudyLeader(leader, studyInfo.getId());
        studyTodoService.registerStudyTodo(request, studyInfo.getId());

        //then
        verify(todoRegisterMemberListener).todoRegisterListener(any(TodoRegisterMemberEvent.class));
    }

    @Test
    @DisplayName("Todo 수정 테스트")
    public void updateTodo() {

        //given
        User leader = userRepository.save(generateAuthUser());

        StudyInfo studyInfo = StudyInfoFixture.createDefaultPublicStudyInfo(leader.getId());
        studyInfoRepository.save(studyInfo);

        StudyTodo studyTodo = StudyTodoFixture.createStudyTodo(studyInfo.getId());
        studyTodoRepository.save(studyTodo);

        String updatedTitle = "제목변경";
        String updatedDetail = "설명변경";
        String updatedTodoLink = "링크변경";
        LocalDate updatedTodoDate = LocalDate.now().plusDays(3);

        StudyTodoUpdateRequest request = StudyTodoFixture.updateStudyTodoRequest(updatedTitle, updatedDetail, updatedTodoLink, updatedTodoDate);

        // when
        studyTodoService.updateStudyTodo(request, studyTodo.getId(), studyInfo.getId());

        // then
        StudyTodo updatedTodo = studyTodoRepository.findById(studyTodo.getId())
                .orElseThrow(() -> new TodoException(ExceptionMessage.TODO_NOT_FOUND));
        assertEquals(updatedTitle, updatedTodo.getTitle());
        assertEquals(updatedDetail, updatedTodo.getDetail());
        assertEquals(updatedTodoLink, updatedTodo.getTodoLink());
        assertEquals(updatedTodoDate, updatedTodo.getTodoDate());
    }

    @Test
    @DisplayName("Todo 삭제 테스트")
    void deleteTodo_Success() {
        // given
        User leader = userRepository.save(generateAuthUser());
        User activeMember1 = userRepository.save(generateGoogleUser());
        User activeMember2 = userRepository.save(generateKaKaoUser());

        StudyInfo studyInfo = StudyInfoFixture.createDefaultPublicStudyInfo(leader.getId());
        studyInfoRepository.save(studyInfo);

        studyMemberRepository.saveAll(List.of(
                StudyMemberFixture.createStudyMemberLeader(leader.getId(), studyInfo.getId()),
                StudyMemberFixture.createDefaultStudyMember(activeMember1.getId(), studyInfo.getId()),
                StudyMemberFixture.createDefaultStudyMember(activeMember2.getId(), studyInfo.getId())

        ));
        // 스터디장 To do 생성
        StudyTodo studyTodo = StudyTodoFixture.createStudyTodo(studyInfo.getId());
        studyTodoRepository.save(studyTodo);

        // 활동중인 스터디원들에게 To do 매핑
        studyTodoMappingRepository.saveAll(List.of(
                StudyTodoFixture.createStudyTodoMapping(studyTodo.getId(), activeMember1.getId()),
                StudyTodoFixture.createStudyTodoMapping(studyTodo.getId(), activeMember2.getId())
        ));

        // when
        studyMemberService.isValidateStudyLeader(leader, studyInfo.getId());
        studyTodoService.deleteStudyTodo(studyTodo.getId(), studyInfo.getId());  //To do 삭제

        // then
        assertThrows(TodoException.class, () -> {
            studyTodoService.deleteStudyTodo(990927L, studyInfo.getId());
        }, ExceptionMessage.TODO_NOT_FOUND.getText());

        // 등록된 To do는 삭제, 모든 매핑제거
        assertFalse(studyTodoRepository.existsById(studyTodo.getId()));
        List<StudyTodoMapping> todoMappings = studyTodoMappingRepository.findByTodoId(studyTodo.getId());
        assertTrue(todoMappings.isEmpty());

    }

    @Test
    @DisplayName("Todo 전체조회 테스트")
    void readTodoList_Success() {
        // given
        Random random = new Random();
        Long cursorIdx = Math.abs(random.nextLong()) + Limit;  // Limit 이상 랜덤값

        User leader = userRepository.save(generateAuthUser());

        StudyInfo studyInfo = StudyInfoFixture.createDefaultPublicStudyInfo(leader.getId());
        studyInfoRepository.save(studyInfo);

        // 스터디장 To do 생성
        StudyTodo studyTodo1 = StudyTodoFixture.createStudyTodo(studyInfo.getId());
        StudyTodo studyTodo2 = StudyTodoFixture.createStudyTodo(studyInfo.getId());
        StudyTodo studyTodo3 = StudyTodoFixture.createStudyTodo(studyInfo.getId());
        StudyTodo studyTodo4 = StudyTodoFixture.createStudyTodo(studyInfo.getId());
        studyTodoRepository.saveAll(List.of(studyTodo1, studyTodo2, studyTodo3, studyTodo4));

        // 커밋 생성
        StudyCommit commit1 = StudyCommitFixture.createDefaultStudyCommit(leader.getId(), studyInfo.getId(), studyTodo4.getId(), "sha1");
        StudyCommit commit2 = StudyCommitFixture.createDefaultStudyCommit(leader.getId(), studyInfo.getId(), studyTodo3.getId(), "sha2");
        studyCommitRepository.saveAll(List.of(commit1, commit2));

        // when
        StudyTodoListAndCursorIdxResponse responses = studyTodoService.readStudyTodoList(studyInfo.getId(), cursorIdx, Limit);

        // then
        assertNotNull(responses);
        assertEquals(3, responses.getTodoList().size());
        assertEquals(studyTodo1.getTitle(), responses.getTodoList().get(0).getTitle());
        assertEquals(studyTodo2.getTitle(), responses.getTodoList().get(1).getTitle());

        assertNotNull(responses.getTodoList().get(0).getTodoFolderName());
        assertNotNull(responses.getTodoList().get(1).getTodoFolderName());
        
        // 커밋 리스트 검증
        assertEquals(1, responses.getTodoList().get(0).getCommits().size());
        assertEquals(commit1.getCommitSHA(), responses.getTodoList().get(0).getCommits().get(0).getCommitSHA());
        assertEquals(1, responses.getTodoList().get(1).getCommits().size());
        assertEquals(commit2.getCommitSHA(), responses.getTodoList().get(1).getCommits().get(0).getCommitSHA());
    }

    @Test
    @DisplayName("Todo 전체조회 커서 기반 페이징 로직 검증")
    void readTodoList_CursorPaging_Success() {
        // given
        User leader = userRepository.save(generateAuthUser());
        StudyInfo studyInfo = StudyInfoFixture.createDefaultPublicStudyInfo(leader.getId());
        studyInfoRepository.save(studyInfo);

        // 7개의 스터디 To do 생성 및 저장
        List<StudyTodo> createdTodos = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            createdTodos.add(StudyTodoFixture.createStudyTodo(studyInfo.getId()));
        }
        studyTodoRepository.saveAll(createdTodos);

        // 투두당 하나의 커밋을 생성
        List<StudyCommit> commitList = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            commitList.add(StudyCommitFixture.createDefaultStudyCommit(leader.getId(), studyInfo.getId(), createdTodos.get(i).getId(), i + "sha"));
        }
        studyCommitRepository.saveAll(commitList);

        // when
        StudyTodoListAndCursorIdxResponse firstPageResponse = studyTodoService.readStudyTodoList(studyInfo.getId(), CursorIdx, Limit);

        // then
        assertNotNull(firstPageResponse);
        assertEquals(3, firstPageResponse.getTodoList().size());  // 3개만 가져와야함

        // 커밋 리스트 검증
        for (int i = 0; i < firstPageResponse.getTodoList().size(); i++) {
            StudyTodoWithCommitsResponse todoWithCommits = firstPageResponse.getTodoList().get(i);
            assertNotNull(todoWithCommits.getCommits());
            assertFalse(todoWithCommits.getCommits().isEmpty());
            assertEquals(1, todoWithCommits.getCommits().size());  // 각 투두당 하나의 커밋
            assertEquals((6 - i) + "sha", todoWithCommits.getCommits().get(0).getCommitSHA());
        }

        // 새로운 커서 인덱스를 사용하여 다음 페이지 조회
        Long newCursorIdx = firstPageResponse.getCursorIdx();
        StudyTodoListAndCursorIdxResponse secondPageResponse = studyTodoService.readStudyTodoList(studyInfo.getId(), newCursorIdx, 3L);

        // 두 번째 페이지의 데이터 검증
        assertNotNull(secondPageResponse);
        assertEquals(3, secondPageResponse.getTodoList().size());

        // 커밋 리스트 검증
        for (int i = 0; i < secondPageResponse.getTodoList().size(); i++) {
            StudyTodoWithCommitsResponse todoWithCommits = secondPageResponse.getTodoList().get(i);
            assertNotNull(todoWithCommits.getCommits());
            assertFalse(todoWithCommits.getCommits().isEmpty());
            assertEquals(1, todoWithCommits.getCommits().size());  // 각 투두당 하나의 커밋
            assertEquals((3 - i) + "sha", todoWithCommits.getCommits().get(0).getCommitSHA());
        }

        // 새로운 커서 인덱스를 사용하여 다음 페이지 조회
        Long newCursorIdx2 = secondPageResponse.getCursorIdx();
        StudyTodoListAndCursorIdxResponse thirdPageResponse = studyTodoService.readStudyTodoList(studyInfo.getId(), newCursorIdx2, 3L);

        // 세 번째 페이지의 데이터 검증
        assertNotNull(thirdPageResponse);
        assertEquals(1, thirdPageResponse.getTodoList().size());

        // 커밋 리스트 검증
        for (int i = 0; i < thirdPageResponse.getTodoList().size(); i++) {
            StudyTodoWithCommitsResponse todoWithCommits = thirdPageResponse.getTodoList().get(i);
            assertNotNull(todoWithCommits.getCommits());
            assertFalse(todoWithCommits.getCommits().isEmpty());
            assertEquals(1, todoWithCommits.getCommits().size());  // 각 투두당 하나의 커밋
            assertEquals((0) + "sha", todoWithCommits.getCommits().get(0).getCommitSHA());
        }
    }


    @Test
    @DisplayName("다른 스터디의 Todo와 섞여있을 때, 특정 스터디의 Todo만 조회 확인 테스트")
    void readTodoList_difStudy() {
        // given
        User leader1 = userRepository.save(generateAuthUser());
        StudyInfo studyInfo1 = StudyInfoFixture.createDefaultPublicStudyInfo(leader1.getId());
        studyInfoRepository.save(studyInfo1);

        User leader2 = userRepository.save(generateGoogleUser());
        StudyInfo studyInfo2 = StudyInfoFixture.createDefaultPublicStudyInfo(leader2.getId());
        studyInfoRepository.save(studyInfo2);

        // 1번 스터디와 2번 스터디의 To do 생성 저장
        List<StudyTodo> createdStudy1Todos = new ArrayList<>();
        List<StudyTodo> createdStudy2Todos = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            createdStudy1Todos.add(StudyTodoFixture.createStudyTodoWithTitle(studyInfo1.getId(), "1번 투두 제목" + i));
            createdStudy2Todos.add(StudyTodoFixture.createStudyTodoWithTitle(studyInfo2.getId(), "2번 투두 제목" + i));
        }

        // 두 리스트 합치기
        List<StudyTodo> allTodos = new ArrayList<>();
        allTodos.addAll(createdStudy1Todos);
        allTodos.addAll(createdStudy2Todos);

        // 합친 리스트 저장
        studyTodoRepository.saveAll(allTodos);

        // when
        StudyTodoListAndCursorIdxResponse responseForStudy1 = studyTodoService.readStudyTodoList(studyInfo1.getId(), CursorIdx, Limit);

        // then
        assertNotNull(responseForStudy1);
        assertEquals(Limit.intValue(), responseForStudy1.getTodoList().size());

        responseForStudy1.getTodoList().forEach(todo ->
                assertTrue(todo.getTitle().contains("1번 투두 제목"), "모든 투두 항목은 '1번 투두 제목' 을 포함해야 한다"));

        // when
        StudyTodoListAndCursorIdxResponse responseForStudy2 = studyTodoService.readStudyTodoList(studyInfo2.getId(), CursorIdx, Limit);

        // then
        assertNotNull(responseForStudy2);
        assertEquals(Limit.intValue(), responseForStudy2.getTodoList().size());

        responseForStudy2.getTodoList().forEach(todo ->
                assertTrue(todo.getTitle().contains("2번 투두 제목"), "모든 투두 항목은 '2번 투두 제목' 을 포함해야 한다"));

    }

    @Test
    @DisplayName("Todo 단일조회 테스트")
    void readStudyTodo() {
        // given
        User savedUser = userRepository.save(generateAuthUser());

        StudyInfo studyInfo = StudyInfoFixture.createDefaultPublicStudyInfo(savedUser.getId());
        studyInfoRepository.save(studyInfo);

        StudyTodo studyTodo = StudyTodoFixture.createStudyTodo(studyInfo.getId());
        StudyTodo todo = studyTodoRepository.save(studyTodo);

        //when
        StudyTodoResponse response = studyTodoService.readStudyTodo(studyInfo.getId(), studyTodo.getId());

        //then
        assertEquals("백준 1234번 풀기", studyTodo.getTitle());
        assertEquals(todo.getTodoFolderName(), response.getTodoFolderName());
    }

    @Test
    @DisplayName("스터디원들의 특정 Todo에 대한 완료여부 조회 테스트")
    void readStudyTodo_status() {
        // given
        User leader = userRepository.save(generateAuthUser());
        User member1 = userRepository.save(generateKaKaoUser());
        User member2 = userRepository.save(generateGoogleUser());

        StudyInfo studyInfo = StudyInfoFixture.createDefaultPublicStudyInfo(leader.getId());
        studyInfoRepository.save(studyInfo);

        // 스터디장 To do 생성
        StudyTodo studyTodo = StudyTodoFixture.createStudyTodo(studyInfo.getId());
        studyTodoRepository.save(studyTodo);

        StudyMember koo = StudyMemberFixture.createDefaultStudyMember(member1.getId(), studyInfo.getId());
        StudyMember Lee = StudyMemberFixture.createDefaultStudyMember(member2.getId(), studyInfo.getId());
        studyMemberRepository.saveAll(List.of(koo, Lee));

        StudyTodoMapping studyTodoMapping1 = StudyTodoFixture.createStudyTodoMapping(studyTodo.getId(), koo.getUserId());
        StudyTodoMapping studyTodoMapping2 = StudyTodoFixture.createCompleteStudyTodoMapping(studyTodo.getId(), Lee.getUserId());
        studyTodoMappingRepository.saveAll(List.of(studyTodoMapping1, studyTodoMapping2));

        // when
        List<StudyTodoStatusResponse> results = studyTodoService.readStudyTodoStatus(studyInfo.getId(), studyTodo.getId());


        // then
        assertEquals(2, results.size());
        assertTrue(results.stream().anyMatch(r -> r.getUserId().equals(koo.getUserId()) && r.getStatus() == StudyTodoStatus.TODO_INCOMPLETE));
        assertTrue(results.stream().anyMatch(r -> r.getUserId().equals(Lee.getUserId()) && r.getStatus() == StudyTodoStatus.TODO_COMPLETE));

    }

    @Test
    void 가장_빠른_마감일을_가진_Todo_진행률_조회_테스트A() {
        // given
        int expectedTotalMemberCnt = 3;
        int expectedCompleteMemberCnt = 2;

        User leader = userRepository.save(generateAuthUser());
        User member1 = userRepository.save(generateKaKaoUser());
        User member2 = userRepository.save(generateGoogleUser());

        StudyInfo studyInfo = StudyInfoFixture.createDefaultPublicStudyInfo(leader.getId());
        studyInfoRepository.save(studyInfo);

        StudyMember A = StudyMemberFixture.createStudyMemberLeader(member1.getId(), studyInfo.getId());
        StudyMember koo = StudyMemberFixture.createDefaultStudyMember(member1.getId(), studyInfo.getId());
        StudyMember Lee = StudyMemberFixture.createDefaultStudyMember(member2.getId(), studyInfo.getId());
        studyMemberRepository.saveAll(List.of(A, koo, Lee));

        // 스터디장 To do 생성
        StudyTodo studyTodoA = StudyTodoFixture.createStudyTodoByTodoDate(studyInfo.getId(), LocalDate.now().plusDays(2));
        StudyTodo studyTodoB = StudyTodoFixture.createStudyTodoByTodoDate(studyInfo.getId(), LocalDate.now().plusDays(3));
        studyTodoRepository.save(studyTodoA);
        studyTodoRepository.save(studyTodoB);

        StudyTodoMapping studyTodoMapping1 = StudyTodoFixture.createStudyTodoMapping(studyTodoA.getId(), koo.getUserId());
        StudyTodoMapping studyTodoMapping2 = StudyTodoFixture.createCompleteStudyTodoMapping(studyTodoA.getId(), Lee.getUserId());
        StudyTodoMapping studyTodoMapping3 = StudyTodoFixture.createCompleteStudyTodoMapping(studyTodoA.getId(), A.getUserId());
        studyTodoMappingRepository.saveAll(List.of(studyTodoMapping1, studyTodoMapping2, studyTodoMapping3));

        studyTodoMappingRepository.save(StudyTodoFixture.createStudyTodoMapping(studyTodoB.getId(), koo.getUserId()));

        // when
        StudyTodoProgressResponse response = studyTodoService.readStudyTodoProgress(studyInfo.getId());

        // then
        assertEquals(response.getTodo().getId(), studyTodoA.getId());
        assertEquals(response.getTodo().getTitle(), studyTodoA.getTitle());
        assertEquals(response.getTotalMemberCount(), expectedTotalMemberCnt);
        assertEquals(response.getCompleteMemberCount(), expectedCompleteMemberCnt);

    }

    @Test
    void 가장_빠른_마감일을_가진_투두_진행률_조회_시_투두가_없을_경우_빈_객체_반환_테스트() {
        // given
        User leader = userRepository.save(generateAuthUser());

        StudyInfo studyInfo = StudyInfoFixture.createDefaultPublicStudyInfo(leader.getId());
        studyInfoRepository.save(studyInfo);

        // 스터디장 To do 생성
        // when
        StudyTodoProgressResponse response = studyTodoService.readStudyTodoProgress(studyInfo.getId());

        // then
        assertNull(response.getTodo());
        assertEquals(response.getTotalMemberCount(), 0);
        assertEquals(response.getCompleteMemberCount(), 0);

    }

    @Test
    void 투두별_커밋_리스트_조회() {
        // given
        User member = userRepository.save(generateAuthUser());

        StudyInfo studyInfo = StudyInfoFixture.createDefaultPublicStudyInfo(member.getId());
        studyInfoRepository.save(studyInfo);

        StudyTodo todo = studyTodoRepository.save(StudyTodoFixture.createStudyTodo(studyInfo.getId()));

        Set<Integer> usedValues = new HashSet<>();

        List<StudyCommit> commitList = createDefaultStudyCommitList(5, member.getId(), studyInfo.getId(), todo.getId(), usedValues);
        studyCommitRepository.saveAll(commitList);

        // when
        List<CommitInfoResponse> list = studyTodoService.selectTodoCommits(todo.getId());

        // then

        for (StudyCommit commit : commitList) {
            CommitInfoResponse response = list.stream()
                    .filter(r -> r.getCommitSHA().equals(commit.getCommitSHA()))
                    .findFirst()
                    .orElse(null);

            assertThat(response).isNotNull();
            assertThat(response.getId()).isEqualTo(commit.getId());
            assertThat(response.getStudyInfoId()).isEqualTo(commit.getStudyInfoId());
            assertThat(response.getStudyTodoId()).isEqualTo(commit.getStudyTodoId());
            assertThat(response.getUserId()).isEqualTo(commit.getUserId());
            assertThat(response.getCommitSHA()).isEqualTo(commit.getCommitSHA());
        }

    }
}