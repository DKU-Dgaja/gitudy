package com.takseha.presentation.viewmodel.feed

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.takseha.common.model.SPKey
import com.takseha.common.util.SP
import com.takseha.data.dto.feed.MakeStudyRequest
import com.takseha.data.dto.feed.StudyPeriod
import com.takseha.data.repository.GitudyRepository
import dagger.hilt.android.internal.Contexts.getApplication
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MakeStudyViewModel(application: Application) : AndroidViewModel(application) {
    private lateinit var gitudyRepository : GitudyRepository
    private val prefs = SP(getApplication())

    private val _newStudyInfoState = MutableStateFlow(MakeStudyRequest())
    private val newStudyInfoState = _newStudyInfoState.asStateFlow()

    fun setStudyIntro(title: String, detail: String, githubRepo: String) {
        // TODO: github repo 관련 구현
        _newStudyInfoState.update { it.copy(topic = title, info = detail) }
    }
    fun setStudyCommitRule(commitTimes: StudyPeriod, dueDate: String, maxMember: Int) {
        _newStudyInfoState.update { it.copy(periodType = commitTimes.toString(), maximumMember = maxMember) }
    }
    fun setStudyRecruitRule(isPublic: Boolean) {

    }
    fun makeNewStudy(input: String) = viewModelScope.launch {
        gitudyRepository = GitudyRepository()

        val bearerToken = "Bearer ${prefs.loadPref(SPKey.ACCESS_TOKEN, "0")} ${
            prefs.loadPref(
                SPKey.REFRESH_TOKEN,
                "0"
            )
        }"

        val request = newStudyInfoState.value

        val newStudyResponse = gitudyRepository.makeNewStudy(bearerToken, request)

        if (newStudyResponse.isSuccessful) {
            val resCode = newStudyResponse.body()!!.resCode
            val resMsg = newStudyResponse.body()!!.resMsg
            val resObj = newStudyResponse.body()!!.resObj

            if (resCode == 200 && resMsg == "OK") {
                Log.d("MakeStudyViewModel", "New study registered successfully!")
            } else {
                Log.e("MakeStudyViewModel", "https status error: $resCode, $resMsg")
            }
        } else {
            Log.e("MakeStudyViewModel", "tokenResponse status: ${newStudyResponse.code()}\ntokenResponse message: ${newStudyResponse.message()}")
        }
    }
}