package com.takseha.presentation.viewmodel.mystudy

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.takseha.data.dto.feed.StudyCountResponse
import com.takseha.data.dto.feed.StudyInfo
import com.takseha.data.dto.mystudy.TodoProgressResponse
import com.takseha.data.repository.gitudy.GitudyAuthRepository
import com.takseha.data.repository.gitudy.GitudyStudyRepository
import com.takseha.presentation.R
import com.takseha.presentation.viewmodel.common.BaseViewModel
import com.takseha.presentation.viewmodel.home.MainHomeMyStudyUiState
import com.takseha.presentation.viewmodel.home.MyStudyWithTodo
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.Serializable

class MyStudyHomeViewModel : BaseViewModel() {
    private var gitudyStudyRepository = GitudyStudyRepository()

    private val _myStudyState = MutableStateFlow(MyStudyHomeUiState())
    val myStudyState = _myStudyState.asStateFlow()

    // stateflow로 바꾸는 거도 고민해보기~ 초기값 null 설정 가정
    private var _cursorIdxRes = MutableLiveData<Long?>()
    val cursorIdxRes: LiveData<Long?>
        get() = _cursorIdxRes

    fun getMyStudyList(cursorIdx: Long?, limit: Long, sortBy: String) = viewModelScope.launch {
        safeApiCall(
            apiCall = { gitudyStudyRepository.getStudyList(
                    cursorIdx,
                    limit,
                    sortBy = sortBy,
                    myStudy = true
                ) },
            onSuccess = { response ->
                if (response.isSuccessful) {
                    val myStudyListInfo = response.body()!!
                    _cursorIdxRes.value = myStudyListInfo.cursorIdx

                    val studies = myStudyListInfo.studyInfoList
                    if (studies.isEmpty()) {
                        _myStudyState.update {
                            it.copy(
                                myStudiesWithTodo = emptyList(),
                                isMyStudiesEmpty = true
                            )
                        }
                    } else {
                        viewModelScope.launch {
                            val studiesWithTodo = studies.map { study ->
                                val urgentTodo = getUrgentTodoProgress(study.id)
                                MyStudyWithTodo(
                                    study,
                                    urgentTodo
                                )
                            }
                            _myStudyState.update {
                                it.copy(
                                    myStudiesWithTodo = studiesWithTodo,
                                    isMyStudiesEmpty = false
                                )
                            }
                        }
                    }
                } else {
                    Log.e(
                        "MainHomeViewModel",
                        "myStudyListResponse status: ${response.code()}\nmyStudyListResponse message: ${
                            response.errorBody()?.string()
                        }"
                    )
                }
            }
        )
    }


    private suspend fun getUrgentTodoProgress(studyInfoId: Int): TodoProgressResponse? {
       return try {
           val response = gitudyStudyRepository.getTodoProgress(
               studyInfoId
           )
           if (response.isSuccessful) {
               response.body()
           } else {
               Log.e(
                   "MainHomeViewModel",
                   "urgentTodoResponse status: ${response.code()}\nurgentTodoResponse message: ${
                       response.errorBody()?.string()
                   }"
               )
                null
           }
       } catch (e: Exception) {
           Log.e("MainHomeViewModel", "Error fetching getUserInfo()", e)
           null
       }
    }

    suspend fun getStudyCount() {
        safeApiCall(
            apiCall = { gitudyStudyRepository.getStudyCount(true) },
            onSuccess = { response ->
                if (response.isSuccessful) {
                    val studyCnt = response.body()!!.count

                    _myStudyState.update {
                        it.copy(
                            studyCnt = studyCnt
                        )
                    }
                } else {
                    Log.e(
                        "MainHomeViewModel",
                        "studyCntResponse status: ${response.code()}\nstudyCntResponse message: ${
                            response.errorBody()?.string()
                        }"
                    )
                }
            }
        )
    }
}

data class MyStudyHomeUiState(
    var myStudiesWithTodo: List<MyStudyWithTodo> = listOf(),
    var studyCnt: Int = 0,
    var isMyStudiesEmpty: Boolean? = null
)