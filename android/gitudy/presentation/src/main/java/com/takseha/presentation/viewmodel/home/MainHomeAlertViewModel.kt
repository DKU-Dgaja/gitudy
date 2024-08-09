package com.takseha.presentation.viewmodel.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.takseha.data.dto.home.Notice
import com.takseha.data.repository.gitudy.GitudyNoticeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainHomeAlertViewModel : ViewModel() {
    private var gitudyNoticeRepository = GitudyNoticeRepository()

    private val _uiState = MutableStateFlow<List<Notice>>(emptyList())
    val uiState = _uiState.asStateFlow()

    fun getNoticeList(cursorTime: String?, limit: Long) = viewModelScope.launch  {
        val noticeListResponse = gitudyNoticeRepository.getNoticeList(cursorTime, limit)

        if (noticeListResponse.isSuccessful) {
            val noticeList = noticeListResponse.body()
            _uiState.update { noticeList!! }
        } else {
            Log.e(
                "MainHomeAlertViewModel",
                "noticeListResponse status: ${noticeListResponse.code()}\nnoticeListResponse message: ${noticeListResponse.message()}"
            )
        }
    }
}