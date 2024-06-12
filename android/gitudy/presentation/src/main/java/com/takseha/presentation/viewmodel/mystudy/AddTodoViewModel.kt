package com.takseha.presentation.viewmodel.mystudy

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.takseha.common.model.SPKey
import com.takseha.common.util.SP
import com.takseha.data.dto.mystudy.MakeTodoRequest
import com.takseha.data.repository.study.GitudyStudyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AddTodoViewModel(application: Application) : AndroidViewModel(application) {
    private var gitudyStudyRepository: GitudyStudyRepository = GitudyStudyRepository()
    private val prefs = SP(getApplication())

    fun makeNewTodo(studyInfoId: Int, title: String, todoLink: String, detail: String, todoDate: String) = viewModelScope.launch {
        val bearerToken = "Bearer ${prefs.loadPref(SPKey.ACCESS_TOKEN, "0")} ${
            prefs.loadPref(SPKey.REFRESH_TOKEN, "0")
        }"
        val request = MakeTodoRequest(detail, title, todoDate, todoLink)
        Log.d("AddTodoViewModel", request.toString())

        val newTodoResponse = gitudyStudyRepository.makeNewTodo(bearerToken, studyInfoId, request)

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