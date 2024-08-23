package com.takseha.presentation.viewmodel.mystudy

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.takseha.data.dto.mystudy.MakeTodoRequest
import com.takseha.data.repository.gitudy.GitudyStudyRepository
import kotlinx.coroutines.launch

class AddTodoViewModel: ViewModel() {
    private var gitudyStudyRepository: GitudyStudyRepository = GitudyStudyRepository()

    suspend fun makeNewTodo(studyInfoId: Int, title: String, todoLink: String, detail: String, todoDate: String) {
        val request = MakeTodoRequest(detail = detail, title = title, todoDate = todoDate, todoLink = todoLink)

        val newTodoResponse = gitudyStudyRepository.makeNewTodo(studyInfoId, request)

        if (newTodoResponse.isSuccessful) {
            Log.d("AddTodoViewModel", newTodoResponse.code().toString())
        } else {
            Log.e("AddTodoViewModel", "newTodoResponse status: ${newTodoResponse.code()}\nnewTodoResponse message: ${newTodoResponse.errorBody()?.string()}")
        }
    }
}