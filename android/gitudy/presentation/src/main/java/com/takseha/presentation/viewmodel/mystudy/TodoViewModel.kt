package com.takseha.presentation.viewmodel.mystudy

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.takseha.data.dto.mystudy.Todo
import com.takseha.data.repository.study.GitudyStudyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TodoViewModel: ViewModel() {
    private var gitudyStudyRepository = GitudyStudyRepository()

    private val _uiState = MutableStateFlow(TodoListInfoState())
    val uiState = _uiState.asStateFlow()

    fun getTodoList(studyInfoId: Int) = viewModelScope.launch {
        val todoListInfoResponse = gitudyStudyRepository.getTodoList(
            studyInfoId,
            cursorIdx = null,
            limit = 10
        )

        if (todoListInfoResponse.isSuccessful) {
            val resCode = todoListInfoResponse.body()!!.resCode
            val resMsg = todoListInfoResponse.body()!!.resMsg
            val todoBody = todoListInfoResponse.body()!!.todoBody
            Log.d("MyStudyMainViewModel", "todo body: $todoBody")

            if (resCode == 200 && resMsg == "OK") {
                _uiState.update {
                    it.copy(
                        todoListInfo = todoBody.todoList
                    )
                }
                Log.d("MyStudyMainViewModel", "todoList: ${todoBody.todoList}")
            } else {
                Log.e("MyStudyMainViewModel", "https status error: $resCode, $resMsg")
            }
        } else {
            Log.e(
                "MyStudyMainViewModel",
                "todoListInfoResponse status: ${todoListInfoResponse.code()}\ntodoListInfoResponse message: ${todoListInfoResponse.message()}"
            )
        }
    }
}

data class TodoListInfoState(
    var todoListInfo: List<Todo> = listOf()
)