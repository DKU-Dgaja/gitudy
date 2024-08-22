package com.takseha.presentation.viewmodel.feed

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.takseha.data.dto.feed.MessageRequest
import com.takseha.data.dto.mystudy.StudyInfoResponse
import com.takseha.data.dto.profile.Bookmark
import com.takseha.data.repository.gitudy.GitudyBookmarksRepository
import com.takseha.data.repository.gitudy.GitudyMemberRepository
import com.takseha.data.repository.gitudy.GitudyStudyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class StudyApplyViewModel: ViewModel() {
    private var gitudyStudyRepository = GitudyStudyRepository()
    private var gitudyMemberRepository = GitudyMemberRepository()
    private var gitudyBookmarksRepository = GitudyBookmarksRepository()

    private val _uiState = MutableStateFlow(StudyMainInfoState())
    val uiState = _uiState.asStateFlow()

    private val _isApplySucceed = MutableStateFlow<Boolean?>(null)
    val isApplySucceed = _isApplySucceed.asStateFlow()

    fun getStudyInfo(studyInfoId: Int) = viewModelScope.launch {
        val studyInfoResponse = gitudyStudyRepository.getStudyInfo(studyInfoId)

        if (studyInfoResponse.isSuccessful) {
            val studyInfo = studyInfoResponse.body()!!
            val bookmarkStatus = checkBookmarkStatus(studyInfoId)

            _uiState.update {
                it.copy(
                    studyInfo = studyInfo,
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

    private suspend fun checkBookmarkStatus(studyInfoId: Int): Boolean {
        val bookmarkStatusResponse = gitudyBookmarksRepository.checkBookmarkStatus(
            studyInfoId
        )
        if (bookmarkStatusResponse.isSuccessful) {
            return bookmarkStatusResponse.body()!!.myBookmark
        } else {
            Log.e(
                "StudyApplyViewModel",
                "bookmarkStatusResponse status: ${bookmarkStatusResponse.code()}\nbookmarkStatusResponse message: ${bookmarkStatusResponse.message()}"
            )
        }
        return false
    }

    suspend fun setBookmarkStatus(studyInfoId: Int) {
        val setBookmarkResponse = gitudyBookmarksRepository.setBookmarkStatus(
            studyInfoId
        )

        if (setBookmarkResponse.isSuccessful) {
            Log.d("StudyApplyViewModel", setBookmarkResponse.code().toString())
        } else {
            Log.e(
                "StudyApplyViewModel",
                "setBookmarkResponse status: ${setBookmarkResponse.code()}\nsetBookmarkResponse message: ${setBookmarkResponse.errorBody()?.string()}"
            )
        }
    }

    suspend fun applyStudy(studyInfoId: Int, joinCode: String, message: String) {
        val request = MessageRequest(message)
        Log.d("StudyApplyViewModel", request.toString())

        val applyStudyResponse = gitudyMemberRepository.applyStudy(studyInfoId, joinCode, request)

        if (applyStudyResponse.isSuccessful) {
            _isApplySucceed.value = true
            Log.d("StudyApplyViewModel", applyStudyResponse.code().toString())
        } else {
            _isApplySucceed.value = false
            Log.e("StudyApplyViewModel", "applyStudyResponse status: ${applyStudyResponse.code()}\napplyStudyResponse message: ${applyStudyResponse.errorBody()?.string()}")
        }
    }

    suspend fun withdrawApplyStudy(studyInfoId: Int) {
        val withdrawStudyResponse = gitudyMemberRepository.withdrawApplyStudy(studyInfoId)

        if (withdrawStudyResponse.isSuccessful) {
            Log.d("StudyApplyViewModel", withdrawStudyResponse.code().toString())
        } else {
            Log.e("StudyApplyViewModel", "withdrawStudyResponse status: ${withdrawStudyResponse.code()}\nwithdrawStudyResponse message: ${withdrawStudyResponse.errorBody()?.string()}")
        }
    }
}

data class StudyMainInfoState(
    val studyInfo: StudyInfoResponse = StudyInfoResponse(),
    val isMyBookmark: Boolean? = null
)