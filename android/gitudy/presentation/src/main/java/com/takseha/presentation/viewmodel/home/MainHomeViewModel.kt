package com.takseha.presentation.viewmodel.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.annotations.SerializedName
import com.takseha.data.dto.mystudy.Commit
import com.takseha.data.dto.mystudy.MyStudyWithTodo
import com.takseha.data.dto.mystudy.Todo
import com.takseha.data.dto.mystudy.TodoProgressResponse
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

class MainHomeViewModel : ViewModel() {
    private var gitudyAuthRepository = GitudyAuthRepository()
    private var gitudyStudyRepository = GitudyStudyRepository()

    private val _uiState = MutableStateFlow(MainHomeUserInfoUiState())
    val uiState = _uiState.asStateFlow()

    private val _myStudyState = MutableStateFlow(MainHomeMyStudyUiState())
    val myStudyState = _myStudyState.asStateFlow()

    // stateflow로 바꾸는 거도 고민해보기~ 초기값 null 설정 가정
    private var _cursorIdxRes = MutableLiveData<Long?>()
    val cursorIdxRes: LiveData<Long?>
        get() = _cursorIdxRes

    suspend fun getUserInfo() {
        val userInfoResponse = gitudyAuthRepository.getUserInfo()

        if (userInfoResponse.isSuccessful) {
            val userInfo = userInfoResponse.body()!!
            _uiState.update {
                it.copy(
                    name = userInfo.name,
                    score = userInfo.score,
                    githubId = userInfo.githubId,
                    profileImgUrl = userInfo.profileImageUrl,
                    rank = userInfo.rank
                )
            }
            getProgressInfo(uiState)
        } else {
            Log.e(
                "MainHomeViewModel",
                "userInfoResponse status: ${userInfoResponse.code()}\nuserInfoResponse message: ${userInfoResponse.message()}"
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
            cursorIdx,
            limit,
            sortBy = "createdDateTime",
            myStudy = true
        )

        if (myStudyListResponse.isSuccessful) {
            val myStudyListInfo = myStudyListResponse.body()!!

            _cursorIdxRes.value = myStudyListInfo.cursorIdx
            Log.d("MainHomeViewModel", "cursorIdx: ${_cursorIdxRes.value}")

            val studies = myStudyListInfo.studyInfoList
            if (studies.isEmpty()) {
                _myStudyState.update {
                    it.copy(
                        isMyStudiesEmpty = true
                    )
                }
            } else {
                val studiesWithTodo = studies.map { study ->
                    val todo = getFirstTodoInfo(study.id)

                    if (todo != null) {
                        if (todo.id != -1) {
                        //    val todoCheckNum = getTodoProgress(study.id)?.completeMemberCount ?: 0
                            val todoCheckNum = 0
                            val todoCheck =
                                if (todoCheckNum == study.maximumMember) TodoStatus.TODO_COMPLETE else TodoStatus.TODO_INCOMPLETE
                            MyStudyWithTodo(
                                backgroundColorList[study.id % 4],
                                study,
                                todo.title,
                                todo.todoDate,
                                todoCheck,
                                todoCheckNum
                            )
                        } else {
                            MyStudyWithTodo(
                                backgroundColorList[study.id % 4],
                                study,
                                null,
                                null,
                                TodoStatus.TODO_EMPTY,
                                null
                            )
                        }
                    } else {
                        MyStudyWithTodo(
                            backgroundColorList[study.id % 4],
                            study,
                            null,
                            null,
                            TodoStatus.TODO_INCOMPLETE,
                            null
                        )
                    }
                }
                _myStudyState.update {
                    it.copy(
                        myStudiesWithTodo = studiesWithTodo,
                        isMyStudiesEmpty = false
                    )
                }
            }
            Log.d("MainHomeViewModel", myStudyState.value.toString())
        } else {
            Log.e(
                "MainHomeViewModel",
                "myStudyListResponse status: ${myStudyListResponse.code()}\nmyStudyListResponse message: ${myStudyListResponse.message()}"
            )
        }
    }

    // TODO: getFirstTodoInfo api 관련 수정하기 -> 마감일 임박 투두 불러오기
    private suspend fun getFirstTodoInfo(studyInfoId: Int): Todo? {
        val todoInfoResponse = gitudyStudyRepository.getTodoList(
            studyInfoId,
            cursorIdx = null,
            limit = 1
        )

        if (todoInfoResponse.isSuccessful) {
            val todoBody = todoInfoResponse.body()!!

            if (todoBody.todoList.isNotEmpty()) {
                val todo = todoBody.todoList.first()
                return todo
            } else {
                Log.d("MainHomeViewModel", "No To-Do")
                return Todo(
                    detail = "No To-Do",
                    id = -1,
                    studyInfoId = studyInfoId,
                    title = "No To-Do",
                    todoDate = "",
                    todoCode = "",
                    todoLink = "",
                    commitList = listOf()
                )
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

    // TODO: getTodoProgress api 관련 수정
    private suspend fun getTodoProgress(studyInfoId: Int): TodoProgressResponse? {
        val todoProgressResponse = gitudyStudyRepository.getTodoProgress(
            studyInfoId
        )

        if (todoProgressResponse.isSuccessful) {
            return todoProgressResponse.body()
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
    var rank: Int = 0,
    var progressScore: Int = 0,
    var progressMax: Int = 15,
    var characterImgSrc: Int = R.drawable.character_bebe_to_15
) : Serializable

data class MainHomeMyStudyUiState(
    var myStudiesWithTodo: List<MyStudyWithTodo> = listOf(),
    var isMyStudiesEmpty: Boolean = false
)