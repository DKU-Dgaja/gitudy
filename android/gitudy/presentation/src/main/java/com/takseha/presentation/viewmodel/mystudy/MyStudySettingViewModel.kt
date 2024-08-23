package com.takseha.presentation.viewmodel.mystudy

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.takseha.data.dto.mystudy.MakeTodoRequest
import com.takseha.data.repository.gitudy.GitudyMemberRepository
import com.takseha.data.repository.gitudy.GitudyStudyRepository
import kotlinx.coroutines.launch

class MyStudySettingViewModel: ViewModel() {
    private var gitudyMemberRepository = GitudyMemberRepository()

    suspend fun withdrawStudy(studyInfoId: Int) {
        val withdrawStudyResponse = gitudyMemberRepository.withdrawStudy(studyInfoId)

        if (withdrawStudyResponse.isSuccessful) {
            Log.d("MyStudySettingViewModel", withdrawStudyResponse.code().toString())
        } else {
            Log.e("MyStudySettingViewModel", "withdrawStudyResponse status: ${withdrawStudyResponse.code()}\nwithdrawStudyResponse message: ${withdrawStudyResponse.errorBody()?.string()}")
        }
    }
}