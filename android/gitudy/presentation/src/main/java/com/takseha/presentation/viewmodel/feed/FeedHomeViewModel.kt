package com.takseha.presentation.viewmodel.feed

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.takseha.data.dto.feed.StudyInfo
import com.takseha.data.repository.study.GitudyStudyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FeedHomeViewModel : ViewModel() {
    private var gitudyStudyRepository = GitudyStudyRepository()

    private var _uiState = MutableStateFlow(FeedHomeUiState())
    val uiState = _uiState.asStateFlow()

    // stateflow로 바꾸는 거도 고민해보기~ 초기값 null 설정 가정
    private var _cursorIdxRes = MutableLiveData<Long?>()
    val cursorIdxRes: LiveData<Long?>
        get() = _cursorIdxRes

    fun getFeedList(cursorIdx: Long?, limit: Long, sortby: String) = viewModelScope.launch {
        val feedListResponse = gitudyStudyRepository.getStudyList(
            cursorIdx,
            limit,
            sortby,
            myStudy = false
        )

        if (feedListResponse.isSuccessful) {
            val feedStudyListInfo = feedListResponse.body()!!

            _cursorIdxRes.value = feedStudyListInfo.cursorIdx
            Log.d("FeedHomeViewModel", _cursorIdxRes.value.toString())

            if (feedStudyListInfo.studyInfoList.isEmpty()) {
                _uiState.update {
                    it.copy(
                        isFeedEmpty = true
                    )
                }
            } else {
                _uiState.update { it.copy(
                    studyInfoList = feedStudyListInfo.studyInfoList,
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
}

data class FeedHomeUiState(
    var studyInfoList: List<StudyInfo> = listOf(),
    var isFeedEmpty: Boolean = false
)