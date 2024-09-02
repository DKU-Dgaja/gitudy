package com.takseha.presentation.viewmodel.mystudy

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.takseha.data.dto.mystudy.MakeTodoRequest
import com.takseha.data.dto.mystudy.UpdateStudyInfoRequest
import com.takseha.data.repository.gitudy.GitudyMemberRepository
import com.takseha.data.repository.gitudy.GitudyStudyRepository
import com.takseha.presentation.viewmodel.common.BaseViewModel
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MyStudySettingViewModel: BaseViewModel() {
    private var gitudyMemberRepository = GitudyMemberRepository()
    private var gitudyStudyRepository = GitudyStudyRepository()

    suspend fun withdrawStudy(studyInfoId: Int) {
        safeApiCall(
            apiCall = { gitudyMemberRepository.withdrawStudy(studyInfoId) },
            onSuccess = { response ->
                if (response.isSuccessful) {
                    Log.d("MyStudySettingViewModel", response.code().toString())
                } else {
                    Log.e("MyStudySettingViewModel", "withdrawStudyResponse status: ${response.code()}\nwithdrawStudyResponse message: ${response.errorBody()?.string()}")
                }
            }
        )
    }

//    suspend fun endStudy(studyInfoId: Int) {
//        val request = UpdateStudyInfoRequest()
//        val endStudyResponse = gitudyStudyRepository.updateStudyInfo(studyInfoId)
//
//        if (endStudyResponse.isSuccessful) {
//
//            Log.d("MyStudySettingViewModel", endStudyResponse.code().toString())
//        } else {
//            Log.e("MyStudySettingViewModel", "endStudyResponse status: ${endStudyResponse.code()}\nendStudyResponse message: ${endStudyResponse.errorBody()?.string()}")
//        }
//    }
}