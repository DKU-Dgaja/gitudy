package com.takseha.presentation.viewmodel.mystudy

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.takseha.data.dto.mystudy.MakeTodoRequest
import com.takseha.data.repository.study.GitudyStudyRepository
import kotlinx.coroutines.launch

class AddTodoViewModel: ViewModel() {
    private var gitudyStudyRepository: GitudyStudyRepository = GitudyStudyRepository()

    fun makeNewTodo(studyInfoId: Int, title: String, todoLink: String, detail: String, todoDate: String) = viewModelScope.launch {
        val request = MakeTodoRequest(detail, title, todoDate, todoLink)
        Log.d("AddTodoViewModel", request.toString())

        val newTodoResponse = gitudyStudyRepository.makeNewTodo(studyInfoId, request)

        if (newTodoResponse.isSuccessful) {
            val resCode = newTodoResponse.body()!!.resCode
            val resMsg = newTodoResponse.body()!!.resMsg
            val resObj = newTodoResponse.body()!!.resObj

            if (resCode == 200 && resMsg == "OK") {
                Log.d("AddTodoViewModel", resObj)
            } else {
                Log.e("AddTodoViewModel", "https status error: $resCode, $resMsg")
            }
        } else {
            Log.e("AddTodoViewModel", "newTodoResponse status: ${newTodoResponse.code()}\nnewTodoResponse message: ${newTodoResponse.message()}")
        }
    }
}