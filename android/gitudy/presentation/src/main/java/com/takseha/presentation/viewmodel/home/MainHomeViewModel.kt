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

    fun getMyStudyList(cursorIdx: Long?, limit: Long) = viewModelScope.launch {
        safeApiCall(
            apiCall = {
                val myStudyListResponse = async {
                    gitudyStudyRepository.getStudyList(
                        cursorIdx,
                        limit,
                        sortBy = "createdDateTime",
                        myStudy = true
                    )
                }
                val studyCntResponse = async { gitudyStudyRepository.getStudyCount(true) }

                // Pair로 두 가지 결과를 반환
                Pair(
                    myStudyListResponse.await(),
                    studyCntResponse.await()
                )
            },
            onSuccess = { (myStudyListResponse, studyCntResponse) ->
                if (myStudyListResponse.isSuccessful && studyCntResponse.isSuccessful) {
                    val myStudyListInfo = myStudyListResponse.body()!!
                    val studyCnt = studyCntResponse.body()!!.count

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
                        viewModelScope.launch {
                            // 비동기적으로 urgentTodo를 가져온 후 리스트를 업데이트
                            val studiesWithTodo = studies.map { study ->
                                val urgentTodo = async { getUrgentTodoProgress(study.id) }.await()
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
                    }
                } else {
                    Log.e(
                        "MainHomeViewModel",
                        "myStudyListResponse status: ${myStudyListResponse.code()}\nmyStudyListResponse message: ${
                            myStudyListResponse.errorBody()?.string()
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

    private suspend fun getStudyCount(): StudyCountResponse? {
        return try {
            val response = gitudyStudyRepository.getStudyCount(true)
            if (response.isSuccessful) {
                response.body()
            } else {
                Log.e(
                    "MainHomeViewModel",
                    "studyCntResponse status: ${response.code()}\nstudyCntResponse message: ${
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