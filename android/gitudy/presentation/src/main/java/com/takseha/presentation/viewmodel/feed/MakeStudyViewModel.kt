package com.takseha.presentation.viewmodel.feed

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.takseha.data.dto.feed.Category
import com.takseha.data.dto.feed.CheckRepoNameRequest
import com.takseha.data.dto.feed.MakeStudyRequest
import com.takseha.data.dto.feed.StudyPeriodStatus
import com.takseha.data.dto.feed.StudyStatus
import com.takseha.data.repository.gitudy.GitudyCategoryRepository
import com.takseha.data.repository.gitudy.GitudyStudyRepository
import com.takseha.presentation.viewmodel.common.BaseApplicationViewModel
import com.takseha.presentation.viewmodel.common.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MakeStudyViewModel() : BaseViewModel()  {
    private lateinit var gitudyStudyRepository: GitudyStudyRepository
    private lateinit var gitudyCategoryRepository: GitudyCategoryRepository

    private val _categoryState = MutableStateFlow<List<Category>>(emptyList())
    val categoryState = _categoryState.asStateFlow()

    private val _isValidRepoName = MutableStateFlow<Boolean?>(null)
    val isValidRepoName = _isValidRepoName.asStateFlow()

    private val _newStudyInfoState = MutableStateFlow(MakeStudyRequest())
    val newStudyInfoState = _newStudyInfoState.asStateFlow()

    private val _responseState = MutableStateFlow<Boolean?>(null)
    val responseState = _responseState.asStateFlow()

    fun setStudyIntro(title: String, detail: String, githubRepo: String, categoryIdList: List<Int>) {
        _newStudyInfoState.update { it.copy(topic = title, info = detail, repositoryName = githubRepo, categoriesId = categoryIdList) }
    }
    fun setStudyRule(commitTimes: StudyPeriodStatus, isPublic: StudyStatus, maxMember: Int, profileImageUrl: String) {
        _newStudyInfoState.update { it.copy(periodType = commitTimes, status = isPublic, maximumMember = maxMember, profileImageUrl = profileImageUrl) }
    }

    fun checkValidRepoName(name: String) = viewModelScope.launch {
        gitudyStudyRepository = GitudyStudyRepository()
        val request = CheckRepoNameRequest(name)
        safeApiCall(
            apiCall = { gitudyStudyRepository.checkValidRepoName(request) },
            onSuccess = { response ->
                if (response.isSuccessful) {
                    _isValidRepoName.value = true
                }
            },
            onError = { e, response ->
                super.handleDefaultError(e)
                super.resetSnackbarMessage()
                _isValidRepoName.value = false
                e?.let {
                    Log.e("MakeStudyViewModel", "Exception: ${it.message}")
                } ?: run {
                    response?.let {
                        Log.e("MakeStudyViewModel", "HTTP Error: ${it.code()} ${it.errorBody()?.string()}")
                    }
                }
            }
        )
    }

    fun resetCorrectRepoName() {
        _isValidRepoName.value = null
    }

    fun getAllCategory() = viewModelScope.launch {
        gitudyCategoryRepository = GitudyCategoryRepository()
        safeApiCall(
            apiCall = { gitudyCategoryRepository.getAllCategory() },
            onSuccess = { response ->
                if (response.isSuccessful) {
                    _categoryState.value = response.body()!!
                } else {
                    Log.e("MakeStudyViewModel", "categoryListResponse status: ${response.code()}\ncategoryListResponse message: ${response.errorBody()?.string()}")
                }
            }
        )
    }

    suspend fun makeNewStudy() {
        gitudyStudyRepository = GitudyStudyRepository()
        val request = newStudyInfoState.value
        safeApiCall(
            apiCall = { gitudyStudyRepository.makeNewStudy(request) },
            onSuccess = { response ->
                if (response.isSuccessful) {
                    _responseState.value = true
                    Log.d("MakeStudyViewModel", response.code().toString())
                }
            },
            onError = { e, response ->
                super.handleDefaultError(e)
                super.resetSnackbarMessage()
                _responseState.value = false
                e?.let {
                    Log.e("MakeStudyViewModel", "Exception: ${it.message}")
                } ?: run {
                    response?.let {
                        Log.e("MakeStudyViewModel", "HTTP Error: ${it.code()} ${it.errorBody()?.string()}")
                    }
                }
            }
        )
    }
}