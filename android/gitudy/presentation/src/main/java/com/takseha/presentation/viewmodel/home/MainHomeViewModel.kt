package com.takseha.presentation.viewmodel.home

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.takseha.common.model.SPKey
import com.takseha.common.util.SP
import com.takseha.data.dto.mystudy.MyStudyWithTodo
import com.takseha.data.dto.mystudy.Todo
import com.takseha.data.repository.auth.GitudyAuthRepository
import com.takseha.data.repository.study.GitudyStudyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.Serializable

class MainHomeViewModel(application: Application) : AndroidViewModel(application) {
    private var gitudyAuthRepository: GitudyAuthRepository = GitudyAuthRepository()
    private var gitudyStudyRepository: GitudyStudyRepository = GitudyStudyRepository()
    private val prefs = SP(getApplication())

    private val bearerToken = "Bearer ${prefs.loadPref(SPKey.ACCESS_TOKEN, "0")} ${
        prefs.loadPref(SPKey.REFRESH_TOKEN, "0")
    }"

    private val _uiState = MutableStateFlow(MainHomeUserInfoUiState())
    val uiState = _uiState.asStateFlow()

    private var _myStudyWithTodo = MutableLiveData<List<MyStudyWithTodo>>()
    val myStudyWithTodo: LiveData<List<MyStudyWithTodo>>
        get() = _myStudyWithTodo

    // stateflow로 바꾸는 거도 고민해보기~ 초기값 null 설정 가정
    private var _cursorIdxRes = MutableLiveData<Long?>()
    val cursorIdxRes: LiveData<Long?>
        get() = _cursorIdxRes

    suspend fun getUserInfo() {
        val userInfoResponse = gitudyAuthRepository.getUserInfo(bearerToken)

        if (userInfoResponse.isSuccessful) {
            val resCode = userInfoResponse.body()!!.resCode
            val resMsg = userInfoResponse.body()!!.resMsg
            val userInfo = userInfoResponse.body()!!.userInfo

            if (resCode == 200 && resMsg == "OK") {
                _uiState.update {
                    it.copy(
                        name = userInfo.name,
                        score = userInfo.score,
                        githubId = userInfo.githubId,
                        profileImgUrl = userInfo.profileImageUrl
                    )
                }
                getProgressInfo(uiState)
            } else {
                Log.e("MainHomeViewModel", "https status error: $resCode, $resMsg")
            }
        } else {
            Log.e(
                "MainHomeViewModel",
                "tokenResponse status: ${userInfoResponse.code()}\ntokenResponse message: ${userInfoResponse.message()}"
            )
        }
    }

    private fun getProgressInfo(state: StateFlow<MainHomeUserInfoUiState>) {
        when (state.value.score) {
            in 0..15 -> _uiState.update { it.copy(progressScore = it.score) }
            in 16..30 -> _uiState.update { it.copy(progressScore = it.score - 15) }
            in 31..50 -> _uiState.update {
                it.copy(
                    progressScore = it.score - 30,
                    progressMax = 20
                )
            }

            in 51..70 -> _uiState.update {
                it.copy(
                    progressScore = it.score - 50,
                    progressMax = 20
                )
            }

            in 71..100 -> _uiState.update {
                it.copy(
                    progressScore = it.score - 70,
                    progressMax = 30
                )
            }

            in 101..130 -> _uiState.update {
                it.copy(
                    progressScore = it.score - 100,
                    progressMax = 30
                )
            }

            else -> _uiState.update { it.copy(progressScore = 1, progressMax = 1) }
        }
    }

    fun getMyStudyList(cursorIdx: Long?, limit: Long) = viewModelScope.launch {
        val myStudyListResponse = gitudyStudyRepository.getStudyList(
            bearerToken,
            cursorIdx,
            limit,
            sortBy = "createdDateTime",
            myStudy = true
        )

        if (myStudyListResponse.isSuccessful) {
            val resCode = myStudyListResponse.body()!!.resCode
            val resMsg = myStudyListResponse.body()!!.resMsg
            val myStudyListInfo = myStudyListResponse.body()!!.studyListInfo


            if (resCode == 200 && resMsg == "OK") {
                _cursorIdxRes.value = myStudyListInfo.cursorIdx

                val studies = myStudyListInfo.studyInfoList
                val myStudiesWithTodo = studies.map { study ->
                    val todo = getFirstTodoInfo(study.id)
                    val todoCheck = "임시 todoCheck"
                    val todoCheckNum = 1    // 임시 todoCheckNum
                    MyStudyWithTodo(study, todo.title, todo.todoDate, todoCheck, todoCheckNum)
                }
                _myStudyWithTodo.postValue(myStudiesWithTodo)

                Log.d("MainHomeViewModel", "cursorIdx: ${_cursorIdxRes.value}")
                Log.d("MainHomeViewModel", "myStudyLiveData: ${_myStudyWithTodo.value}")
            } else {
                Log.e("MainHomeViewModel", "https status error: $resCode, $resMsg")
            }
        } else {
            Log.e(
                "MainHomeViewModel",
                "myStudyListResponse status: ${myStudyListResponse.code()}\nmyStudyListResponse message: ${myStudyListResponse.message()}"
            )
        }
    }

    private suspend fun getFirstTodoInfo(studyInfoId: Int): Todo {
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
            Log.d("MainHomeViewModel", "todo body: $todoBody")

            if (resCode == 200 && resMsg == "OK") {
                if (todoBody.todoList.isNotEmpty()) {
                    val todo = todoBody.todoList.first()
                    Log.d("MainHomeViewModel", "todo first: $todo")

                    return todo
                } else {
                    return Todo("", 0, studyInfoId, "To-Do를 생성해주세요!", "", "")
                }
            } else {
                Log.e("MainHomeViewModel", "https status error: $resCode, $resMsg")
            }
        } else {
            Log.e(
                "MainHomeViewModel",
                "todoInfoResponse status: ${todoInfoResponse.code()}\ntodoInfoResponse message: ${todoInfoResponse.message()}"
            )
        }
        // 에러 발생 시 빈 Todo를 return
        return Todo("",0,0,"","","")
    }
}

data class MainHomeUserInfoUiState(
    var name: String = "",
    var score: Int = 0,
    var githubId: String = "",
    var profileImgUrl: String = "",
//    var rank: Int,
    var progressScore: Int = 0,
    var progressMax: Int = 15
) : Serializable