package com.takseha.presentation.viewmodel.mystudy

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.takseha.data.dto.mystudy.MakeTodoRequest
import com.takseha.data.dto.mystudy.Todo
import com.takseha.data.repository.gitudy.GitudyStudyRepository
import com.takseha.presentation.viewmodel.common.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TodoViewModel : BaseViewModel() {
    private var gitudyStudyRepository = GitudyStudyRepository()

    private val _todoListState = MutableStateFlow(TodoListInfoState())
    val todoListState = _todoListState.asStateFlow()
    private val _todoState = MutableStateFlow(Todo())
    val todoState = _todoState.asStateFlow()

    fun getTodoList(studyInfoId: Int) = viewModelScope.launch {
        safeApiCall(
            apiCall = { gitudyStudyRepository.getTodoList(
                studyInfoId,
                cursorIdx = null,
                limit = 10
            ) },
            onSuccess = { response ->
                if (response.isSuccessful) {
                    val todoBody = response.body()!!
                    Log.d("TodoViewModel", "todo body: $todoBody")

                    if (todoBody.todoList.isEmpty()) {
                        _todoListState.update {
                            it.copy(
                                todoListInfo = todoBody.todoList,
                                isTodoEmpty = true
                            )
                        }
                    } else {
                        _todoListState.update {
                            it.copy(
                                todoListInfo = todoBody.todoList,
                                isTodoEmpty = false
                            )
                        }
                    }
                    Log.d("TodoViewModel", "todoList: ${todoBody.todoList}")
                } else {
                    Log.e(
                        "TodoViewModel",
                        "todoListInfoResponse status: ${response.code()}\ntodoListInfoResponse message: ${
                            response.errorBody()?.string()
                        }"
                    )
                }
            }
        )
    }

    fun getTodo(studyInfoId: Int, todoId: Int) = viewModelScope.launch {
        safeApiCall(
            apiCall = { gitudyStudyRepository.getTodo(
                studyInfoId,
                todoId
            ) },
            onSuccess = { response ->
                if (response.isSuccessful) {
                    val todoInfo = response.body()!!
                    _todoState.update {
                        it.copy(
                            title = todoInfo.title,
                            todoLink = todoInfo.todoLink,
                            detail = todoInfo.detail,
                            todoDate = todoInfo.todoDate
                        )
                    }
                } else {
                    Log.e(
                        "TodoViewModel",
                        "getTodoResponse status: ${response.code()}\ngetTodoResponse message: ${
                            response.errorBody()?.string()
                        }"
                    )
                }
            }
        )
    }

    suspend fun updateTodo(
        studyInfoId: Int,
        todoId: Int,
        title: String,
        todoLink: String,
        detail: String,
        todoDate: String
    ) {
        val request = MakeTodoRequest(detail = detail, title = title, todoDate = todoDate, todoLink = todoLink)
        safeApiCall(
            apiCall = { gitudyStudyRepository.updateTodo(
                studyInfoId,
                todoId,
                request
            ) },
            onSuccess = { response ->
                if (response.isSuccessful) {
                    Log.d("TodoViewModel", "updateTodoResponse: ${response.code()}")
                } else {
                    Log.e(
                        "TodoViewModel",
                        "updateTodoResponse status: ${response.code()}\nupdateTodoResponse message: ${
                            response.errorBody()?.string()
                        }"
                    )
                }
            }
        )
    }

    suspend fun deleteTodo(studyInfoId: Int, todoId: Int) {
        safeApiCall(
            apiCall = { gitudyStudyRepository.deleteTodo(
                studyInfoId,
                todoId
            ) },
            onSuccess = { response ->
                if (response.isSuccessful) {
                    getTodoList(studyInfoId)
                    Log.d("TodoViewModel", "deleteTodoResponse: ${response.code()}")
                } else {
                    Log.e(
                        "TodoViewModel",
                        "deleteTodoResponse status: ${response.code()}\ndeleteTodoResponse message: ${
                            response.errorBody()?.string()
                        }"
                    )
                }
            }
        )
    }
}

data class TodoListInfoState(
    var todoListInfo: List<Todo> = listOf(),
    var isTodoEmpty: Boolean = false
)