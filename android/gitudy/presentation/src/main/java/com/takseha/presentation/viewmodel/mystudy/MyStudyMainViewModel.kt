package com.takseha.presentation.viewmodel.mystudy

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.takseha.data.dto.mystudy.StudyComment
import com.takseha.data.dto.mystudy.StudyConvention
import com.takseha.data.dto.mystudy.StudyInfoResponse
import com.takseha.data.dto.mystudy.StudyMember
import com.takseha.data.dto.mystudy.Todo
import com.takseha.data.repository.gitudy.GitudyMemberRepository
import com.takseha.data.repository.gitudy.GitudyStudyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MyStudyMainViewModel : ViewModel() {
    private var gitudyStudyRepository = GitudyStudyRepository()
    private var gitudyMemberRepository = GitudyMemberRepository()

    private val _myStudyState = MutableStateFlow(MyStudyMainInfoState())
    val myStudyState = _myStudyState.asStateFlow()

    private val _commentState = MutableStateFlow<List<StudyComment>>(emptyList())
    val commentState = _commentState.asStateFlow()

    fun getMyStudyInfo(studyInfoId: Int) = viewModelScope.launch {
        val myStudyInfoResponse = gitudyStudyRepository.getStudyInfo(studyInfoId)

        if (myStudyInfoResponse.isSuccessful) {
            val myStudyInfo = myStudyInfoResponse.body()!!
            val urgentTodo = getUrgentTodo(studyInfoId)
            val convention = getConvention(studyInfoId)
            val studyMemberList = getStudyMemberList(studyInfoId)

            if (urgentTodo == null) {
                _myStudyState.update {
                    it.copy(
                        myStudyInfo = myStudyInfo,
                        todoInfo = urgentTodo,
                        conventionInfo = convention,
                        studyMemberListInfo = studyMemberList,
                        isUrgentTodo = false
                    )
                }
            } else {
                _myStudyState.update {
                    it.copy(
                        myStudyInfo = myStudyInfo,
                        todoInfo = urgentTodo,
                        conventionInfo = convention,
                        studyMemberListInfo = studyMemberList,
                        isUrgentTodo = true
                    )
                }
            }
            Log.d("MyStudyMainViewModel", "_myStudyState: ${_myStudyState.value}")
        } else {
            Log.e(
                "MyStudyMainViewModel",
                "myStudyInfoResponse status: ${myStudyInfoResponse.code()}\nmyStudyInfoResponse message: ${myStudyInfoResponse.message()}"
            )
        }
    }

    fun getStudyComments(studyInfoId: Int, limit: Long) = viewModelScope.launch {
        val studyCommentListResponse =
            gitudyStudyRepository.getStudyComments(studyInfoId, null, limit)

        if (studyCommentListResponse.isSuccessful) {
            _commentState.value = studyCommentListResponse.body()?.studyCommentList ?: emptyList()
        } else {
            Log.e(
                "MyStudyMainViewModel",
                "studyCommentListResponse status: ${studyCommentListResponse.code()}\nstudyCommentListResponse message: ${studyCommentListResponse.message()}"
            )
        }
    }

    private suspend fun getUrgentTodo(studyInfoId: Int): Todo? {
        val urgentTodoResponse = gitudyStudyRepository.getTodoProgress(
            studyInfoId
        )

        if (urgentTodoResponse.isSuccessful) {
            return urgentTodoResponse.body()?.todo
        } else {
            Log.e(
                "MainHomeViewModel",
                "urgentTodoResponse status: ${urgentTodoResponse.code()}\nurgentTodoResponse message: ${urgentTodoResponse.message()}"
            )
        }
        return null
    }

    private suspend fun getConvention(studyInfoId: Int): StudyConvention? {
        val conventionInfoResponse = gitudyStudyRepository.getConvention(
            studyInfoId
        )

        if (conventionInfoResponse.isSuccessful) {
            val conventionInfo = conventionInfoResponse.body()!!
            Log.d("MyStudyMainViewModel", "conventionInfo: $conventionInfo")

            if (conventionInfo.studyConventionList.isNotEmpty()) {
                return conventionInfo.studyConventionList.first()
            } else {
                Log.d("MyStudyMainViewModel", "No Convention")
                return null
            }
        } else {
            Log.e(
                "MyStudyMainViewModel",
                "conventionInfoResponse status: ${conventionInfoResponse.code()}\nconventionInfoResponse message: ${conventionInfoResponse.message()}"
            )
        }
        // 에러 발생 시 null return
        Log.d("MyStudyMainViewModel", "Error")
        return null
    }

    private suspend fun getStudyMemberList(studyInfoId: Int): List<StudyMember> {
        val studyMemberListResponse =
            gitudyMemberRepository.getStudyMemberList(studyInfoId, true)

        if (studyMemberListResponse.isSuccessful) {
            return studyMemberListResponse.body()!!
        } else {
            Log.e(
                "MyStudyMainViewModel",
                "studyMemberListResponse status: ${studyMemberListResponse.code()}\nstudyMemberListResponse message: ${studyMemberListResponse.message()}"
            )
            return listOf()
        }
    }

    fun makeStudyComment(studyInfoId: Int, content: String, limit: Long) = viewModelScope.launch {
        val newStudyCommentResponse =
            gitudyStudyRepository.makeStudyComment(studyInfoId, content)

        if (newStudyCommentResponse.isSuccessful) {
            // commentList 상태 업데이트
            getStudyComments(studyInfoId, limit)
            Log.d("MyStudyMainViewModel", newStudyCommentResponse.code().toString())
        } else {
            Log.e(
                "MyStudyMainViewModel",
                "newStudyCommentResponse status: ${newStudyCommentResponse.code()}\nnewStudyCommentResponse message: ${newStudyCommentResponse.message()}"
            )
        }
    }
}

data class MyStudyMainInfoState(
    var myStudyInfo: StudyInfoResponse = StudyInfoResponse(),
    var todoInfo: Todo? = null,
    var conventionInfo: StudyConvention? = null,
    var studyMemberListInfo: List<StudyMember> = listOf(),
    var isUrgentTodo: Boolean = true
)