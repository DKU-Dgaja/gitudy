package com.takseha.presentation.viewmodel.feed

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.takseha.data.api.gitudy.GitudyBookmarksService
import com.takseha.data.dto.feed.StudyCountResponse
import com.takseha.data.dto.feed.StudyInfo
import com.takseha.data.dto.feed.StudyRankResponse
import com.takseha.data.dto.profile.Bookmark
import com.takseha.data.repository.gitudy.GitudyBookmarksRepository
import com.takseha.data.repository.gitudy.GitudyStudyRepository
import com.takseha.presentation.adapter.FeedRVAdapter
import com.takseha.presentation.viewmodel.common.BaseViewModel
import com.takseha.presentation.viewmodel.home.MyStudyWithTodo
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FeedHomeViewModel : BaseViewModel() {
    private var gitudyStudyRepository = GitudyStudyRepository()
    private var gitudyBookmarksRepository = GitudyBookmarksRepository()

    private var _uiState = MutableStateFlow(FeedHomeUiState())
    val uiState = _uiState.asStateFlow()

    // stateflow로 바꾸는 거도 고민해보기~ 초기값 null 설정 가정
    private var _cursorIdxRes = MutableLiveData<Long?>()
    val cursorIdxRes: LiveData<Long?>
        get() = _cursorIdxRes

    fun getFeedList(cursorIdx: Long?, limit: Long, sortBy: String) = viewModelScope.launch {
        val result = safeApiResponse {
            val feedListResponse = async {
                gitudyStudyRepository.getStudyList(cursorIdx, limit, sortBy, myStudy = false)
            }
            val studyCnt = async { getStudyCount()?.count ?: -1 }

            Pair(feedListResponse.await(), studyCnt.await())
        }

        result?.let { (feedListResponse, studyCnt) ->
            if (feedListResponse.isSuccessful) {
                val feedStudyListInfo = feedListResponse.body()!!
                _cursorIdxRes.value = feedStudyListInfo.cursorIdx

                if (feedStudyListInfo.studyInfoList.isEmpty()) {
                    _uiState.update {
                        it.copy(
                            isFeedEmpty = true,
                            studyCnt = studyCnt
                        )
                    }
                } else {
                    launch {
                        val studiesInfoWithBookmarkStatus =
                            feedStudyListInfo.studyInfoList.map { study ->
                                val bookmarkStatus = async { checkBookmarkStatus(study.id) }.await()
                                val rank = async { getStudyRank(study.id)!!.ranking }.await()
                                StudyInfoWithBookmarkStatus(
                                    studyInfo = study,
                                    rank = rank,
                                    isMyBookmark = bookmarkStatus
                                )
                            }
                        _uiState.update {
                            it.copy(
                                studyInfoList = studiesInfoWithBookmarkStatus,
                                studyCategoryMappingMap = feedStudyListInfo.studyCategoryMappingMap,
                                studyCnt = studyCnt,
                                isFeedEmpty = false
                            )
                        }
                    }
                }
            } else {
                Log.e(
                    "FeedHomeViewModel",
                    "feedListResponse status: ${feedListResponse.code()}\nfeedListResponse message: ${
                        feedListResponse.errorBody()?.string()
                    }"
                )
            }
        } ?: Log.e("FeedHomeViewModel", "API 호출 실패")
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

    private suspend fun getStudyCount(): StudyCountResponse? {
        return try {
            val response = gitudyStudyRepository.getStudyCount(false)
            if (response.isSuccessful) {
                response.body()
            } else {
                Log.e(
                    "MainHomeViewModel",
                    "studyCntResponse status: ${response.code()}\nstudyCntResponse message: ${response.message()}"
                )
                null
            }
        } catch (e: Exception) {
            Log.e("MainHomeViewModel", "Error fetching getUserInfo()", e)
            null
        }
    }
}

data class FeedHomeUiState(
    var studyInfoList: List<StudyInfoWithBookmarkStatus> = listOf(),
    var studyCategoryMappingMap: Map<Int, List<String>> = mapOf(),
    var studyCnt: Int? = null,
    var isFeedEmpty: Boolean = false
)

data class StudyInfoWithBookmarkStatus(
    val studyInfo: StudyInfo,
    val rank: Int,
    val isMyBookmark: Boolean
)