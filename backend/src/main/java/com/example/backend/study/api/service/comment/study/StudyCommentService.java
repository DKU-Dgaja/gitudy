package com.example.backend.study.api.service.comment.study;

import com.example.backend.common.exception.ExceptionMessage;
import com.example.backend.common.exception.comment.study.StudyCommentException;
import com.example.backend.domain.define.account.user.User;
import com.example.backend.domain.define.study.comment.study.StudyComment;
import com.example.backend.domain.define.study.comment.study.repository.StudyCommentRepository;
import com.example.backend.domain.define.study.member.repository.StudyMemberRepository;
import com.example.backend.study.api.controller.comment.study.request.StudyCommentRegisterRequest;
import com.example.backend.study.api.controller.comment.study.request.StudyCommentUpdateRequest;
import com.example.backend.study.api.controller.comment.study.response.StudyCommentListAndCursorIdxResponse;
import com.example.backend.study.api.controller.comment.study.response.StudyCommentResponse;
import com.example.backend.study.api.service.info.StudyInfoService;
import com.example.backend.study.api.service.member.StudyMemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StudyCommentService {
    private final StudyCommentRepository studyCommentRepository;
    private final StudyMemberRepository studyMemberRepository;
    private final StudyMemberService studyMemberService;
    private final StudyInfoService studyInfoService;

    @Transactional
    public void registerStudyComment(StudyCommentRegisterRequest studyCommentRegisterRequest, Long studyInfoId, Long userId) {

        StudyComment studyComment = createStudyComment(studyCommentRegisterRequest, studyInfoId, userId);
        studyCommentRepository.save(studyComment);
    }

    @Transactional
    public void updateStudyComment(StudyCommentUpdateRequest request, Long studyInfoId, Long studyCommentId, Long userId) {
        // 스터디 조회 예외처리
        studyInfoService.findByIdOrThrowStudyInfoException(studyInfoId);

        // 댓글 조회 예외처리
        StudyComment studyComment = findByIdOrThrowStudyCommentException(studyCommentId);

        // 댓글 수정 권한 확인
        if (userId != studyComment.getUserId()) {
            log.warn(">>>> {} : {} <<<<", userId, ExceptionMessage.STUDY_COMMENT_NOT_AUTHORIZED.getText());
            throw new StudyCommentException(ExceptionMessage.STUDY_COMMENT_NOT_AUTHORIZED);
        }
        studyComment.updateStudyComment(request.getContent());
    }

    @Transactional
    public void deleteStudyComment(User user, Long studyInfoId, Long studyCommentId) {
        // 스터디 조회 예외처리
        studyInfoService.findByIdOrThrowStudyInfoException(studyInfoId);

        // 댓글 조회 예외처리
        StudyComment studyComment = findByIdOrThrowStudyCommentException(studyCommentId);

        // User 객체 조회 예외처리
        User savedUser = studyMemberService.findByIdAndPlatformTypeOrThrowUserException(user);

        // 유저가 스터디 장이거나 댓글 작성자인지 검증
        if(!studyMemberService.isTrueStudyLeader(savedUser, studyInfoId) && savedUser.getId() != studyComment.getUserId()){
            log.warn(">>>> {} : {} <<<<", savedUser.getId(), ExceptionMessage.STUDY_COMMENT_NOT_AUTHORIZED.getText());
            throw new StudyCommentException(ExceptionMessage.STUDY_COMMENT_NOT_AUTHORIZED);
        }
        studyCommentRepository.deleteById(studyCommentId);
    }
    public StudyCommentListAndCursorIdxResponse selectStudyCommentList(Long studyInfoId, Long cursorIdx, Long limit) {
        // 스터디 조회 예외처리
        studyInfoService.findByIdOrThrowStudyInfoException(studyInfoId);

        List<StudyCommentResponse> studyCommentResponseList =
                studyCommentRepository.findStudyCommentListByStudyInfoIdJoinUser(studyInfoId, cursorIdx, limit);
        StudyCommentListAndCursorIdxResponse response = (StudyCommentListAndCursorIdxResponse.builder()
                .studyCommentList(studyCommentResponseList)
                .build());
        response.getNextCursorIdx();
        return response;
    }

    public StudyComment findByIdOrThrowStudyCommentException(Long studyCommentId) {
        StudyComment studyComment = studyCommentRepository.findById(studyCommentId).orElseThrow(() -> {
            log.warn(">>>> {} : {} <<<<", studyCommentId, ExceptionMessage.STUDY_COMMENT_NOT_FOUND.getText());
            return new StudyCommentException(ExceptionMessage.STUDY_COMMENT_NOT_FOUND);
        });
        return studyComment;
    }

    // StudyComment 생성 로직
    private StudyComment createStudyComment(StudyCommentRegisterRequest studyCommentRegisterRequest, Long studyInfoId, Long userId) {
        return StudyComment.builder()
                .studyInfoId(studyInfoId)
                .userId(userId)
                .content(studyCommentRegisterRequest.getContent())
                .build();
    }
}
