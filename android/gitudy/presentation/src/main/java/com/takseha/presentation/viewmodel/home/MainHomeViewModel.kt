package com.takseha.presentation.viewmodel.home

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
import kotlinx.coroutines.flow.MutableStateFlow
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

    init {
        viewModelScope.launch {
            getUserInfo()
            getMyStudyList(null, 10)
        }
    }

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
            updateProgressInfo()
        } else {
            Log.e(
                "MainHomeViewModel",
                "userInfoResponse status: ${userInfoResponse.code()}\nuserInfoResponse message: ${userInfoResponse.message()}"
            )
        }
    }

    private fun updateProgressInfo() {
        val uiStateValue = uiState.value
        val progressInfo = when (uiStateValue.score) {
            in 0..15 -> ProgressInfo(uiStateValue.score, R.drawable.character_bebe_to_15)
            in 16..30 -> ProgressInfo(uiStateValue.score - 15, R.drawable.character_bebe_to_30, 15)
            in 31..50 -> ProgressInfo(uiStateValue.score - 30, R.drawable.character_bebe_to_50, 20)
            in 51..70 -> ProgressInfo(uiStateValue.score - 50, R.drawable.character_bebe_to_70, 20)
            in 71..100 -> ProgressInfo(uiStateValue.score - 70, R.drawable.character_bebe_to_100, 30)
            in 101..130 -> ProgressInfo(uiStateValue.score - 100, R.drawable.character_bebe_to_130, 30)
            else -> ProgressInfo(1, R.drawable.character_bebe_to_130, 1)
        }
        _uiState.update {
            it.copy(
                progressScore = progressInfo.score,
                progressMax = progressInfo.max,
                characterImgSrc = progressInfo.imgSrc
            )
        }
    }

    suspend fun getMyStudyList(cursorIdx: Long?, limit: Long) {
        val myStudyListResponse = gitudyStudyRepository.getStudyList(
            cursorIdx,
            limit,
            sortBy = "createdDateTime",
            myStudy = true
        )
        val studyCnt = getStudyCount()?.count ?: -1

        if (myStudyListResponse.isSuccessful) {
            val myStudyListInfo = myStudyListResponse.body()!!

            _cursorIdxRes.value = myStudyListInfo.cursorIdx
            Log.d("MainHomeViewModel", "cursorIdx: ${_cursorIdxRes.value}")

            val studies = myStudyListInfo.studyInfoList
            if (studies.isEmpty()) {
                _myStudyState.update {
                    it.copy(
                        isMyStudiesEmpty = true,
                        studyCnt = studyCnt
                    )
                }
            } else {
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
                        studyCnt = studyCnt,
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

    private suspend fun getUrgentTodoProgress(studyInfoId: Int): TodoProgressResponse? {
        val urgentTodoResponse = gitudyStudyRepository.getTodoProgress(
            studyInfoId
        )

        if (urgentTodoResponse.isSuccessful) {
            return urgentTodoResponse.body()
        } else {
            Log.e(
                "MainHomeViewModel",
                "urgentTodoResponse status: ${urgentTodoResponse.code()}\nurgentTodoResponse message: ${urgentTodoResponse.message()}"
            )
        }
        return null
    }

    private suspend fun getStudyCount(): StudyCountResponse? {
        val studyCntResponse = gitudyStudyRepository.getStudyCount(true)

        if (studyCntResponse.isSuccessful) {
            return studyCntResponse.body()
        } else {
            Log.e(
                "MainHomeViewModel",
                "studyCntResponse status: ${studyCntResponse.code()}\nstudyCntResponse message: ${studyCntResponse.message()}"
            )
        }
        return null
    }

    private data class ProgressInfo(val score: Int, val imgSrc: Int, val max: Int = 15)
}

data class MyStudyWithTodo(
    val studyInfo: StudyInfo,
    val urgentTodo: TodoProgressResponse?
)

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
    var studyCnt: Int = 0,
    var isMyStudiesEmpty: Boolean = false
)