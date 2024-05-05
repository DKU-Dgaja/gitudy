package com.takseha.presentation.viewmodel.feed

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.takseha.common.model.SPKey
import com.takseha.common.util.SP
import com.takseha.data.dto.mystudy.StudyInfo
import com.takseha.data.dto.mystudy.StudyListInfo
import com.takseha.data.repository.study.GitudyStudyRepository
import kotlinx.coroutines.launch

class FeedHomeViewModel(application: Application) : AndroidViewModel(application) {
    private var gitudyStudyRepository = GitudyStudyRepository()
    private val prefs = SP(getApplication())

    private val bearerToken = "Bearer ${prefs.loadPref(SPKey.ACCESS_TOKEN, "0")} ${
        prefs.loadPref(SPKey.REFRESH_TOKEN, "0")}"

    private var _feedStudyInfo = MutableLiveData<List<StudyInfo>>()
    val feedStudyInfo : LiveData<List<StudyInfo>>
        get() = _feedStudyInfo

    //live data로 할까?
    private var _cursorIdxx = MutableLiveData<Long?>()
    val cursorIdxx : LiveData<Long?>
        get() = _cursorIdxx

    fun getFeedList(cursorIdx: Long?) = viewModelScope.launch {
        val myStudyListResponse = gitudyStudyRepository.getStudyList(bearerToken, cursorIdx, myStudy = false)

        if (myStudyListResponse.isSuccessful) {
            val resCode = myStudyListResponse.body()!!.resCode
            val resMsg = myStudyListResponse.body()!!.resMsg
            val feedStudyListInfo = myStudyListResponse.body()!!.studyListInfo


            if (resCode == 200 && resMsg == "OK") {
                _cursorIdxx.value = feedStudyListInfo.cursorIdx
                _feedStudyInfo.value = feedStudyListInfo.studyInfoList

                Log.d("FeedHomeViewModel", _cursorIdxx.value.toString())
            } else {
                Log.e("FeedHomeViewModel", "https status error: $resCode, $resMsg")
            }
        } else {
            Log.e("FeedHomeViewModel", "tokenResponse status: ${myStudyListResponse.code()}\ntokenResponse message: ${myStudyListResponse.message()}")
        }
    }
}