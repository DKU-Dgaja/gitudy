package com.takseha.presentation.viewmodel.mystudy

import android.util.Log
import com.takseha.data.dto.mystudy.StudyApplyMemberListResponse
import com.takseha.data.repository.gitudy.GitudyMemberRepository
import com.takseha.presentation.viewmodel.common.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class StudyApplyMemberProfileViewModel : BaseViewModel() {
    private var gitudyMemberRepository = GitudyMemberRepository()

    private val _responseState = MutableStateFlow<Boolean?>(null)
    val responseState = _responseState.asStateFlow()

    suspend fun approveApplyMember(studyInfoId: Int, applyUserId: Int, approve: Boolean) {
        safeApiCall(
            apiCall = { gitudyMemberRepository.approveApplyMember(studyInfoId, applyUserId, approve) },
            onSuccess = { response ->
                if (response.isSuccessful) {
                    _responseState.value = true
                    Log.d("StudyApplyMemberProfileViewModel", response.code().toString())
                } else {
                    _responseState.value = false
                    Log.e(
                        "StudyApplyMemberProfileViewModel",
                        "approveApplyMemberResponse status: ${response.code()}\napproveApplyMemberResponse message: ${response.errorBody()?.string()}"
                    )
                }
            }
        )
    }
}