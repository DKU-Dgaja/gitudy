package com.takseha.presentation.viewmodel.mystudy

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.takseha.data.repository.gitudy.GitudyMemberRepository
import kotlinx.coroutines.launch

class StudyApplyMemberProfileViewModel : ViewModel() {
    private var gitudyMemberRepository = GitudyMemberRepository()

    fun approveApplyMember(studyInfoId: Int, applyUserId: Int, approve: Boolean) = viewModelScope.launch {
        val approveApplyMemberResponse =
            gitudyMemberRepository.approveApplyMember(studyInfoId, applyUserId, approve)

        if (approveApplyMemberResponse.isSuccessful) {
            Log.d("StudyApplyMemberProfileViewModel", approveApplyMemberResponse.code().toString())
        } else {
            Log.e(
                "StudyApplyMemberProfileViewModel",
                "approveApplyMemberResponse status: ${approveApplyMemberResponse.code()}\napproveApplyMemberResponse message: ${approveApplyMemberResponse.errorBody()?.string()}"
            )
        }
    }
}