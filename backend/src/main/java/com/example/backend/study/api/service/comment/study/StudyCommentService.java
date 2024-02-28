package com.example.backend.study.api.service.comment.study;

import com.example.backend.common.exception.ExceptionMessage;
import com.example.backend.common.exception.comment.study.StudyCommentException;
import com.example.backend.common.exception.study.StudyInfoException;
import com.example.backend.domain.define.study.comment.study.StudyComment;
import com.example.backend.domain.define.study.comment.study.repository.StudyCommentRepository;
import com.example.backend.domain.define.study.info.StudyInfo;
import com.example.backend.domain.define.study.member.repository.StudyMemberRepository;
import com.example.backend.study.api.controller.comment.study.request.StudyCommentRegisterRequest;
import com.example.backend.study.api.controller.comment.study.request.StudyCommentUpdateRequest;
import com.example.backend.study.api.controller.info.request.StudyInfoUpdateRequest;
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

    @Transactional
    public void registerStudyComment(StudyCommentRegisterRequest studyCommentRegisterRequest, Long studyInfoId) {
        // 활동중인 스터디원인지 확인
        if (!studyMemberRepository.existsStudyMemberByUserIdAndStudyInfoId(studyCommentRegisterRequest.getUserId(), studyInfoId)) {
            log.warn(">>>> {} : {} : {} <<<<", studyCommentRegisterRequest.getUserId()
                    , studyInfoId
                    , ExceptionMessage.USER_NOT_STUDY_MEMBER.getText());
            throw new StudyCommentException(ExceptionMessage.USER_NOT_STUDY_MEMBER);
        }

        StudyComment studyComment = createStudyComment(studyCommentRegisterRequest, studyInfoId);
        studyCommentRepository.save(studyComment);
    }

    @Transactional
    public void updateStudyComment(StudyCommentUpdateRequest request, Long studyCommentId) {
        // StudyComment 조회
        StudyComment studyComment = studyCommentRepository.findById(studyCommentId).orElseThrow(() -> {
            log.warn(">>>> {} : {} <<<<", studyCommentId, ExceptionMessage.STUDY_COMMENT_NOT_FOUND.getText());
            return new StudyCommentException(ExceptionMessage.STUDY_COMMENT_NOT_FOUND);
        });
        // 댓글 수정 권한 확인
        if (request.getUserId() != studyComment.getUserId()) {
            log.warn(">>>> {} : {} <<<<", request.getUserId(), ExceptionMessage.STUDY_COMMENT_NOT_AUTHORIZED.getText());
            throw new StudyCommentException(ExceptionMessage.STUDY_COMMENT_NOT_AUTHORIZED);
        }
        studyComment.updateStudyComment(request.getContent());
    }

    // StudyComment 생성 로직
    private StudyComment createStudyComment(StudyCommentRegisterRequest studyCommentRegisterRequest, Long studyInfoId) {
        return StudyComment.builder()
                .studyInfoId(studyInfoId)
                .userId(studyCommentRegisterRequest.getUserId())
                .content(studyCommentRegisterRequest.getContent())
                .build();
    }
}