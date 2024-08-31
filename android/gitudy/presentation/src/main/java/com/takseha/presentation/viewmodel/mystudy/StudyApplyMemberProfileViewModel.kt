package com.takseha.presentation.viewmodel.mystudy

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.takseha.data.repository.gitudy.GitudyMemberRepository
import com.takseha.presentation.viewmodel.common.BaseViewModel
import kotlinx.coroutines.launch

class StudyApplyMemberProfileViewModel : BaseViewModel() {
    private var gitudyMemberRepository = GitudyMemberRepository()

    suspend fun approveApplyMember(studyInfoId: Int, applyUserId: Int, approve: Boolean) {
        safeApiCall(
            apiCall = { gitudyMemberRepository.approveApplyMember(studyInfoId, applyUserId, approve) },
            onSuccess = { response ->
                if (response.isSuccessful) {
                    Log.d("StudyApplyMemberProfileViewModel", response.code().toString())
                } else {
                    Log.e(
                        "StudyApplyMemberProfileViewModel",
                        "approveApplyMemberResponse status: ${response.code()}\napproveApplyMemberResponse message: ${response.errorBody()?.string()}"
                    )
                }
            }
        )
    }
}