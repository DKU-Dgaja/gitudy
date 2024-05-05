package com.takseha.presentation.viewmodel.home

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.takseha.common.model.SPKey
import com.takseha.common.util.SP
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
        prefs.loadPref(SPKey.REFRESH_TOKEN, "0")}"

    private val _uiState = MutableStateFlow(MainHomeUserInfoUiState())
    val uiState = _uiState.asStateFlow()

    //live data로 할까?
    private var _cursorIdx = MutableLiveData<Long?>()
    val cursorIdx : LiveData<Long?>
        get() = _cursorIdx

    suspend fun getUserInfo() {
        val userInfoResponse = gitudyAuthRepository.getUserInfo(bearerToken)

        if (userInfoResponse.isSuccessful) {
            val resCode = userInfoResponse.body()!!.resCode
            val resMsg = userInfoResponse.body()!!.resMsg
            val userInfo = userInfoResponse.body()!!.userInfo

            if (resCode == 200 && resMsg == "OK") {
                _uiState.update { it.copy(name = userInfo.name, score = userInfo.score, githubId = userInfo.githubId, profileImgUrl = userInfo.profileImageUrl) }
                getProgressInfo(uiState)
            } else {
                Log.e("MainHomeViewModel", "https status error: $resCode, $resMsg")
            }
        } else {
            Log.e("MainHomeViewModel", "tokenResponse status: ${userInfoResponse.code()}\ntokenResponse message: ${userInfoResponse.message()}")
        }
    }

    private fun getProgressInfo(state: StateFlow<MainHomeUserInfoUiState>) {
        when (state.value.score) {
            in 0..15 -> _uiState.update { it.copy(progressScore = it.score) }
            in 16..30 -> _uiState.update { it.copy(progressScore = it.score - 15) }
            in 31..50 -> _uiState.update { it.copy(progressScore = it.score - 30, progressMax = 20) }
            in 51..70 -> _uiState.update { it.copy(progressScore = it.score - 50, progressMax = 20) }
            in 71..100 -> _uiState.update { it.copy(progressScore = it.score - 70, progressMax = 30) }
            in 101..130 -> _uiState.update { it.copy(progressScore = it.score - 100, progressMax = 30) }
            else -> _uiState.update { it.copy(progressScore = 1, progressMax = 1) }
        }
    }

    fun getMyStudyList(cursorIdx: Long?) = viewModelScope.launch {
        val myStudyListResponse = gitudyStudyRepository.getStudyList(bearerToken, cursorIdx, myStudy = true)

        if (myStudyListResponse.isSuccessful) {
            val resCode = myStudyListResponse.body()!!.resCode
            val resMsg = myStudyListResponse.body()!!.resMsg
            val myStudyListInfo = myStudyListResponse.body()!!.studyListInfo


            if (resCode == 200 && resMsg == "OK") {
                _cursorIdx.value = myStudyListInfo.cursorIdx
                // recyclerview 관련 myStudyList 업데이트 기능 구현

                Log.d("MainHomeViewModel", _cursorIdx.value.toString())
            } else {
                Log.e("MainHomeViewModel", "https status error: $resCode, $resMsg")
            }
        } else {
            Log.e("MainHomeViewModel", "tokenResponse status: ${myStudyListResponse.code()}\ntokenResponse message: ${myStudyListResponse.message()}")
        }
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
): Serializable