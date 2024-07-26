package com.takseha.presentation.viewmodel.mystudy

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.takseha.data.dto.mystudy.MyStudyInfoResponse
import com.takseha.data.dto.mystudy.StudyConvention
import com.takseha.data.dto.mystudy.StudyMember
import com.takseha.data.dto.mystudy.Todo
import com.takseha.data.repository.member.GitudyMemberRepository
import com.takseha.data.repository.study.GitudyStudyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MyStudyMainViewModel: ViewModel() {
    private var gitudyStudyRepository = GitudyStudyRepository()
    private var gitudyMemberRepository = GitudyMemberRepository()

    private val _uiState = MutableStateFlow(MyStudyMainInfoState())
    val uiState = _uiState.asStateFlow()

    fun getMyStudyInfo(studyInfoId: Int) = viewModelScope.launch {
        val myStudyInfoResponse = gitudyStudyRepository.getMyStudyInfo(studyInfoId)

        if (myStudyInfoResponse.isSuccessful) {
            val myStudyInfo = myStudyInfoResponse.body()!!

            val todo = getFirstTodoInfo(studyInfoId)
            val convention = getConvention(studyInfoId)
            val studyMemberList = getStudyMemberList(studyInfoId)

            _uiState.update {
                it.copy(
                    myStudyInfo = myStudyInfo,
                    todoInfo = todo,
                    conventionInfo = convention,
                    studyMemberListInfo = studyMemberList
                )
            }

            Log.d("MyStudyMainViewModel", "_uiState: ${_uiState.value}")
        } else {
            Log.e(
                "MyStudyMainViewModel",
                "myStudyInfoResponse status: ${myStudyInfoResponse.code()}\nmyStudyInfoResponse message: ${myStudyInfoResponse.message()}"
            )
        }
    }

    private suspend fun getFirstTodoInfo(studyInfoId: Int): Todo? {
        val todoInfoResponse = gitudyStudyRepository.getTodoList(
            studyInfoId,
            cursorIdx = null,
            limit = 1
        )

        if (todoInfoResponse.isSuccessful) {
            val todoBody = todoInfoResponse.body()!!

            if (todoBody.todoList.isNotEmpty()) {
                val todo = todoBody.todoList.first()

                return todo
            } else {
                Log.d("MainHomeViewModel", "No To-Do")
                return Todo(
                    detail = "No To-Do",
                    id = -1,
                    studyInfoId = studyInfoId,
                    title = "No To-Do",
                    todoDate = "",
                    todoCode = "",
                    todoLink = "",
                    commitList = listOf()
                )
            }
        } else {
            Log.e(
                "MyStudyMainViewModel",
                "todoInfoResponse status: ${todoInfoResponse.code()}\ntodoInfoResponse message: ${todoInfoResponse.message()}"
            )
        }
        // 에러 발생 시 null return
        Log.d("MyStudyMainViewModel", "Error")
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
            studyMemberListResponse.body()!!
        } else {
            Log.e(
                "MyStudyMainViewModel",
                "studyMemberListResponse status: ${studyMemberListResponse.code()}\nstudyMemberListResponse message: ${studyMemberListResponse.message()}"
            )
            return listOf()
        }
        Log.e(
            "MyStudyMainViewModel", "통신 에러")
        return listOf()
    }
}

data class MyStudyMainInfoState(
    var myStudyInfo: MyStudyInfoResponse = MyStudyInfoResponse(),
    var todoInfo: Todo? = null,
    var conventionInfo: StudyConvention? = null,
    var studyMemberListInfo: List<StudyMember> = listOf()
    // TODO: comment 추가
)