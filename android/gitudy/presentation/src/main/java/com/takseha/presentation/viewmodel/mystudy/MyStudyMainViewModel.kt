package com.takseha.presentation.viewmodel.mystudy

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.takseha.common.model.SPKey
import com.takseha.common.util.SP
import com.takseha.data.dto.mystudy.MyStudyInfo
import com.takseha.data.dto.mystudy.StudyConvention
import com.takseha.data.dto.mystudy.StudyMember
import com.takseha.data.dto.mystudy.Todo
import com.takseha.data.repository.member.GitudyMemberRepository
import com.takseha.data.repository.study.GitudyStudyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MyStudyMainViewModel(application: Application) : AndroidViewModel(application) {
    private var gitudyStudyRepository = GitudyStudyRepository()
    private var gitudyMemberRepository = GitudyMemberRepository()
    private val prefs = SP(getApplication())

    private val bearerToken = "Bearer ${prefs.loadPref(SPKey.ACCESS_TOKEN, "0")} ${
        prefs.loadPref(SPKey.REFRESH_TOKEN, "0")
    }"

    private val _uiState = MutableStateFlow(MyStudyMainInfoState())
    val uiState = _uiState.asStateFlow()

    fun getMyStudyInfo(studyInfoId: Int) = viewModelScope.launch {
        val myStudyInfoResponse = gitudyStudyRepository.getMyStudyInfo(bearerToken, studyInfoId)

        if (myStudyInfoResponse.isSuccessful) {
            val resCode = myStudyInfoResponse.body()!!.resCode
            val resMsg = myStudyInfoResponse.body()!!.resMsg
            val myStudyInfo = myStudyInfoResponse.body()!!.myStudyInfo


            if (resCode == 200 && resMsg == "OK") {
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
                Log.e("MyStudyMainViewModel", "https status error: $resCode, $resMsg")
            }
        } else {
            Log.e(
                "MyStudyMainViewModel",
                "myStudyInfoResponse status: ${myStudyInfoResponse.code()}\nmyStudyInfoResponse message: ${myStudyInfoResponse.message()}"
            )
        }
    }

    private suspend fun getFirstTodoInfo(studyInfoId: Int): Todo? {
        val todoInfoResponse = gitudyStudyRepository.getTodoList(
            bearerToken,
            studyInfoId,
            cursorIdx = null,
            limit = 1
        )

        if (todoInfoResponse.isSuccessful) {
            val resCode = todoInfoResponse.body()!!.resCode
            val resMsg = todoInfoResponse.body()!!.resMsg
            val todoBody = todoInfoResponse.body()!!.todoBody
            Log.d("MyStudyMainViewModel", "todo body: $todoBody")

            if (resCode == 200 && resMsg == "OK") {
                if (todoBody.todoList.isNotEmpty()) {
                    val todo = todoBody.todoList.first()
                    Log.d("MyStudyMainViewModel", "todo first: $todo")

                    return todo
                } else {
                    Log.d("MyStudyMainViewModel", "No To-Do")
                    return null
                }
            } else {
                Log.e("MyStudyMainViewModel", "https status error: $resCode, $resMsg")
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
            bearerToken,
            studyInfoId
        )

        if (conventionInfoResponse.isSuccessful) {
            val resCode = conventionInfoResponse.body()!!.resCode
            val resMsg = conventionInfoResponse.body()!!.resMsg
            val conventionInfo = conventionInfoResponse.body()!!.conventionInfo
            Log.d("MyStudyMainViewModel", "conventionInfo: $conventionInfo")

            if (resCode == 200 && resMsg == "OK") {
                if (conventionInfo.studyConventionList.isNotEmpty()) {
                    return conventionInfo.studyConventionList.first()
                } else {
                    Log.d("MyStudyMainViewModel", "No Convention")
                    return null
                }
            } else {
                Log.e("MyStudyMainViewModel", "https status error: $resCode, $resMsg")
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
            gitudyMemberRepository.getStudyMemberList(bearerToken, studyInfoId, true)

        if (studyMemberListResponse.isSuccessful) {
            val resCode = studyMemberListResponse.body()!!.resCode
            val resMsg = studyMemberListResponse.body()!!.resMsg
            val studyMemberList = studyMemberListResponse.body()!!.studyMemberList
            Log.d("MyStudyMainViewModel", "studyMemberList: $studyMemberList")

            if (resCode == 200 && resMsg == "OK") {
                return studyMemberList
            } else {
                Log.e("MyStudyMainViewModel", "https status error: $resCode, $resMsg")
            }
        } else {
            Log.e(
                "MyStudyMainViewModel",
                "studyMemberListResponse status: ${studyMemberListResponse.code()}\nstudyMemberListResponse message: ${studyMemberListResponse.message()}"
            )
            return listOf()
        }
        Log.e(
            "MyStudyMainViewModel",
            "통신 에러"
        )
        return listOf()
    }
}

data class MyStudyMainInfoState(
    var myStudyInfo: MyStudyInfo = MyStudyInfo(),
    var todoInfo: Todo? = null,
    var conventionInfo: StudyConvention? = null,
    var studyMemberListInfo: List<StudyMember> = listOf()
    // TODO: comment 추가
)