package com.takseha.presentation.viewmodel.feed

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.takseha.data.dto.feed.Category
import com.takseha.data.dto.feed.CheckRepoNameRequest
import com.takseha.data.dto.feed.MakeStudyRequest
import com.takseha.data.dto.feed.StudyPeriodStatus
import com.takseha.data.dto.feed.StudyStatus
import com.takseha.data.repository.gitudy.GitudyCategoryRepository
import com.takseha.data.repository.gitudy.GitudyStudyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MakeStudyViewModel: ViewModel() {
    private lateinit var gitudyStudyRepository: GitudyStudyRepository
    private lateinit var gitudyCategoryRepository: GitudyCategoryRepository
    private val backgroundColorList = listOf("#00BE93", "#00A19A", "#008291", "#08647A", "#386C5F", "#6E9B7B")
    val randIdx = (0..5).random()

    private val _categoryState = MutableStateFlow<List<Category>>(emptyList())
    val categoryState = _categoryState.asStateFlow()

    private val _isValidRepoName = MutableStateFlow<Boolean?>(null)
    val isValidRepoName = _isValidRepoName.asStateFlow()

    private val _newStudyInfoState = MutableStateFlow(MakeStudyRequest())
    val newStudyInfoState = _newStudyInfoState.asStateFlow()

    fun setStudyIntro(title: String, detail: String, githubRepo: String, categoryIdList: List<Int>) {
        _newStudyInfoState.update { it.copy(topic = title, info = detail, repositoryName = githubRepo, categoriesId = categoryIdList) }
    }
    fun setStudyRule(commitTimes: StudyPeriodStatus, isPublic: StudyStatus, maxMember: Int) {
        _newStudyInfoState.update { it.copy(periodType = commitTimes, status = isPublic, maximumMember = maxMember, profileImageUrl = backgroundColorList[randIdx]) }
    }

    fun checkValidRepoName(name: String) = viewModelScope.launch {
        gitudyStudyRepository = GitudyStudyRepository()

        val request = CheckRepoNameRequest(name)
        val isValidRepoNameResponse = gitudyStudyRepository.checkValidRepoName(request)

        if (isValidRepoNameResponse.isSuccessful) {
            _isValidRepoName.value = true
        } else {
            _isValidRepoName.value = false
            Log.e("MakeStudyViewModel", "isValidRepoNameResponse status: ${isValidRepoNameResponse.code()}\nisValidRepoNameResponse message: ${isValidRepoNameResponse.errorBody()?.string()}")
        }
    }

    fun getAllCategory() = viewModelScope.launch {
        gitudyCategoryRepository = GitudyCategoryRepository()
        val categoryListResponse = gitudyCategoryRepository.getAllCategory()

        if (categoryListResponse.isSuccessful) {
            _categoryState.value = categoryListResponse.body()!!
        } else {
            Log.e("MakeStudyViewModel", "categoryListResponse status: ${categoryListResponse.code()}\ncategoryListResponse message: ${categoryListResponse.errorBody()?.string()}")
        }
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