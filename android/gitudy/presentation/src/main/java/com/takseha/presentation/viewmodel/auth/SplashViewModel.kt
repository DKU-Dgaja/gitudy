import android.util.Log
import com.takseha.data.dto.auth.login.RoleStatus
import com.takseha.data.repository.gitudy.GitudyAuthRepository
import com.takseha.presentation.viewmodel.common.BaseViewModel
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
                } else {
                    if (response.code() == 401 || response.code() == 403) {
                        // 토큰 재발급
                        _availableTokenCheck.value = false
                    } else {
                        // 기타 서버 에러
                    }
                    Log.e(
                        "SplashViewModel",
                        "checkTokenResponse status: ${response.code()}\ncheckTokenResponse message: ${
                            response.errorBody()!!.string()
                        }"
                    )
                }
            }
        )
    }
}
