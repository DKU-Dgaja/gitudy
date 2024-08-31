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

    fun getStudyInfo(studyInfoId: Int) = viewModelScope.launch {
        safeApiCall(
            apiCall = {
                val studyInfoResponse = async { gitudyStudyRepository.getStudyInfo(studyInfoId) }
                val studyRankResponse = async { gitudyStudyRepository.getStudyRank(studyInfoId) }
                val bookmarkStatusResponse = async { gitudyBookmarksRepository.checkBookmarkStatus(
                    studyInfoId
                ) }

                // Triple로 세 가지 결과를 반환
                Triple(
                    studyInfoResponse.await(),
                    studyRankResponse.await(),
                    bookmarkStatusResponse.await()
                )
            },
            onSuccess = { (studyInfoResponse, studyRankResponse, bookmarkStatusResponse) ->
                if (studyInfoResponse.isSuccessful && studyRankResponse.isSuccessful && bookmarkStatusResponse.isSuccessful) {
                    val studyInfo = studyInfoResponse.body()!!
                    val studyRank = studyRankResponse.body()!!.ranking
                    val bookmarkStatus = bookmarkStatusResponse.body()!!.myBookmark

                    _uiState.update {
                        it.copy(
                            studyInfo = studyInfo,
                            rank = studyRank,
                            isMyBookmark = bookmarkStatus
                        )
                    }
                } else {
                    Log.e(
                        "StudyApplyViewModel",
                        "studyInfoResponse status: ${studyInfoResponse.code()}\nstudyInfoResponse message: ${studyInfoResponse.errorBody()?.string()}"
                    )
                }
            }
        )
    }

    private suspend fun getStudyRank(studyInfoId: Int): StudyRankResponse? {
        return try {
            val response = gitudyStudyRepository.getStudyRank(studyInfoId)
            if (response.isSuccessful) {
                response.body()!!
            } else {
                Log.e(
                    "StudyApplyViewModel",
                    "studyRankResponse status: ${response.code()}\nstudyRankResponse message: ${
                        response.errorBody()?.string()
                    }"
                )
                null
            }
        } catch (e: Exception) {
            Log.e("StudyApplyViewModel", "Error fetching getUserInfo()", e)
            null
        }
    }

    private suspend fun checkBookmarkStatus(studyInfoId: Int): Boolean {
        return try {
            val response = gitudyBookmarksRepository.checkBookmarkStatus(
                studyInfoId
            )
            if (response.isSuccessful) {
                response.body()!!.myBookmark
            } else {
                Log.e(
                    "StudyApplyViewModel",
                    "bookmarkStatusResponse status: ${response.code()}\nbookmarkStatusResponse message: ${response.message()}"
                )
                false
            }
        } catch (e: Exception) {
            Log.e("StudyApplyViewModel", "Error fetching getUserInfo()", e)
            false
        }
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
                } else {
                    _isApplySucceed.value = false
                    Log.e(
                        "StudyApplyViewModel",
                        "applyStudyResponse status: ${response.code()}\napplyStudyResponse message: ${
                            response.errorBody()?.string()
                        }"
                    )
                }
            }
        )
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