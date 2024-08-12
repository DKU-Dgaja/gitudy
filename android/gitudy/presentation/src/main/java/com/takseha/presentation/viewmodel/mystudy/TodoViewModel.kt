package com.takseha.presentation.viewmodel.mystudy

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.takseha.data.dto.mystudy.MakeTodoRequest
import com.takseha.data.dto.mystudy.Todo
import com.takseha.data.repository.gitudy.GitudyStudyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TodoViewModel : ViewModel() {
    private var gitudyStudyRepository = GitudyStudyRepository()

    private val _todoListState = MutableStateFlow(TodoListInfoState())
    val todoListState = _todoListState.asStateFlow()
    private val _todoState = MutableStateFlow(Todo())
    val todoState = _todoState.asStateFlow()

    fun getTodoList(studyInfoId: Int) = viewModelScope.launch {
        val todoListInfoResponse = gitudyStudyRepository.getTodoList(
            studyInfoId,
            cursorIdx = null,
            limit = 10
        )

        if (todoListInfoResponse.isSuccessful) {
            val todoBody = todoListInfoResponse.body()!!
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
                "todoListInfoResponse status: ${todoListInfoResponse.code()}\ntodoListInfoResponse message: ${
                    todoListInfoResponse.errorBody()?.string()
                }"
            )
        }
    }

    fun getTodo(studyInfoId: Int, todoId: Int) = viewModelScope.launch {
        val getTodoResponse = gitudyStudyRepository.getTodo(
            studyInfoId,
            todoId
        )

        if (getTodoResponse.isSuccessful) {
            val todoInfo = getTodoResponse.body()!!
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
                "getTodoResponse status: ${getTodoResponse.code()}\ngetTodoResponse message: ${
                    getTodoResponse.errorBody()?.string()
                }"
            )
        }
    }

    fun updateTodo(
        studyInfoId: Int,
        todoId: Int,
        title: String,
        todoLink: String,
        detail: String,
        todoDate: String
    ) = viewModelScope.launch {
        val request = MakeTodoRequest(detail = detail, title = title, todoDate = todoDate, todoLink = todoLink)

        val updateTodoResponse = gitudyStudyRepository.updateTodo(
            studyInfoId,
            todoId,
            request
        )
        if (updateTodoResponse.isSuccessful) {
            Log.d("TodoViewModel", "updateTodoResponse: ${updateTodoResponse.code()}")
        } else {
            Log.e(
                "TodoViewModel",
                "updateTodoResponse status: ${updateTodoResponse.code()}\nupdateTodoResponse message: ${
                    updateTodoResponse.errorBody()?.string()
                }"
            )
        }
    }

    fun deleteTodo(studyInfoId: Int, todoId: Int) = viewModelScope.launch {
        val deleteTodoResponse = gitudyStudyRepository.deleteTodo(
            studyInfoId,
            todoId
        )
        if (deleteTodoResponse.isSuccessful) {
            getTodoList(studyInfoId)
            Log.d("TodoViewModel", "deleteTodoResponse: ${deleteTodoResponse.code()}")
        } else {
            Log.e(
                "TodoViewModel",
                "deleteTodoResponse status: ${deleteTodoResponse.code()}\ndeleteTodoResponse message: ${
                    deleteTodoResponse.errorBody()?.string()
                }"
            )
        }
    }
}

data class TodoListInfoState(
    var todoListInfo: List<Todo> = listOf(),
    var isTodoEmpty: Boolean = false
)