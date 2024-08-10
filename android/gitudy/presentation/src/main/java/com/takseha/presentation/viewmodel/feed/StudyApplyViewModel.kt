package com.takseha.presentation.viewmodel.feed

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.takseha.data.dto.feed.MessageRequest
import com.takseha.data.dto.mystudy.StudyInfoResponse
import com.takseha.data.repository.gitudy.GitudyMemberRepository
import com.takseha.data.repository.gitudy.GitudyStudyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class StudyApplyViewModel: ViewModel() {
    private var gitudyStudyRepository = GitudyStudyRepository()
    private var gitudyMemberRepository = GitudyMemberRepository()

    private val _uiState = MutableStateFlow(StudyMainInfoState())
    val uiState = _uiState.asStateFlow()

    private val _isApplySucceed = MutableStateFlow<Boolean?>(null)
    val isApplySucceed = _isApplySucceed.asStateFlow()

    fun getStudyInfo(studyInfoId: Int) = viewModelScope.launch {
        val studyInfoResponse = gitudyStudyRepository.getStudyInfo(studyInfoId)

        if (studyInfoResponse.isSuccessful) {
            val studyInfo = studyInfoResponse.body()!!

            _uiState.update {
                it.copy(
                    studyInfo = studyInfo
                )
            }
        } else {
            Log.e(
                "StudyApplyViewModel",
                "studyInfoResponse status: ${studyInfoResponse.code()}\nstudyInfoResponse message: ${studyInfoResponse.message()}"
            )
        }
    }

    fun applyStudy(studyInfoId: Int, joinCode: String, message: String) = viewModelScope.launch {
        val request = MessageRequest(message)
        Log.d("StudyApplyViewModel", request.toString())

        val applyStudyResponse = gitudyMemberRepository.applyStudy(studyInfoId, joinCode, request)

        if (applyStudyResponse.isSuccessful) {
            _isApplySucceed.value = true
            Log.d("StudyApplyViewModel", applyStudyResponse.code().toString())
        } else {
            _isApplySucceed.value = false
            Log.e("StudyApplyViewModel", "applyStudyResponse status: ${applyStudyResponse.code()}\napplyStudyResponse message: ${applyStudyResponse.message()}")
        }
    }
}

data class StudyMainInfoState(
    var studyInfo: StudyInfoResponse = StudyInfoResponse()
)