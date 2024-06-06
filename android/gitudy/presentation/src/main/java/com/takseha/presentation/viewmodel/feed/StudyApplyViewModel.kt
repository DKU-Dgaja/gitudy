package com.takseha.presentation.viewmodel.feed

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.takseha.common.model.SPKey
import com.takseha.common.util.SP
import com.takseha.data.dto.feed.MessageRequest
import com.takseha.data.dto.mystudy.MyStudyInfo
import com.takseha.data.repository.member.GitudyMemberRepository
import com.takseha.data.repository.study.GitudyStudyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class StudyApplyViewModel(application: Application) : AndroidViewModel(application) {
    private var gitudyStudyRepository = GitudyStudyRepository()
    private var gitudyMemberRepository = GitudyMemberRepository()
    private val prefs = SP(getApplication())

    private val bearerToken = "Bearer ${prefs.loadPref(SPKey.ACCESS_TOKEN, "0")} ${
        prefs.loadPref(SPKey.REFRESH_TOKEN, "0")
    }"

    private val _uiState = MutableStateFlow(StudyMainInfoState())
    val uiState = _uiState.asStateFlow()

    fun getStudyInfo(studyInfoId: Int) = viewModelScope.launch {
        val myStudyInfoResponse = gitudyStudyRepository.getMyStudyInfo(bearerToken, studyInfoId)

        if (myStudyInfoResponse.isSuccessful) {
            val resCode = myStudyInfoResponse.body()!!.resCode
            val resMsg = myStudyInfoResponse.body()!!.resMsg
            val myStudyInfo = myStudyInfoResponse.body()!!.myStudyInfo


            if (resCode == 200 && resMsg == "OK") {
                _uiState.update {
                    it.copy(
                        studyInfo = myStudyInfo,
                    )
                }

                Log.d("StudyApplyViewModel", "_uiState: ${_uiState.value}")
            } else {
                Log.e("StudyApplyViewModel", "https status error: $resCode, $resMsg")
            }
        } else {
            Log.e(
                "StudyApplyViewModel",
                "myStudyInfoResponse status: ${myStudyInfoResponse.code()}\nmyStudyInfoResponse message: ${myStudyInfoResponse.message()}"
            )
        }
    }

    fun applyStudy(studyInfoId: Int, joinCode: String, message: String) = viewModelScope.launch {
        val request = MessageRequest(message)
        Log.d("StudyApplyViewModel", request.toString())

        val applyStudyResponse = gitudyMemberRepository.applyStudy(bearerToken, studyInfoId, joinCode, request)

        if (applyStudyResponse.isSuccessful) {
            val resCode = applyStudyResponse.body()!!.resCode
            val resMsg = applyStudyResponse.body()!!.resMsg
            val resObj = applyStudyResponse.body()!!.resObj

            if (resCode == 200 && resMsg == "OK") {

                Log.d("StudyApplyViewModel", resObj)
            } else {
                Log.e("StudyApplyViewModel", "https status error: $resCode, $resMsg")
            }
        } else {
            Log.e("StudyApplyViewModel", "applyStudyResponse status: ${applyStudyResponse.code()}\napplyStudyResponse message: ${applyStudyResponse.message()}")
        }
    }
}

data class StudyMainInfoState(
    var studyInfo: MyStudyInfo = MyStudyInfo()
)