package com.takseha.data

import android.content.Context
import android.util.Log
import com.takseha.data.api.gitudy.auth.GitudyAuthApi
import com.takseha.data.dto.auth.login.LoginTokenInfo
import com.takseha.data.dto.auth.register.ReissueTokenInfo
import com.takseha.data.repository.auth.GitudyAuthRepository
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
    private val loginApi = retrofit.create(GitudyAuthApi::class.java)


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

    suspend fun getLoginTokens(platformType: String, code: String, state: String): LoginTokenInfo? {
        return withContext(Dispatchers.IO) {
            try {
                val response = loginApi.getLoginTokens(platformType, code, state)

                if (response.isSuccessful) {
                    accessToken = response.body()!!.tokenInfo.accessToken
                    refreshToken = response.body()!!.tokenInfo.refreshToken
                    response.body()!!.tokenInfo
                } else {
                    Log.e("TokenManager", "response status: ${response.code()}\nresponse message: ${response.message()}")
                    null
                }
            } catch (e: Exception) {
                null
            }
        }
    }

    suspend fun reissueTokens(): ReissueTokenInfo? {
        return withContext(Dispatchers.IO) {
            try {
                val bearerToken = "Bearer $accessToken $refreshToken"
                val response = loginApi.reissueTokens(bearerToken)

                if (response.isSuccessful) {
                    accessToken = response.body()!!.tokenInfo.accessToken
                    refreshToken = response.body()!!.tokenInfo.refreshToken
                    response.body()!!.tokenInfo
                } else {
                    Log.e("TokenManager", "response status: ${response.code()}\nresponse message: ${response.message()}")
                    null
                }
            } catch (e: Exception) {
                null
            }
        }
    }
}