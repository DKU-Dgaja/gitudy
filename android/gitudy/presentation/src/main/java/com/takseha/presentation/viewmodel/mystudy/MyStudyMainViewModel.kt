package com.takseha.presentation.viewmodel.mystudy

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.takseha.common.model.SPKey
import com.takseha.common.util.SP
import com.takseha.data.dto.mystudy.MyStudyInfo
import com.takseha.data.dto.mystudy.MyStudyWithTodo
import com.takseha.data.dto.mystudy.Todo
import com.takseha.data.dto.mystudy.TodoStatus
import com.takseha.data.repository.study.GitudyStudyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MyStudyMainViewModel(application: Application) : AndroidViewModel(application) {
    private var gitudyStudyRepository: GitudyStudyRepository = GitudyStudyRepository()
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

                _uiState.update {
                    it.copy(
                        myStudyInfo = myStudyInfo,
                        todoInfo = todo
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


}

data class MyStudyMainInfoState(
    var myStudyInfo: MyStudyInfo = MyStudyInfo(),
    var todoInfo: Todo? = null,
    // todo: convention 추가, comment 추가
    )