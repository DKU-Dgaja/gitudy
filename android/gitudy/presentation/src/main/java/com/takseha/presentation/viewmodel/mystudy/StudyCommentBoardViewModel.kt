package com.takseha.presentation.viewmodel.mystudy

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.takseha.data.dto.mystudy.StudyComment
import com.takseha.data.repository.gitudy.GitudyStudyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class StudyCommentBoardViewModel : ViewModel() {
    private var gitudyStudyRepository = GitudyStudyRepository()

    private val _commentState = MutableStateFlow<List<StudyComment>>(emptyList())
    val commentState = _commentState.asStateFlow()

    fun getStudyComments(studyInfoId: Int, limit: Long) = viewModelScope.launch {
        val studyCommentListResponse =
            gitudyStudyRepository.getStudyComments(studyInfoId, null, limit)

        if (studyCommentListResponse.isSuccessful) {
            _commentState.value = studyCommentListResponse.body()?.studyCommentList ?: emptyList()
        } else {
            Log.e(
                "StudyCommentBoardViewModel",
                "studyCommentListResponse status: ${studyCommentListResponse.code()}\nstudyCommentListResponse message: ${studyCommentListResponse.message()}"
            )
        }
    }


    fun makeStudyComment(studyInfoId: Int, content: String, limit: Long) = viewModelScope.launch {
        val newStudyCommentResponse =
            gitudyStudyRepository.makeStudyComment(studyInfoId, content)

        if (newStudyCommentResponse.isSuccessful) {
            // commentList 상태 업데이트
            getStudyComments(studyInfoId, limit)
            Log.d("StudyCommentBoardViewModel", newStudyCommentResponse.code().toString())
        } else {
            Log.e(
                "StudyCommentBoardViewModel",
                "newStudyCommentResponse status: ${newStudyCommentResponse.code()}\nnewStudyCommentResponse message: ${newStudyCommentResponse.message()}"
            )
        }
    }

    fun deleteStudyComment(studyInfoId: Int, studyCommentId: Int, limit: Long) = viewModelScope.launch {
        val deleteStudyCommentResponse = gitudyStudyRepository.deleteStudyComment(
            studyInfoId,
            studyCommentId
        )
        if (deleteStudyCommentResponse.isSuccessful) {
            getStudyComments(studyInfoId, limit)
            Log.d("StudyCommentBoardViewModel", "deleteStudyCommentResponse: ${deleteStudyCommentResponse.code()}")
        } else {
            Log.e(
                "StudyCommentBoardViewModel",
                "deleteStudyCommentResponse status: ${deleteStudyCommentResponse.code()}\ndeleteStudyCommentResponse message: ${
                    deleteStudyCommentResponse.errorBody()?.string()
                }"
            )
        }
    }
}