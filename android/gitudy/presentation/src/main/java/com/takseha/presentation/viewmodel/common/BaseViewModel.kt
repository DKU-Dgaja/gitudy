package com.takseha.presentation.viewmodel.common

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response

abstract class BaseViewModel : ViewModel() {

    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage

    protected fun <T> safeApiCall(
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
        apiCall: suspend () -> T,
        onSuccess: (T) -> Unit,
        onError: (Exception?, Response<*>?) -> Unit = { e, response ->
            viewModelScope.launch {
                if (e != null) {
                    _snackbarMessage.emit("네트워크 연결을 확인해주세요")
                } else {
                    _snackbarMessage.emit(null)
                }
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
                                502, 500 -> {
                                    onError(null, result)
                                    _snackbarMessage.emit("서버에 문제가 발생했어요. 잠시 후 다시 시도해주세요.")
                                }
                                else -> onError(null, result)
                            }
                        }
                    } else {
                        onSuccess(result)
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

    protected fun handleDefaultError(e: Exception?) {
        viewModelScope.launch {
            if (e != null) {
                _snackbarMessage.emit("네트워크 연결을 확인해주세요")
            } else {
                _snackbarMessage.emit(null)
            }
        }
    }


    fun resetSnackbarMessage() {
        _snackbarMessage.value = null
    }
}
