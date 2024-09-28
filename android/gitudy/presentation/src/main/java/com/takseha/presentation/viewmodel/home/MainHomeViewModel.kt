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
import com.takseha.data.repository.gitudy.GitudyNoticeRepository
import com.takseha.data.repository.gitudy.GitudyStudyRepository
import com.takseha.presentation.R
import com.takseha.presentation.viewmodel.common.BaseViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.Serializable

class MainHomeViewModel : BaseViewModel() {
    private var gitudyAuthRepository = GitudyAuthRepository()
    private var gitudyStudyRepository = GitudyStudyRepository()
    private val gitudyNoticeRepository = GitudyNoticeRepository()

    private val _uiState = MutableStateFlow(MainHomeUserInfoUiState())
    val uiState = _uiState.asStateFlow()

    private val _myStudyState = MutableStateFlow(MainHomeMyStudyUiState())
    val myStudyState = _myStudyState.asStateFlow()

    // stateflow로 바꾸는 거도 고민해보기~ 초기값 null 설정 가정
    private var _cursorIdxRes = MutableLiveData<Long?>()
    val cursorIdxRes: LiveData<Long?>
        get() = _cursorIdxRes

    fun getUserInfo() = viewModelScope.launch {
        safeApiCall(
            apiCall = { gitudyAuthRepository.getUserInfo() },
            onSuccess = { response ->
                if (response.isSuccessful) {
                    val userInfo = response.body()!!
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
                        "userInfoResponse status: ${response.code()}\nuserInfoResponse message: ${response.message()}"
                    )
                }
            }
        )
    }

    private fun updateProgressInfo() {
        val uiStateValue = uiState.value
        val progressInfo = when (uiStateValue.score) {
            in 0..15 -> ProgressInfo(uiStateValue.score, R.drawable.character_bebe_to_15)
            in 16..30 -> ProgressInfo(uiStateValue.score - 15, R.drawable.character_bebe_to_30, 15)
            in 31..50 -> ProgressInfo(uiStateValue.score - 30, R.drawable.character_bebe_to_50, 20)
            in 51..70 -> ProgressInfo(uiStateValue.score - 50, R.drawable.character_bebe_to_70, 20)
            in 71..100 -> ProgressInfo(
                uiStateValue.score - 70,
                R.drawable.character_bebe_to_100,
                30
            )

            in 101..130 -> ProgressInfo(
                uiStateValue.score - 100,
                R.drawable.character_bebe_to_130,
                30
            )

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

    fun getAlertCount(cursorTime: String?, limit: Long) =viewModelScope.launch {
        safeApiCall(
            apiCall = { gitudyNoticeRepository.getNoticeList(cursorTime, limit) },
            onSuccess = { response ->
                if (response.isSuccessful) {
                    val noticeList = response.body()

                    if (noticeList?.isEmpty() != false) {
                        _uiState.update { it.copy(
                            isAlert = false
                        ) }
                    } else {
                        _uiState.update { it.copy(
                            isAlert = true
                        ) }
                    }

                } else {
                    Log.e(
                        "MainHomeViewModel",
                        "isAlertResponse status: ${response.code()}\nisAlertResponse message: ${response.errorBody()?.string()}"
                    )
                }
            }
        )
    }

    private data class ProgressInfo(val score: Int, val imgSrc: Int, val max: Int = 15)
}

data class MyStudyWithTodo(
    val studyInfo: StudyInfo,
    val urgentTodo: TodoProgressResponse?
)

data class MainHomeUserInfoUiState(
    val name: String = "",
    val score: Int = 0,
    val githubId: String = "",
    val profileImgUrl: String = "",
    val rank: Int = 0,
    val progressScore: Int = 0,
    val progressMax: Int = 15,
    val characterImgSrc: Int = R.drawable.character_bebe_to_15,
    val isAlert: Boolean = false
) : Serializable

data class MainHomeMyStudyUiState(
    var myStudiesWithTodo: List<MyStudyWithTodo> = listOf(),
    var studyCnt: Int = 0,
    var isMyStudiesEmpty: Boolean = false
)