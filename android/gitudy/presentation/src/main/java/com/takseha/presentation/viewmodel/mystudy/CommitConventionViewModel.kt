package com.takseha.presentation.viewmodel.mystudy

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.takseha.data.dto.mystudy.SetConventionRequest
import com.takseha.data.repository.study.GitudyStudyRepository
import kotlinx.coroutines.launch

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
            val resCode = newConventionResponse.body()!!.resCode
            val resMsg = newConventionResponse.body()!!.resMsg
            val resObj = newConventionResponse.body()!!.resObj

            if (resCode == 200 && resMsg == "OK") {
                Log.d("CommitConventionViewModel", resObj)
            } else {
                Log.e("CommitConventionViewModel", "https status error: $resCode, $resMsg")
            }
        } else {
            Log.e(
                "CommitConventionViewModel",
                "newConventionResponse status: ${newConventionResponse.code()}\nnewConventionResponse message: ${newConventionResponse.message()}"
            )
        }
    }
}