package com.example.backend.study.api.service.info;

import com.example.backend.common.exception.ExceptionMessage;
import com.example.backend.common.exception.study.StudyInfoException;
import com.example.backend.domain.define.account.user.User;
import com.example.backend.domain.define.account.user.repository.UserRepository;
import com.example.backend.domain.define.study.category.info.StudyCategory;
import com.example.backend.domain.define.study.category.info.repository.StudyCategoryRepository;
import com.example.backend.domain.define.study.category.mapping.StudyCategoryMapping;
import com.example.backend.domain.define.study.category.mapping.repository.StudyCategoryMappingRepository;
import com.example.backend.domain.define.study.info.StudyInfo;
import com.example.backend.domain.define.study.info.repository.StudyInfoRepository;
import com.example.backend.domain.define.study.member.StudyMember;
import com.example.backend.domain.define.study.member.repository.StudyMemberRepository;
import com.example.backend.study.api.controller.info.request.StudyInfoRegisterRequest;
import com.example.backend.study.api.controller.info.request.StudyInfoUpdateRequest;
import com.example.backend.study.api.controller.info.response.MyStudyInfoListAndCursorIdxResponse;
import com.example.backend.study.api.controller.info.response.MyStudyInfoListResponse;
import com.example.backend.study.api.controller.info.response.StudyInfoRegisterResponse;
import com.example.backend.study.api.controller.info.response.UpdateStudyInfoPageResponse;
import com.example.backend.study.api.service.info.response.StudyCategoryMappingListResponse;
import com.example.backend.study.api.service.info.response.StudyMemberNameAndProfileImageResponse;
import com.example.backend.study.api.service.info.response.StudyMembersIdListResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static com.example.backend.domain.define.study.member.constant.StudyMemberRole.STUDY_LEADER;
import static com.example.backend.domain.define.study.member.constant.StudyMemberStatus.STUDY_ACTIVE;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StudyInfoService {

    private final StudyInfoRepository studyInfoRepository;

    private final StudyMemberRepository memberRepository;

    private final StudyCategoryMappingRepository studyCategoryMappingRepository;

    private final StudyMemberRepository studyMemberRepository;

    private final StudyCategoryRepository studyCategoryRepository;

    private final UserRepository userRepository;
    @Transactional
    public StudyInfoRegisterResponse registerStudy(StudyInfoRegisterRequest request) {
        // 새로운 스터디 생성
        StudyInfo studyInfo = saveStudyInfo(request);

        // 스터디장 생성
        StudyMember studyMember = saveStudyMember(request, studyInfo);

        // 스터디 카테고리 매핑
        List<Long> categories = saveStudyCategoryMappings(request.getCategoriesId(), studyInfo);

        return StudyInfoRegisterResponse.of(studyInfo, categories);
    }


    @Transactional
    public void updateStudyInfo(StudyInfoUpdateRequest request, Long studyInfoId) {
        // Study 조회
        StudyInfo studyInfo = studyInfoRepository.findById(studyInfoId).orElseThrow(() -> {
            log.warn(">>>> {} : {} <<<<", studyInfoId, ExceptionMessage.STUDY_INFO_NOT_FOUND.getText());
            return new StudyInfoException(ExceptionMessage.STUDY_INFO_NOT_FOUND);
        });

        // 변경 전 카테고리 매핑 삭제
        studyCategoryMappingRepository.deleteByStudyInfoId(studyInfo.getId());

        // 변경 후 카테고리 매핑 생성
        List<Long> categories = saveStudyCategoryMappings(request.getCategoriesId(), studyInfo);

        // 스터디 업데이트
        studyInfo.updateStudyInfo(request);
    }

    // 스터디 삭제
    @Transactional
    public boolean deleteStudy(Long studyInfoId) {
        // 스터디가 있는지 확인
        StudyInfo studyInfo = studyInfoRepository.findById(studyInfoId).orElseThrow(() -> {
            log.warn(">>>> {} : {} <<<<", studyInfoId, ExceptionMessage.STUDY_INFO_NOT_FOUND.getText());
            throw new StudyInfoException(ExceptionMessage.STUDY_INFO_NOT_FOUND);
        });

        // 스터디 상태정보 변경
        studyInfo.updateDeletedStudy();

        // 스터디 멤버 상태정보 변경
        updateWithdrawalStudyMember(studyInfoId);

        return true;
    }

    public UpdateStudyInfoPageResponse updateStudyInfoPage(Long studyInfoId) {
        // Study 조회
        StudyInfo studyInfo = studyInfoRepository.findById(studyInfoId).orElseThrow(() -> {
            log.warn(">>>> {} : {} <<<<", studyInfoId, ExceptionMessage.STUDY_INFO_NOT_FOUND.getText());
            return new StudyInfoException(ExceptionMessage.STUDY_INFO_NOT_FOUND);
        });
        List<Long> categoriesId = getCategoriesId(studyInfoId);

        UpdateStudyInfoPageResponse response = getUpdateStudyInfoPageResponse(studyInfo, categoriesId);

        return response;
    }

    // 정렬된 모든 마이 스터디 조회
    public MyStudyInfoListAndCursorIdxResponse selectMyStudyInfoList(Long userId, Long cursorIdx, Long limit, String sortBy) {
        List<MyStudyInfoListResponse> studyInfoListResponse = studyInfoRepository.findMyStudyInfoListByParameter_CursorPaging(userId, cursorIdx, limit, sortBy);

        // studyInfoIdList 만들기
        List<Long> studyInfoIdList = getStudyInfoIdList(studyInfoListResponse);

        // 활동중인 StudyMemberList 만들기
        List<StudyMember> studyMemberList = studyMemberRepository.findActiveStudyMemberListByStudyInfoIdList(studyInfoIdList);

        // studyMemberIdList 만들기
        List<Long> studyMemberIdList = getStudyMemberIdList(studyMemberList);

        // studyMemberIdList로 UserList를 select하기
        List<User> userList = userRepository.findUserListByStudyMemberIdList(studyMemberIdList);

        // Map<USER_ID, USER>
        Map<Long, User> userMap = getUserMap(userList);

        // Map<STUDY_INFO_ID, List<STUDY_MEMBER_ID>>
        Map<Long, List<StudyMember>> studyMemberMap = getStudyMemberMap(studyMemberList);

        // Map<STUDY_INFO_ID, StudyMemberNameAndProfileImageResponse>
        Map<Long, List<StudyMemberNameAndProfileImageResponse>> studyUserInfoMap = getStudyUserInfoMap(userMap, studyMemberMap);




        // studyInfoIdList로 StudyCategoryMappingList 생성
        List<StudyCategoryMapping> studyCategoryMappingList
                = studyCategoryMappingRepository.findStudyCategoryMappingListByStudyInfoIdList(studyInfoIdList);

        // studyInfoIdList에 존재하는 모든 카테고리 Id 생성
        List<Long> categoriesId = getCategoryIdWithoutDuplicates(studyCategoryMappingList);

        //  Map<STUDY_CATEGORY_ID, List<STUDY_CATEGORY_NAME>>
        Map<Long, String> studyCategoryMap = getCategoryNameMap(categoriesId);

        // Map<STUDY_INFO_ID, List<STUDY_CATEGORY_NAME>>
        Map<Long, List<String>> studyCategoryMappingMap = getStudyCategoryMappingMap(studyCategoryMappingList, studyCategoryMap);

        MyStudyInfoListAndCursorIdxResponse response = MyStudyInfoListAndCursorIdxResponse.builder()
                .studyInfoList(studyInfoListResponse)
                .studyUserInfoMap(studyUserInfoMap)
                .studyCategoryMappingMap(studyCategoryMappingMap)
                .build();
        response.setNextCursorIdx();
        return response;
    }

    // Map<STUDY_INFO_ID, StudyMemberNameAndProfileImageResponse> 생성하는 메소드
    private static Map<Long, List<StudyMemberNameAndProfileImageResponse>> getStudyUserInfoMap(Map<Long, User> userMap, Map<Long, List<StudyMember>> studyMemberMap) {
        Map<Long, List<StudyMemberNameAndProfileImageResponse>> studyUserInfoMap = new HashMap<>();

        for (Map.Entry<Long, List<StudyMember>> entry : studyMemberMap.entrySet()) {
            Long studyId = entry.getKey(); // 스터디 정보 ID 가져오기
            List<StudyMember> studyMembers = entry.getValue(); // 해당 스터디의 멤버 리스트 가져오기

            List<StudyMemberNameAndProfileImageResponse> usersInStudy = new ArrayList<>();
            for (StudyMember studyMember : studyMembers) {
                User user = userMap.get(studyMember.getUserId()); // userMap에서 해당 사용자 ID에 해당하는 User 객체 가져오기
                if (user != null) {
                    // StudyMemberNameAndProfileImageResponse 객체 생성 후 리스트에 추가
                    StudyMemberNameAndProfileImageResponse response = StudyMemberNameAndProfileImageResponse.builder()
                            .name(user.getName())
                            .profileImageUrl(user.getProfileImageUrl())
                            .build();
                    usersInStudy.add(response);
                }
            }
            studyUserInfoMap.put(studyId, usersInStudy); // 스터디 정보 ID와 해당하는 사용자 리스트를 맵에 추가
        }
        return studyUserInfoMap;
    }

    // Map<STUDY_INFO_ID, List<STUDY_MEMBER_ID>> 생성하는 메소드
    private static Map<Long, List<StudyMember>> getStudyMemberMap(List<StudyMember> studyMemberList) {
        Map<Long, List<StudyMember>> studyMemberMap = new HashMap<>();

        for (StudyMember studyMember : studyMemberList) {
            Long studyId = studyMember.getStudyInfoId();

            if (studyMemberMap.containsKey(studyId)) {
                List<StudyMember> existingList = studyMemberMap.get(studyId);
                existingList.add(studyMember);
                studyMemberMap.put(studyId, existingList);
            } else {
                List<StudyMember> newList = new ArrayList<>();
                newList.add(studyMember);
                studyMemberMap.put(studyId, newList);
            }
        }
        return studyMemberMap;
    }

    // Map<USER_ID, USER> 생성하는 메소드
    private static Map<Long, User> getUserMap(List<User> userList) {
        Map<Long, User> userMap = new HashMap<>();
        for (User user : userList) {
            userMap.put(user.getId(), user);
        }
        return userMap;
    }

    // studyMemberIdList 생성하는 메소드
    private static List<Long> getStudyMemberIdList(List<StudyMember> studyMemberList) {
        List<Long> studyMemberIdList = new ArrayList<>();
        for(int i = 0; i< studyMemberList.size(); i++){
            studyMemberIdList.add(studyMemberList.get(i).getId());
        }
        return studyMemberIdList;
    }

    // studyInfoIdList 만들어주는 함수
    private static List<Long> getStudyInfoIdList(List<MyStudyInfoListResponse> studyInfoListResponse) {
        List<Long> studyInfoIdList = new ArrayList<>();
        for(MyStudyInfoListResponse myStudyInfoListResponse: studyInfoListResponse){
            studyInfoIdList.add(myStudyInfoListResponse.getId());
        }
        return studyInfoIdList;
    }

    // Map<STUDY_INFO_ID, List<STUDY_CATEGORY_NAME>> 생성해주는 함수
    private static Map<Long, List<String>> getStudyCategoryMappingMap(List<StudyCategoryMapping> studyCategoryMappingList, Map<Long, String> studyCategoryMap) {
        Map<Long, List<String>> studyCategoryMappingMap = new HashMap<>();

        for (StudyCategoryMapping studyCategoryMapping : studyCategoryMappingList) {
            Long studyId = studyCategoryMapping.getStudyInfoId();

            if (studyCategoryMappingMap.containsKey(studyId)) {
                List<String> existingList = studyCategoryMappingMap.get(studyId);
                existingList.add(studyCategoryMap.get(studyCategoryMapping.getStudyCategoryId()));
                studyCategoryMappingMap.put(studyId, existingList);
            } else {
                List<String> newList = new ArrayList<>();
                newList.add(studyCategoryMap.get(studyCategoryMapping.getStudyCategoryId()));
                studyCategoryMappingMap.put(studyId, newList);
            }
        }
        return studyCategoryMappingMap;
    }

    //  Map<STUDY_CATEGORY_ID, List<STUDY_CATEGORY_NAME>> 생성해주는 함수
    private Map<Long, String> getCategoryNameMap(List<Long> categoriesId) {
        List<StudyCategory> studyCategoryList = studyCategoryRepository.findStudyCategoryListByCategoryIdList(categoriesId);
        Map<Long, String> studyCategoryMap = new HashMap<>();

        for (StudyCategory studyCategory : studyCategoryList) {
            Long categoryId = studyCategory.getId();
            String categoryName = studyCategory.getName();
            studyCategoryMap.put(categoryId, categoryName);
        }
        return studyCategoryMap;
    }

    // studyInfoIdList에 존재하는 모든 카테고리 Id 생성 (중복 제거)
    private static List<Long> getCategoryIdWithoutDuplicates(List<StudyCategoryMapping> studyCategoryMappingList) {
        List<Long> categoriesId = new ArrayList<>();
        for(int i = 0; i< studyCategoryMappingList.size(); i++){
            categoriesId.add(studyCategoryMappingList.get(i).getStudyCategoryId());
        }
        // 중복 제거
        Set<Long> setWithoutDuplicates = new LinkedHashSet<>(categoriesId);
        List<Long> categoriesIdWithoutDuplicates = new ArrayList<>(setWithoutDuplicates);
        return categoriesIdWithoutDuplicates;
    }

    // studyinfoId를 파라미터로 받아 카테고리 id를 생성해주는 함수
    private List<Long> getCategoriesId(Long studyInfoId) {
        List<Long> categoriesId = studyCategoryMappingRepository.findByStudyInfoId(studyInfoId)
                .stream()
                .map(StudyCategoryMapping::getStudyCategoryId)
                .collect(Collectors.toList());
        return categoriesId;
    }

    private static UpdateStudyInfoPageResponse getUpdateStudyInfoPageResponse(StudyInfo studyInfo, List<Long> categoriesId) {
        UpdateStudyInfoPageResponse response = UpdateStudyInfoPageResponse.builder()
                .userId(studyInfo.getUserId())
                .topic(studyInfo.getTopic())
                .endDate(studyInfo.getEndDate())
                .info(studyInfo.getInfo())
                .status(studyInfo.getStatus())
                .joinCode(studyInfo.getJoinCode())
                .maximumMember(studyInfo.getMaximumMember())
                .profileImageUrl(studyInfo.getProfileImageUrl())
                .repositoryInfo(studyInfo.getRepositoryInfo())
                .periodType(studyInfo.getPeriodType())
                .categoriesId(categoriesId)
                .build();
        return response;
    }

    private void updateWithdrawalStudyMember(Long studyInfoId) {
        List<StudyMember> studyMembers = studyMemberRepository.findByStudyInfoId(studyInfoId);
        studyMembers.forEach(StudyMember::updateWithdrawalStudyMember);
    }

    // 카테고리 매핑 생성해주는 함수
    private List<Long> saveStudyCategoryMappings(List<Long> categories, StudyInfo studyInfo) {
        List<Long> categoriesId = new ArrayList<>();
        List<StudyCategoryMapping> studyCategoryMapping = categories.stream()
                .peek(categoriesId::add)
                .map(categoryId -> StudyCategoryMapping.builder()
                        .studyInfoId(studyInfo.getId())
                        .studyCategoryId(categoryId)
                        .build())
                .collect(Collectors.toList());

        studyCategoryMappingRepository.saveAll(studyCategoryMapping);
        return categoriesId;
    }

    // StudyMember를 leader로 생성해주는 함수
    private StudyMember saveStudyMember(StudyInfoRegisterRequest request, StudyInfo studyInfo) {
        StudyMember studyMember = StudyMember.builder()
                .studyInfoId(studyInfo.getId())
                .userId(request.getUserId())
                .role(STUDY_LEADER)
                .status(STUDY_ACTIVE)
                .score(0)
                .build();
        return memberRepository.save(studyMember);
    }

    // StudyInfo를 생성해주는 함수
    private StudyInfo saveStudyInfo(StudyInfoRegisterRequest request) {
        StudyInfo studyInfo = StudyInfo.builder()
                .userId(request.getUserId())
                .topic(request.getTopic())
                .score(0)
                .endDate(request.getEndDate())
                .info(request.getInfo())
                .status(request.getStatus())
                .maximumMember(request.getMaximumMember())
                .currentMember(1)
                .lastCommitDay(null)
                .profileImageUrl(request.getProfileImageUrl())
                .notice(null)
                .repositoryInfo(request.getRepositoryInfo())
                .periodType(request.getPeriodType())
                .build();
        return studyInfoRepository.save(studyInfo);
    }
}
