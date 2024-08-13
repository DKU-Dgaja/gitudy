package com.takseha.presentation.viewmodel.mystudy

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.takseha.data.dto.mystudy.StudyApplyMember
import com.takseha.data.dto.mystudy.StudyApplyMemberListResponse
import com.takseha.data.repository.gitudy.GitudyMemberRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class StudyApplyMemberListViewModel : ViewModel() {
    private var gitudyMemberRepository = GitudyMemberRepository()

    private val _uiState = MutableStateFlow<StudyApplyMemberListResponse?>(null)
    val uiState = _uiState.asStateFlow()

    fun getStudyApplyMemberList(studyInfoId: Int, cursorIdx: Long?, limit: Long) = viewModelScope.launch {
        val studyApplyMemberListResponse =
            gitudyMemberRepository.getStudyApplyMemberList(studyInfoId, cursorIdx, limit)

        if (studyApplyMemberListResponse.isSuccessful) {
            _uiState.value = studyApplyMemberListResponse.body()
        } else {
            Log.e(
                "StudyCommentBoardViewModel",
                "studyApplyMemberListResponse status: ${studyApplyMemberListResponse.code()}\nstudyApplyMemberListResponse message: ${studyApplyMemberListResponse.errorBody()?.string()}"
            )
        }
    }
}