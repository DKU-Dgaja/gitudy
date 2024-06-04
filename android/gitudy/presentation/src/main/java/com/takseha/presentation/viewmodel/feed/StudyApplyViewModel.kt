package com.takseha.presentation.viewmodel.feed

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.takseha.common.model.SPKey
import com.takseha.common.util.SP
import com.takseha.data.dto.mystudy.MyStudyInfo
import com.takseha.data.repository.study.GitudyStudyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class StudyApplyViewModel(application: Application) : AndroidViewModel(application) {
    private var gitudyStudyRepository: GitudyStudyRepository = GitudyStudyRepository()
    private val prefs = SP(getApplication())

    private val bearerToken = "Bearer ${prefs.loadPref(SPKey.ACCESS_TOKEN, "0")} ${
        prefs.loadPref(SPKey.REFRESH_TOKEN, "0")
    }"

    private val _uiState = MutableStateFlow(StudyMainInfoState())
    val uiState = _uiState.asStateFlow()

    fun getStudyInfo(studyInfoId: Int) = viewModelScope.launch {
        val myStudyInfoResponse = gitudyStudyRepository.getMyStudyInfo(bearerToken, studyInfoId)

        if (myStudyInfoResponse.isSuccessful) {
            val resCode = myStudyInfoResponse.body()!!.resCode
            val resMsg = myStudyInfoResponse.body()!!.resMsg
            val myStudyInfo = myStudyInfoResponse.body()!!.myStudyInfo


            if (resCode == 200 && resMsg == "OK") {
                _uiState.update {
                    it.copy(
                        studyInfo = myStudyInfo,
                    )
                }

                Log.d("MyStudyMainViewModel", "_uiState: ${_uiState.value}")
            } else {
                Log.e("MyStudyMainViewModel", "https status error: $resCode, $resMsg")
            }
        } else {
            Log.e(
                "MyStudyMainViewModel",
                "myStudyInfoResponse status: ${myStudyInfoResponse.code()}\nmyStudyInfoResponse message: ${myStudyInfoResponse.message()}"
            )
        }
    }
}

data class StudyMainInfoState(
    var studyInfo: MyStudyInfo = MyStudyInfo(),
)