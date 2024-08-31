package com.takseha.presentation.viewmodel.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.takseha.data.dto.home.Notice
import com.takseha.data.repository.gitudy.GitudyNoticeRepository
import com.takseha.presentation.viewmodel.common.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainHomeAlertViewModel : BaseViewModel() {
    private val gitudyNoticeRepository = GitudyNoticeRepository()

    private val _uiState = MutableStateFlow<List<Notice>?>(null)
    val uiState = _uiState.asStateFlow()

    fun getNoticeList(cursorTime: String?, limit: Long) =viewModelScope.launch {
        safeApiCall(
            apiCall = { gitudyNoticeRepository.getNoticeList(cursorTime, limit) },
            onSuccess = { response ->
                if (response.isSuccessful) {
                    val noticeList = response.body()
                    _uiState.update { noticeList }
                } else {
                    Log.e(
                        "MainHomeAlertViewModel",
                        "noticeListResponse status: ${response.code()}\nnoticeListResponse message: ${response.errorBody()?.string()}"
                    )
                }
            }
        )
    }

    fun deleteAllNotice(cursorTime: String?, limit: Long) = viewModelScope.launch {
        safeApiCall(
            apiCall = { gitudyNoticeRepository.deleteAllNotice() },
            onSuccess = { response ->
                if (response.isSuccessful) {
                    getNoticeList(cursorTime, limit)
                } else {
                    Log.e(
                        "MainHomeAlertViewModel",
                        "emptyListResponse status: ${response.code()}\nemptyListResponse message: ${response.errorBody()?.string()}"
                    )
                }
            }
        )
    }

    fun deleteNotice(id: String, cursorTime: String?, limit: Long) = viewModelScope.launch {
        safeApiCall(
            apiCall = { gitudyNoticeRepository.deleteNotice(id) },
            onSuccess = { response ->
                if (response.isSuccessful) {
                    getNoticeList(cursorTime, limit)
                    Log.d(
                        "MainHomeAlertViewModel",
                        "deleteNoticeListResponse status: ${response.code()}"
                    )
                } else {
                    Log.e(
                        "MainHomeAlertViewModel",
                        "deleteNoticeListResponse status: ${response.code()}\ndeleteNoticeListResponse message: ${response.errorBody()?.string()}"
                    )
                }
            }
        )
    }
}

