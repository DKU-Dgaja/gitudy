package com.takseha.presentation.viewmodel.mystudy

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.takseha.data.dto.mystudy.SetConventionRequest
import com.takseha.data.repository.study.GitudyStudyRepository
import kotlinx.coroutines.launch

// 추후 삭제 예정
class CommitConventionViewModel: ViewModel() {
    private var gitudyStudyRepository: GitudyStudyRepository = GitudyStudyRepository()

    fun setConvention(
        studyInfoId: Int,
        name: String,
        description: String,
        content: String,
        active: Boolean
    ) = viewModelScope.launch {
        val request = SetConventionRequest(active, content, description, name)
        Log.d("CommitConventionViewModel", request.toString())

        val newConventionResponse =
            gitudyStudyRepository.setConvention(studyInfoId, request)

        if (newConventionResponse.isSuccessful) {
            Log.d("CommitConventionViewModel", newConventionResponse.code().toString())
        } else {
            Log.e(
                "CommitConventionViewModel",
                "newConventionResponse status: ${newConventionResponse.code()}\nnewConventionResponse message: ${newConventionResponse.message()}"
            )
        }
    }
}