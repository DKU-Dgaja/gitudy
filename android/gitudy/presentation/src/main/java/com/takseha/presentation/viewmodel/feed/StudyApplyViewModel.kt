package com.takseha.presentation.viewmodel.feed

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.takseha.data.dto.feed.MessageRequest
import com.takseha.data.dto.feed.StudyRankResponse
import com.takseha.data.dto.mystudy.StudyInfoResponse
import com.takseha.data.repository.gitudy.GitudyBookmarksRepository
import com.takseha.data.repository.gitudy.GitudyMemberRepository
import com.takseha.data.repository.gitudy.GitudyStudyRepository
import com.takseha.presentation.viewmodel.common.BaseViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class StudyApplyViewModel : BaseViewModel() {
    private var gitudyStudyRepository = GitudyStudyRepository()
    private var gitudyMemberRepository = GitudyMemberRepository()
    private var gitudyBookmarksRepository = GitudyBookmarksRepository()

    private val _uiState = MutableStateFlow(StudyMainInfoState())
    val uiState = _uiState.asStateFlow()

    private val _isApplySucceed = MutableStateFlow<Boolean?>(null)
    val isApplySucceed = _isApplySucceed.asStateFlow()

    private val _applyErrorMessage = MutableStateFlow<String?>(null)
    val applyErrorMessage = _applyErrorMessage.asStateFlow()

    suspend fun getStudyInfo(studyInfoId: Int) {
        safeApiCall(
            apiCall = { gitudyStudyRepository.getStudyInfo(studyInfoId) },
            onSuccess = { response ->
                if (response.isSuccessful) {
                    val studyInfo = response.body()!!

                    _uiState.update {
                        it.copy(
                            studyInfo = studyInfo
                        )
                    }
                } else {
                    Log.e(
                        "StudyApplyViewModel",
                        "studyInfoResponse status: ${response.code()}\nstudyInfoResponse message: ${response.errorBody()?.string()}"
                    )
                }
            }
        )
    }

    suspend fun getStudyRank(studyInfoId: Int) {
        safeApiCall(
            apiCall = { gitudyStudyRepository.getStudyRank(studyInfoId) },
            onSuccess = { response ->
                if (response.isSuccessful) {
                    val studyRank = response.body()!!.ranking

                    _uiState.update {
                        it.copy(
                            rank = studyRank
                        )
                    }
                } else {
                    Log.e(
                        "StudyApplyViewModel",
                        "studyRankResponse status: ${response.code()}\nstudyRankResponse message: ${response.errorBody()?.string()}"
                    )
                }
            }
        )
    }

    suspend fun checkBookmarkStatus(studyInfoId: Int) {
        safeApiCall(
            apiCall = { gitudyBookmarksRepository.checkBookmarkStatus(studyInfoId) },
            onSuccess = { response ->
                if (response.isSuccessful) {
                    val bookmarkStatus = response.body()!!.myBookmark

                    _uiState.update {
                        it.copy(
                            isMyBookmark = bookmarkStatus
                        )
                    }
                } else {
                    Log.e(
                        "StudyApplyViewModel",
                        "bookmarkStatusResponse status: ${response.code()}\nbookmarkStatusResponse message: ${response.errorBody()?.string()}"
                    )
                }
            }
        )
    }

    suspend fun setBookmarkStatus(studyInfoId: Int) {
        safeApiCall(
            apiCall = {
                gitudyBookmarksRepository.setBookmarkStatus(
                    studyInfoId
                )
            },
            onSuccess = { response ->
                if (response.isSuccessful) {
                    viewModelScope.launch {
                        checkBookmarkStatus(studyInfoId)
                    }
                    Log.d("StudyApplyViewModel", response.code().toString())
                } else {
                    Log.e(
                        "StudyApplyViewModel",
                        "setBookmarkResponse status: ${response.code()}\nsetBookmarkResponse message: ${
                            response.errorBody()?.string()
                        }"
                    )
                }
            }
        )
    }

    suspend fun applyStudy(studyInfoId: Int, joinCode: String, message: String) {
        val request = MessageRequest(message)
        safeApiCall(
            apiCall = { gitudyMemberRepository.applyStudy(studyInfoId, joinCode, request) },
            onSuccess = { response ->
                if (response.isSuccessful) {
                    _isApplySucceed.value = true
                    Log.d("StudyApplyViewModel", response.code().toString())
                }
            },
            onError = { e, response ->
                super.handleDefaultError(e)
                _isApplySucceed.value = false
                e?.let {
                    Log.e("StudyApplyViewModel", "Exception: ${it.message}")
                } ?: run {
                    response?.let {
                        val errorBody = it.errorBody()?.string() ?: "없음"
                        Log.e("StudyApplyViewModel", "HTTP Error: ${it.code()} $errorBody")

                        if (errorBody.contains("재가입")) {
                            _applyErrorMessage.value = "스터디 재가입은 불가합니다"
                        } else if (errorBody.contains("이미 해당")) {
                            _applyErrorMessage.value = "이미 가입한 스터디입니다"
                        }
                    }
                }
            }
        )
    }

    fun resetApplyErrorMessage() {
        _applyErrorMessage.value = null
    }

    suspend fun withdrawApplyStudy(studyInfoId: Int) {
        safeApiCall(
            apiCall = { gitudyMemberRepository.withdrawApplyStudy(studyInfoId) },
            onSuccess = { response ->
                if (response.isSuccessful) {
                    Log.d("StudyApplyViewModel", response.code().toString())
                } else {
                    Log.e(
                        "StudyApplyViewModel",
                        "withdrawStudyResponse status: ${response.code()}\nwithdrawStudyResponse message: ${
                            response.errorBody()?.string()
                        }"
                    )
                }
            }
        )
    }
}

data class StudyMainInfoState(
    val studyInfo: StudyInfoResponse = StudyInfoResponse(),
    val rank: Int = 0,
    val isMyBookmark: Boolean? = null
)