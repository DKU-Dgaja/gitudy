package com.takseha.presentation.viewmodel.feed

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.takseha.common.model.SPKey
import com.takseha.common.util.SP
import com.takseha.data.dto.feed.StudyInfo
import com.takseha.data.repository.study.GitudyStudyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FeedHomeViewModel(application: Application) : AndroidViewModel(application) {
    private var gitudyStudyRepository = GitudyStudyRepository()
    private val prefs = SP(getApplication())

    private val bearerToken = "Bearer ${prefs.loadPref(SPKey.ACCESS_TOKEN, "0")} ${
        prefs.loadPref(SPKey.REFRESH_TOKEN, "0")
    }"

    private var _uiState = MutableStateFlow(FeedHomeUiState())
    val uiState = _uiState.asStateFlow()

    // stateflow로 바꾸는 거도 고민해보기~ 초기값 null 설정 가정
    private var _cursorIdxRes = MutableLiveData<Long?>()
    val cursorIdxRes: LiveData<Long?>
        get() = _cursorIdxRes

    fun getFeedList(cursorIdx: Long?, limit: Long, sortby: String) = viewModelScope.launch {
        val feedListResponse = gitudyStudyRepository.getStudyList(
            bearerToken,
            cursorIdx,
            limit,
            sortby,
            myStudy = false
        )

        if (feedListResponse.isSuccessful) {
            val resCode = feedListResponse.body()!!.resCode
            val resMsg = feedListResponse.body()!!.resMsg
            val feedStudyListInfo = feedListResponse.body()!!.studyListInfo

            if (resCode == 200 && resMsg == "OK") {
                _cursorIdxRes.value = feedStudyListInfo.cursorIdx
                _uiState.update { it.copy(studyInfoList = feedStudyListInfo.studyInfoList) }

                Log.d("FeedHomeViewModel", _cursorIdxRes.value.toString())
            } else {
                Log.e("FeedHomeViewModel", "https status error: $resCode, $resMsg")
            }
        } else {
            Log.e(
                "FeedHomeViewModel",
                "tokenResponse status: ${feedListResponse.code()}\ntokenResponse message: ${feedListResponse.message()}"
            )
        }
    }
}

data class FeedHomeUiState(
    var studyInfoList: List<StudyInfo> = listOf()
)