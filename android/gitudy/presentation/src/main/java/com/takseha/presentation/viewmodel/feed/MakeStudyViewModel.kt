package com.takseha.presentation.viewmodel.feed

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.takseha.data.dto.feed.MakeStudyRequest
import com.takseha.data.dto.feed.StudyPeriodStatus
import com.takseha.data.dto.feed.StudyStatus
import com.takseha.data.dto.mystudy.RepositoryInfo
import com.takseha.data.repository.study.GitudyStudyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MakeStudyViewModel: ViewModel() {
    private lateinit var gitudyStudyRepository: GitudyStudyRepository
    private val backgroundColorList = listOf("#00BE93", "#00A19A", "#008291", "#08647A", "#386C5F", "#6E9B7B")
    val randIdx = (0..5).random()

    private val _newStudyInfoState = MutableStateFlow(MakeStudyRequest())
    val newStudyInfoState = _newStudyInfoState.asStateFlow()

    fun setStudyIntro(title: String, detail: String, githubRepo: String) {
        _newStudyInfoState.update { it.copy(topic = title, info = detail, repositoryName = githubRepo) }
    }
    fun setStudyRule(commitTimes: StudyPeriodStatus, isPublic: StudyStatus, maxMember: Int) {
        _newStudyInfoState.update { it.copy(periodType = commitTimes, status = isPublic, maximumMember = maxMember, profileImageUrl = backgroundColorList[randIdx]) }
    }
    fun makeNewStudy() = viewModelScope.launch {
        gitudyStudyRepository = GitudyStudyRepository()

        val request = newStudyInfoState.value
        Log.d("MakeStudyViewModel", request.toString())

        val newStudyResponse = gitudyStudyRepository.makeNewStudy(request)

        if (newStudyResponse.isSuccessful) {
            Log.d("MakeStudyViewModel", newStudyResponse.code().toString())
        } else {
            Log.e("MakeStudyViewModel", "newStudyResponse status: ${newStudyResponse.code()}\nnewStudyResponse message: ${newStudyResponse.errorBody()?.string()}")
        }
    }
}