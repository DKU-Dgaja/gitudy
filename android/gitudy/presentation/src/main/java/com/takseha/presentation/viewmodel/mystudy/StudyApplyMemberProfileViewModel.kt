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

class StudyApplyMemberProfileViewModel : ViewModel() {
    private var gitudyMemberRepository = GitudyMemberRepository()

    private val _applyApproveState = MutableStateFlow<Boolean?>(null)
    val applyApproveState = _applyApproveState.asStateFlow()

    fun approveApplyMember(studyInfoId: Int, applyUerId: Int, approve: Boolean) = viewModelScope.launch {
        val approveApplyMemberResponse =
            gitudyMemberRepository.approveApplyMember(studyInfoId, applyUerId, approve)

        if (approveApplyMemberResponse.isSuccessful) {
            _applyApproveState.value = true
        } else {
            _applyApproveState.value = false
            Log.e(
                "StudyCommentBoardViewModel",
                "approveApplyMemberResponse status: ${approveApplyMemberResponse.code()}\napproveApplyMemberResponse message: ${approveApplyMemberResponse.errorBody()?.string()}"
            )
        }
    }
}