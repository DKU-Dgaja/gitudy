package com.takseha.presentation.viewmodel.mystudy

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.takseha.data.dto.mystudy.StudyApplyMemberListResponse
import com.takseha.data.repository.gitudy.GitudyMemberRepository
import com.takseha.presentation.viewmodel.common.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class StudyApplyMemberListViewModel : BaseViewModel() {
    private var gitudyMemberRepository = GitudyMemberRepository()

    private val _uiState = MutableStateFlow<StudyApplyMemberListResponse?>(null)
    val uiState = _uiState.asStateFlow()

    fun getStudyApplyMemberList(studyInfoId: Int, cursorIdx: Long?, limit: Long) =viewModelScope.launch {
        safeApiCall(
            apiCall = { gitudyMemberRepository.getStudyApplyMemberList(studyInfoId, cursorIdx, limit) },
            onSuccess = { response ->
                if (response.isSuccessful) {
                    _uiState.value = response.body()
                } else {
                    Log.e(
                        "StudyApplyMemberListViewModel",
                        "studyApplyMemberListResponse status: ${response.code()}\nstudyApplyMemberListResponse message: ${response.errorBody()?.string()}"
                    )
                }
            }
        )
    }
}