package com.takseha.presentation.viewmodel.auth

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

// MVI 구조
class PopupAgreementViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(PopupAgreementUiState())
    val uiState = _uiState.asStateFlow()

    fun handleIntent(intent: PopupAgreementIntent) {
        when (intent) {
            PopupAgreementIntent.CheckAllAgreeBtn -> checkAllAgreeBtn()
            PopupAgreementIntent.CheckCheckBtn1 -> checkCheckBtn1()
            PopupAgreementIntent.CheckCheckBtn2 -> checkCheckBtn2()
            PopupAgreementIntent.CheckCheckBtn3 -> checkCheckBtn3()
            PopupAgreementIntent.PushAlarmYnBtnChecked -> checkPushAlarmYnBtn()
        }
    }
    private fun checkAllAgreeBtn() {
        val state = uiState.value
        if (!state.isCheckBtn1Checked || !state.isCheckBtn2Checked || !state.isCheckBtn3Checked || !state.isPushAlarmYnBtnChecked) {
            _uiState.update {
                PopupAgreementUiState(
                    isCheckBtn1Checked = true,
                    isCheckBtn2Checked = true,
                    isCheckBtn3Checked = true,
                    isPushAlarmYnBtnChecked = true
                )
            }
        } else {
            _uiState.update {
                PopupAgreementUiState()
            }
        }
    }
    private fun checkCheckBtn1() {
        _uiState.update {
            it.copy(isCheckBtn1Checked = !it.isCheckBtn1Checked)
        }
    }
    private fun checkCheckBtn2() {
        _uiState.update {
            it.copy(isCheckBtn2Checked = !it.isCheckBtn2Checked)
        }
    }
    private fun checkCheckBtn3() {
        _uiState.update {
            it.copy(isCheckBtn3Checked = !it.isCheckBtn3Checked)
        }
    }
    private fun checkPushAlarmYnBtn() {
        _uiState.update {
            it.copy(isPushAlarmYnBtnChecked = !it.isPushAlarmYnBtnChecked)
        }
    }
}

data class PopupAgreementUiState(
    val isCheckBtn1Checked: Boolean = false,
    val isCheckBtn2Checked: Boolean = false,
    val isCheckBtn3Checked: Boolean = false,
    val isPushAlarmYnBtnChecked: Boolean = false
)

sealed interface PopupAgreementIntent {
    object CheckAllAgreeBtn : PopupAgreementIntent
    object CheckCheckBtn1 : PopupAgreementIntent
    object CheckCheckBtn2 : PopupAgreementIntent
    object CheckCheckBtn3 : PopupAgreementIntent
    object PushAlarmYnBtnChecked : PopupAgreementIntent
}