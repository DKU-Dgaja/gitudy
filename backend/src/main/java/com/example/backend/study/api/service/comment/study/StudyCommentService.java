package com.example.backend.study.api.service.comment.study;

import com.example.backend.common.exception.ExceptionMessage;
import com.example.backend.common.exception.comment.study.StudyCommentException;
import com.example.backend.domain.define.study.comment.study.StudyComment;
import com.example.backend.domain.define.study.comment.study.repository.StudyCommentRepository;
import com.example.backend.domain.define.study.member.repository.StudyMemberRepository;
import com.example.backend.study.api.controller.comment.study.request.StudyCommentRegisterRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    // StudyComment 생성 로직
    private StudyComment createStudyComment(StudyCommentRegisterRequest studyCommentRegisterRequest, Long studyInfoId) {
        return StudyComment.builder()
                .studyInfoId(studyInfoId)
                .userId(studyCommentRegisterRequest.getUserId())
                .content(studyCommentRegisterRequest.getContent())
                .build();
    }
}
