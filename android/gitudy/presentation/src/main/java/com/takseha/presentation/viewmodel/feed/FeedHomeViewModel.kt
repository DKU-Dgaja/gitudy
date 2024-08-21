package com.takseha.presentation.viewmodel.feed

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.takseha.data.api.gitudy.GitudyBookmarksService
import com.takseha.data.dto.feed.StudyCountResponse
import com.takseha.data.dto.feed.StudyInfo
import com.takseha.data.repository.gitudy.GitudyBookmarksRepository
import com.takseha.data.repository.gitudy.GitudyStudyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FeedHomeViewModel : ViewModel() {
    private var gitudyStudyRepository = GitudyStudyRepository()
    private var gitudyBookmarksRepository = GitudyBookmarksRepository()

    private var _uiState = MutableStateFlow(FeedHomeUiState())
    val uiState = _uiState.asStateFlow()

    // stateflow로 바꾸는 거도 고민해보기~ 초기값 null 설정 가정
    private var _cursorIdxRes = MutableLiveData<Long?>()
    val cursorIdxRes: LiveData<Long?>
        get() = _cursorIdxRes

    fun getFeedList(cursorIdx: Long?, limit: Long, sortBy: String) = viewModelScope.launch {
        val feedListResponse = gitudyStudyRepository.getStudyList(
            cursorIdx,
            limit,
            sortBy,
            myStudy = false
        )
        val studyCnt = getStudyCount()?.count ?: -1

        if (feedListResponse.isSuccessful) {
            val feedStudyListInfo = feedListResponse.body()!!

            _cursorIdxRes.value = feedStudyListInfo.cursorIdx
            Log.d("FeedHomeViewModel", _cursorIdxRes.value.toString())

            if (feedStudyListInfo.studyInfoList.isEmpty()) {
                _uiState.update {
                    it.copy(
                        isFeedEmpty = true,
                        studyCnt = studyCnt
                    )
                }
            } else {
                _uiState.update { it.copy(
                    studyInfoList = feedStudyListInfo.studyInfoList,
                    studyCategoryMappingMap = feedStudyListInfo.studyCategoryMappingMap,
                    studyCnt = studyCnt,
                    isFeedEmpty = false
                ) }
            }
        } else {
            Log.e(
                "FeedHomeViewModel",
                "feedListResponse status: ${feedListResponse.code()}\nfeedListResponse message: ${feedListResponse.message()}"
            )
        }
    }

    fun setBookmarkStatus(studyInfoId: Int) = viewModelScope.launch {
        val setBookmarkResponse = gitudyBookmarksRepository.setBookmarkStatus(
            studyInfoId
        )

        if (setBookmarkResponse.isSuccessful) {
            Log.d("FeedHomeViewModel", setBookmarkResponse.code().toString())
        } else {
            Log.e(
                "FeedHomeViewModel",
                "setBookmarkResponse status: ${setBookmarkResponse.code()}\nsetBookmarkResponse message: ${setBookmarkResponse.errorBody()?.string()}"
            )
        }
    }

    private suspend fun getStudyCount(): StudyCountResponse? {
        val studyCntResponse = gitudyStudyRepository.getStudyCount(false)

        if (studyCntResponse.isSuccessful) {
            return studyCntResponse.body()
        } else {
            Log.e(
                "MainHomeViewModel",
                "studyCntResponse status: ${studyCntResponse.code()}\nstudyCntResponse message: ${studyCntResponse.message()}"
            )
        }
        return null
    }
}

data class FeedHomeUiState(
    var studyInfoList: List<StudyInfo> = listOf(),
    var studyCategoryMappingMap: Map<Int, List<String>> = mapOf(),
    var studyCnt: Int? = null,
    var isFeedEmpty: Boolean = false
)