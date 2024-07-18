package com.takseha.presentation.viewmodel.feed

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.takseha.data.dto.feed.MessageRequest
import com.takseha.data.dto.mystudy.MyStudyInfoResponse
import com.takseha.data.repository.member.GitudyMemberRepository
import com.takseha.data.repository.study.GitudyStudyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class StudyApplyViewModel: ViewModel() {
    private var gitudyStudyRepository = GitudyStudyRepository()
    private var gitudyMemberRepository = GitudyMemberRepository()

    private val _uiState = MutableStateFlow(StudyMainInfoState())
    val uiState = _uiState.asStateFlow()

    fun getStudyInfo(studyInfoId: Int) = viewModelScope.launch {
        val myStudyInfoResponse = gitudyStudyRepository.getMyStudyInfo(studyInfoId)

        if (myStudyInfoResponse.isSuccessful) {
            val myStudyInfo = myStudyInfoResponse.body()!!

            _uiState.update {
                it.copy(
                    studyInfo = myStudyInfo,
                )
            }

            Log.d("StudyApplyViewModel", "_uiState: ${_uiState.value}")
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

        val applyStudyResponse = gitudyMemberRepository.applyStudy(studyInfoId, joinCode, request)

        if (applyStudyResponse.isSuccessful) {
            Log.d("StudyApplyViewModel", applyStudyResponse.code().toString())
        } else {
            Log.e("StudyApplyViewModel", "applyStudyResponse status: ${applyStudyResponse.code()}\napplyStudyResponse message: ${applyStudyResponse.message()}")
        }
    }
}

data class StudyMainInfoState(
    var studyInfo: MyStudyInfoResponse = MyStudyInfoResponse()
)