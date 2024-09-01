package com.takseha.presentation.viewmodel.mystudy

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.takseha.data.dto.mystudy.StudyComment
import com.takseha.data.repository.gitudy.GitudyStudyRepository
import com.takseha.presentation.viewmodel.common.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class StudyCommentBoardViewModel : BaseViewModel() {
    private var gitudyStudyRepository = GitudyStudyRepository()

    private val _commentState = MutableStateFlow<List<StudyComment>?>(null)
    val commentState = _commentState.asStateFlow()

    fun getStudyComments(studyInfoId: Int, limit: Long) = viewModelScope.launch {
        safeApiCall(
            apiCall = { gitudyStudyRepository.getStudyComments(studyInfoId, null, limit) },
            onSuccess = { response ->
                if (response.isSuccessful) {
                    _commentState.value = response.body()?.studyCommentList ?: emptyList()
                } else {
                    Log.e(
                        "StudyCommentBoardViewModel",
                        "studyCommentListResponse status: ${response.code()}\nstudyCommentListResponse message: ${response.message()}"
                    )
                }
            }
        )
    }


    suspend fun makeStudyComment(studyInfoId: Int, content: String, limit: Long) {
        safeApiCall(
            apiCall = { gitudyStudyRepository.makeStudyComment(studyInfoId, content) },
            onSuccess = { response ->
                if (response.isSuccessful) {
                    getStudyComments(studyInfoId, limit)
                    Log.d("StudyCommentBoardViewModel", response.code().toString())
                } else {
                    Log.e(
                        "StudyCommentBoardViewModel",
                        "newStudyCommentResponse status: ${response.code()}\nnewStudyCommentResponse message: ${response.message()}"
                    )
                }
            }
        )
    }

    suspend fun deleteStudyComment(studyInfoId: Int, studyCommentId: Int, limit: Long) {
        safeApiCall(
            apiCall = { gitudyStudyRepository.deleteStudyComment(
                studyInfoId,
                studyCommentId
            ) },
            onSuccess = { response ->
                if (response.isSuccessful) {
                    getStudyComments(studyInfoId, limit)
                    Log.d("StudyCommentBoardViewModel", "deleteStudyCommentResponse: ${response.code()}")
                } else {
                    Log.e(
                        "StudyCommentBoardViewModel",
                        "deleteStudyCommentResponse status: ${response.code()}\ndeleteStudyCommentResponse message: ${
                            response.errorBody()?.string()
                        }"
                    )
                }
            }
        )
    }
}