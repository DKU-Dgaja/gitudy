package com.takseha.data.token

import android.content.Context
import android.util.Log
import com.takseha.data.BuildConfig
import com.takseha.data.api.gitudy.auth.GitudyAuthService
import com.takseha.data.dto.auth.login.LoginPageInfoResponse
import com.takseha.data.dto.auth.login.TokenResponse
import com.takseha.data.dto.auth.login.ReissueTokenResponse
import com.takseha.data.dto.auth.register.RegisterRequest
import com.takseha.data.sharedPreferences.SP
import com.takseha.data.sharedPreferences.SPKey
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class TokenManager(context: Context) {
    private val prefs = SP(context)
    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.GITUDY_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val loginApi = retrofit.create(GitudyAuthService::class.java)


    var accessToken: String?
        get() = prefs.loadPref(SPKey.ACCESS_TOKEN, "0")
        set(value) {
            prefs.savePref(SPKey.ACCESS_TOKEN, value!!)
        }

    var refreshToken: String?
        get() = prefs.loadPref(SPKey.REFRESH_TOKEN, "0")
        set(value) {
            prefs.savePref(SPKey.REFRESH_TOKEN, value!!)
        }

    suspend fun getLoginPages(): LoginPageInfoResponse? {
        return withContext(Dispatchers.IO) {
            try {
                val response = loginApi.getLoginPage()

                if (response.isSuccessful) {
                    response.body()
                } else {
                    Log.e("TokenManager", "response status: ${response.code()}\nresponse message: ${response.errorBody()?.string()}")
                    null
                }
            } catch (e: Exception) {
                Log.e("TokenManager", e.message.toString())
                null
            }
        }
    }
    suspend fun getLoginTokens(platformType: String, code: String, state: String): TokenResponse? {
        return withContext(Dispatchers.IO) {
            try {
                val response = loginApi.getLoginTokens(platformType, code, state)

                if (response.isSuccessful) {
                    accessToken = response.body()!!.accessToken
                    refreshToken = response.body()!!.refreshToken
                    response.body()!!
                } else {
                    Log.e("TokenManager", "login response status: ${response.code()}\nlogin response message: ${response.errorBody()?.string()}")
                    null
                }
            } catch (e: Exception) {
                Log.e("TokenManager", e.message.toString())
                null
            }
        }
    }

    suspend fun getRegisterTokens(request: RegisterRequest): TokenResponse? {
        return withContext(Dispatchers.IO) {
            try {
                val token = "Bearer $accessToken"
                val response = loginApi.getRegisterTokens(token, request)

                if (response.isSuccessful) {
                    accessToken = response.body()!!.accessToken
                    refreshToken = response.body()!!.refreshToken
                    response.body()!!
                } else {
                    Log.e("TokenManager", "register response status: ${response.code()}\nregister response message: ${response.errorBody()?.string()}")
                    null
                }
            } catch (e: Exception) {
                Log.e("TokenManager", e.message.toString())
                null
            }
        }
    }

    suspend fun reissueTokens(): ReissueTokenResponse? {
        return withContext(Dispatchers.IO) {
            try {
                val token = "Bearer $refreshToken"
                val response = loginApi.reissueTokens(token)

                if (response.isSuccessful) {
                    accessToken = response.body()!!.accessToken
                    refreshToken = response.body()!!.refreshToken
                    response.body()
                } else {
                    Log.e("TokenManager", "response status: ${response.code()}\nresponse message:${response.errorBody()!!.string()}")
                    null
                }
            } catch (e: Exception) {
                Log.e("TokenManager", e.message.toString())
                null
            }
        }
    }
}