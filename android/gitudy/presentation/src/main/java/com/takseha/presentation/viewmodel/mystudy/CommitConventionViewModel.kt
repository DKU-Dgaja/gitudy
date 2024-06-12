package com.takseha.presentation.viewmodel.mystudy

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.takseha.common.model.SPKey
import com.takseha.common.util.SP
import com.takseha.data.dto.mystudy.MakeTodoRequest
import com.takseha.data.dto.mystudy.SetConventionRequest
import com.takseha.data.repository.study.GitudyStudyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CommitConventionViewModel(application: Application) : AndroidViewModel(application) {
    private var gitudyStudyRepository: GitudyStudyRepository = GitudyStudyRepository()
    private val prefs = SP(getApplication())

    val bearerToken = "Bearer ${prefs.loadPref(SPKey.ACCESS_TOKEN, "0")} ${
        prefs.loadPref(SPKey.REFRESH_TOKEN, "0")
    }"

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
            gitudyStudyRepository.setConvention(bearerToken, studyInfoId, request)

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