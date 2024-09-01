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

abstract class BaseViewModel : ViewModel() {

    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage

    protected fun <T> safeApiCall(
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
        apiCall: suspend () -> T,
        onSuccess: (T) -> Unit,
        onError: (Exception) -> Unit = {
            _snackbarMessage.value = "네트워크 연결을 확인해주세요"
        }
    ) {
        viewModelScope.launch(dispatcher) {
            try {
                val result = apiCall()
                withContext(Dispatchers.Main) {
                    onSuccess(result)
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
