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
        apiCall: suspend () -> Response<T>,
        onSuccess: (Response<T>) -> Unit,
        onError: (Exception) -> Unit = {
            _snackbarMessage.value = "네트워크 연결을 확인해주세요"
        }
    ) {
        viewModelScope.launch(dispatcher) {
            try {
                val result = apiCall()
                withContext(Dispatchers.Main) {
                    if (result.isSuccessful) {
                        result.body()?.let { onSuccess(result) }
                    } else {
                        if (result.code() == 502 || result.code() == 500) {
                            _snackbarMessage.value = "서버에 문제가 발생했어요. 잠시 후 다시 시도해주세요."
                        } else {
                            _snackbarMessage.value = "알 수 없는 오류가 발생했어요. 잠시 후 다시 시도해주세요"
                        }
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    onError(e)
                    Log.e("BaseViewModel", e.toString())
                }
            }
        }
    }

    fun resetSnackbarMessage() {
        _snackbarMessage.value = null
    }
}
