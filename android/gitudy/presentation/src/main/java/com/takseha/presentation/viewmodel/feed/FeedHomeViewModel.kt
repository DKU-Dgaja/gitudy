package com.takseha.presentation.viewmodel.feed

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.takseha.data.dto.feed.StudyInfo
import com.takseha.data.dto.feed.StudyRankResponse
import com.takseha.data.repository.gitudy.GitudyBookmarksRepository
import com.takseha.data.repository.gitudy.GitudyNoticeRepository
import com.takseha.data.repository.gitudy.GitudyStudyRepository
import com.takseha.presentation.viewmodel.common.BaseViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class FeedHomeViewModel : BaseViewModel() {
    private var gitudyStudyRepository = GitudyStudyRepository()
    private var gitudyBookmarksRepository = GitudyBookmarksRepository()
    private val gitudyNoticeRepository = GitudyNoticeRepository()

    private var _uiState = MutableStateFlow(FeedHomeUiState())
    val uiState = _uiState.asStateFlow()

    // stateflow로 바꾸는 거도 고민해보기~ 초기값 null 설정 가정
    private var _cursorIdxRes = MutableLiveData<Long?>()
    val cursorIdxRes: LiveData<Long?>
        get() = _cursorIdxRes

    suspend fun getFeedList(cursorIdx: Long?, limit: Long, sortBy: String) = viewModelScope.launch {
        safeApiCall(
            apiCall = {
                gitudyStudyRepository.getStudyList(
                    cursorIdx,
                    limit,
                    sortBy,
                    myStudy = false
                )
            },
            onSuccess = { response ->
                if (response.isSuccessful) {
                    val feedStudyListInfo = response.body()!!
                    _cursorIdxRes.value = feedStudyListInfo.cursorIdx

                    if (feedStudyListInfo.studyInfoList.isEmpty()) {
                        _uiState.update {
                            it.copy(
                                studyInfoList = emptyList(),
                                isFeedEmpty = true
                            )
                        }
                    } else {
                        viewModelScope.launch {
                            val studiesInfoWithBookmarkStatus =
                                feedStudyListInfo.studyInfoList.map { study ->
                                    val bookmarkStatus = async { checkBookmarkStatus(study.id) }
                                    val rank = async { getStudyRank(study.id)?.ranking ?: 0 }
                                    StudyInfoWithBookmarkStatus(
                                        studyInfo = study,
                                        rank = rank.await(),
                                        isMyBookmark = bookmarkStatus.await()
                                    )
                                }
                            _uiState.update {
                                it.copy(
                                    studyInfoList = studiesInfoWithBookmarkStatus,
                                    studyCategoryMappingMap = feedStudyListInfo.studyCategoryMappingMap,
                                    isFeedEmpty = false
                                )
                            }
                        }
                    }
                } else {
                    Log.e(
                        "FeedHomeViewModel",
                        "feedListResponse status: ${response.code()}\nfeedListResponse message: ${
                            response.errorBody()?.string()
                        }"
                    )
                }
            }
        )
    }


    private suspend fun getStudyRank(studyInfoId: Int): StudyRankResponse? {
        return try {
            val response = gitudyStudyRepository.getStudyRank(studyInfoId)
            if (response.isSuccessful) {
                response.body()
            } else {
                Log.e(
                    "FeedHomeViewModel",
                    "studyRankResponse status: ${response.code()}\nstudyRankResponse message: ${
                        response.errorBody()?.string()
                    }"
                )
                null
            }
        } catch (e: Exception) {
            Log.e("FeedHomeViewModel", "Error fetching getUserInfo()", e)
            null
        }
    }

    private suspend fun checkBookmarkStatus(studyInfoId: Int): Boolean {
        return try {
            val response = gitudyBookmarksRepository.checkBookmarkStatus(
                studyInfoId
            )
            if (response.isSuccessful) {
                response.body()!!.myBookmark
            } else {
                Log.e(
                    "FeedHomeViewModel",
                    "bookmarkStatusResponse status: ${response.code()}\nbookmarkStatusResponse message: ${response.message()}"
                )
                false
            }
        } catch (e: Exception) {
            Log.e("FeedHomeViewModel", "Error fetching getUserInfo()", e)
            false
        }
    }

    suspend fun setBookmarkStatus(studyInfoId: Int) {
        safeApiCall(
            apiCall = {
                gitudyBookmarksRepository.setBookmarkStatus(
                    studyInfoId
                )
            },
            onSuccess = { response ->
                if (response.isSuccessful) {
                    viewModelScope.launch {
                        checkBookmarkStatus(studyInfoId)
                    }
                    Log.d("FeedHomeViewModel", response.code().toString())
                } else {
                    Log.e(
                        "FeedHomeViewModel",
                        "setBookmarkResponse status: ${response.code()}\nsetBookmarkResponse message: ${
                            response.errorBody()?.string()
                        }"
                    )
                }
            }
        )
    }

    suspend fun getStudyCount() {
        safeApiCall(
            apiCall = { gitudyStudyRepository.getStudyCount(false) },
            onSuccess = { response ->
                if (response.isSuccessful) {
                    val studyCnt = response.body()!!.count
                    _uiState.update {
                        it.copy(
                            studyCnt = studyCnt
                        )
                    }
                } else {
                    Log.e(
                        "FeedHomeViewModel",
                        "studyCountResponse status: ${response.code()}\nstudyCountResponse message: ${
                            response.errorBody()?.string()
                        }"
                    )
                }
            }
        )
    }

    fun getAlertCount(cursorTime: String?, limit: Long) =viewModelScope.launch {
        safeApiCall(
            apiCall = { gitudyNoticeRepository.getNoticeList(cursorTime, limit) },
            onSuccess = { response ->
                if (response.isSuccessful) {
                    val noticeList = response.body()

                    if (noticeList?.isEmpty() != false) {
                        _uiState.update { it.copy(
                            isAlert = false
                        ) }
                    } else {
                        _uiState.update { it.copy(
                            isAlert = true
                        ) }
                    }

                } else {
                    Log.e(
                        "FeedHomeViewModel",
                        "isAlertResponse status: ${response.code()}\nisAlertResponse message: ${response.errorBody()?.string()}"
                    )
                }
            }
        )
    }
}

data class FeedHomeUiState(
    var studyInfoList: List<StudyInfoWithBookmarkStatus> = listOf(),
    var studyCategoryMappingMap: Map<Int, List<String>> = mapOf(),
    var studyCnt: Int? = null,
    var isFeedEmpty: Boolean? = null,
    val isAlert: Boolean = false
)

data class StudyInfoWithBookmarkStatus(
    val studyInfo: StudyInfo,
    val rank: Int,
    val isMyBookmark: Boolean
)