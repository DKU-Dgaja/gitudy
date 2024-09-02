package com.takseha.presentation.viewmodel.mystudy

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.takseha.data.dto.mystudy.MakeTodoRequest
import com.takseha.data.repository.gitudy.GitudyStudyRepository
import com.takseha.presentation.viewmodel.common.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AddTodoViewModel: BaseViewModel() {
    private var gitudyStudyRepository: GitudyStudyRepository = GitudyStudyRepository()

    private val _responseState = MutableStateFlow<Boolean?>(null)
    val responseState = _responseState.asStateFlow()

    suspend fun makeNewTodo(studyInfoId: Int, title: String, todoLink: String, detail: String, todoDate: String) {
        val request = MakeTodoRequest(detail = detail, title = title, todoDate = todoDate, todoLink = todoLink)
        safeApiCall(
            apiCall = { gitudyStudyRepository.makeNewTodo(studyInfoId, request) },
            onSuccess = { response ->
                if (response.isSuccessful) {
                    _responseState.value = true
                    Log.d("AddTodoViewModel", response.code().toString())
                } else {
                    _responseState.value = false
                    Log.e("AddTodoViewModel", "newTodoResponse status: ${response.code()}\nnewTodoResponse message: ${response.errorBody()?.string()}")
                }
            }
        )
    }
}