package com.takseha.presentation.viewmodel.home

import android.app.Application
import android.graphics.Color
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.takseha.common.model.SPKey
import com.takseha.common.util.SP
import com.takseha.data.dto.mystudy.MyStudyWithTodo
import com.takseha.data.dto.mystudy.Todo
import com.takseha.data.dto.mystudy.TodoProgress
import com.takseha.data.dto.mystudy.TodoStatus
import com.takseha.data.repository.auth.GitudyAuthRepository
import com.takseha.data.repository.study.GitudyStudyRepository
import com.takseha.presentation.R
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

    private val _myStudyState = MutableStateFlow(MainHomeMyStudyUiState())
    val myStudyState = _myStudyState.asStateFlow()

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
            in 16..30 -> _uiState.update {
                it.copy(
                    progressScore = it.score - 15,
                    characterImgSrc = R.drawable.character_bebe_to_30
                )
            }

            in 31..50 -> _uiState.update {
                it.copy(
                    progressScore = it.score - 30,
                    progressMax = 20,
                    characterImgSrc = R.drawable.character_bebe_to_50
                )
            }

            in 51..70 -> _uiState.update {
                it.copy(
                    progressScore = it.score - 50,
                    progressMax = 20,
                    characterImgSrc = R.drawable.character_bebe_to_70
                )
            }

            in 71..100 -> _uiState.update {
                it.copy(
                    progressScore = it.score - 70,
                    progressMax = 30,
                    characterImgSrc = R.drawable.character_bebe_to_100
                )
            }

            in 101..130 -> _uiState.update {
                it.copy(
                    progressScore = it.score - 100,
                    progressMax = 30,
                    characterImgSrc = R.drawable.character_bebe_to_130
                )
            }

            else -> _uiState.update {
                it.copy(
                    progressScore = 1,
                    progressMax = 1,
                    characterImgSrc = R.drawable.character_bebe_to_130
                )
            }
        }
    }

    fun getMyStudyList(cursorIdx: Long?, limit: Long) = viewModelScope.launch {
        val backgroundColorList = listOf("#f8a7a7", "#f8dea6", "#d3f3be", "#85b0e9")

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
                val studiesWithTodo = studies.map { study ->
                    val todo = getFirstTodoInfo(study.id)

                    if (todo != null) {
                        val todoCheckNum = getTodoProgress(study.id)?.completeMemberCount ?: -1
                        val todoCheck = if (todoCheckNum == study.maximumMember) TodoStatus.TODO_COMPLETE else if (todoCheckNum == -1) TodoStatus.TODO_EMPTY else TodoStatus.TODO_INCOMPLETE
                        MyStudyWithTodo(backgroundColorList[study.id % 4], study, todo.title, todo.todoDate, todoCheck, todoCheckNum)
                    } else {
                        MyStudyWithTodo(backgroundColorList[study.id % 4], study, null, null, TodoStatus.TODO_EMPTY, null)
                    }
                }
                _myStudyState.update {
                    it.copy(
                        myStudiesWithTodo = studiesWithTodo
                    )
                }

                Log.d("MainHomeViewModel", "cursorIdx: ${_cursorIdxRes.value}")
                Log.d("MainHomeViewModel", "_uiState: ${_uiState.value}")
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
            Log.d("MainHomeViewModel", "todo body: $todoBody")

            if (resCode == 200 && resMsg == "OK") {
                if (todoBody.todoList.isNotEmpty()) {
                    val todo = todoBody.todoList.first()
                    Log.d("MainHomeViewModel", "todo first: $todo")

                    return todo
                } else {
                    Log.d("MainHomeViewModel", "No To-Do")
                    return null
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
        // 에러 발생 시 null return
        Log.d("MainHomeViewModel", "Error")
        return null
    }

    private suspend fun getTodoProgress(studyInfoId: Int): TodoProgress? {
        val todoProgressResponse = gitudyStudyRepository.getTodoProgress(
            bearerToken,
            studyInfoId
        )

        if (todoProgressResponse.isSuccessful) {
            val resCode = todoProgressResponse.body()!!.resCode
            val resMsg = todoProgressResponse.body()!!.resMsg
            val todoProgress = todoProgressResponse.body()!!.todoProgress

            if (resCode == 200 && resMsg == "OK") {
                return todoProgress
            } else {
                Log.e("MainHomeViewModel", "https status error: $resCode, $resMsg")
            }
        } else {
            Log.e(
                "MainHomeViewModel",
                "todoProgressResponse status: ${todoProgressResponse.code()}\ntodoProgressResponse message: ${todoProgressResponse.message()}"
            )
        }
        return null
    }
}

data class MainHomeUserInfoUiState(
    var name: String = "",
    var score: Int = 0,
    var githubId: String = "",
    var profileImgUrl: String = "",
//    var rank: Int,
    var progressScore: Int = 0,
    var progressMax: Int = 15,
    var characterImgSrc: Int = R.drawable.character_bebe_to_15,
) : Serializable

data class MainHomeMyStudyUiState(
    var myStudiesWithTodo: List<MyStudyWithTodo> = listOf()
)