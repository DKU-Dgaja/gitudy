package com.takseha.presentation.viewmodel.common

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response

abstract class BaseApplicationViewModel(application: Application) : AndroidViewModel(application) {

    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage

    protected fun <T> safeApiCall(
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
        apiCall: suspend () -> T,
        onSuccess: (T) -> Unit,
        onError: (Exception?, T?) -> Unit = { e, response ->
            if (e != null) {
                _snackbarMessage.value = "네트워크 연결을 확인해주세요"
            } else {
                _snackbarMessage.value = null
            }
        }
    ) {
        viewModelScope.launch(dispatcher) {
            try {
                val result = apiCall()
                withContext(Dispatchers.Main) {
                    if (result is Response<*>) {
                        if (result.isSuccessful) {
                            onSuccess(result)
                        } else {
                            // 서버 에러 코드에 따라 다른 메시지를 설정할 수 있음
                            when (result.code()) {
                                502, 500 -> _snackbarMessage.value = "서버에 문제가 발생했어요. 잠시 후 다시 시도해주세요."
                                else -> onError(null, result)
                            }
                        }
                    } else {
                        onError(null, result)
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    onError(e, null)
                    Log.e("BaseViewModel", e.toString())
                }
            }
        }
    }

    fun resetSnackbarMessage() {
        _snackbarMessage.value = null
    }
}
