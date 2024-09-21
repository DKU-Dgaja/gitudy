import android.util.Log
import com.takseha.data.dto.auth.login.RoleStatus
import com.takseha.data.repository.gitudy.GitudyAuthRepository
import com.takseha.presentation.viewmodel.common.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class SplashViewModel : BaseViewModel() {
    private val gitudyAuthRepository = GitudyAuthRepository()

    private val _availableTokenCheck = MutableStateFlow<Boolean?>(null)
    val availableTokenCheck = _availableTokenCheck.asStateFlow()

    suspend fun checkAvailableToken() {
        safeApiCall(
            apiCall = { gitudyAuthRepository.getUserInfo() },
            onSuccess = { response ->
                if (response.isSuccessful) {
                    val role = response.body()?.role
                    _availableTokenCheck.value =
                        !(role == RoleStatus.WITHDRAW || role == RoleStatus.UNAUTH)
                }
            },
            onError = { e, response ->
                super.handleDefaultError(e)
                super.resetSnackbarMessage()
                e?.let {
                    Log.e("SplashViewModel", "Exception: ${it.message}")
                } ?: run {
                    response?.let {
                        if (response.code() == 400 || response.code() == 401 || response.code() == 403) {
                            _availableTokenCheck.value = false
                        }
                        Log.e("SplashViewModel", "HTTP Error: ${it.code()} ${it.message()}")
                    }
                }
            }
        )
    }
}
