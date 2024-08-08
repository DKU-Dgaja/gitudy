package com.takseha.data.token

import android.util.Log
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody

class TokenInterceptor(
    private val tokenManager: TokenManager
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        request = addTokenToRequest(request, tokenManager.accessToken)
        val response = chain.proceed(request)
        if (response.code() == 401) {
            synchronized(this) {
                val isReissueSucceed = runBlocking { tokenManager.reissueTokens() }
                if (isReissueSucceed) {
                    request = addTokenToRequest(request, tokenManager.accessToken)
                    return chain.proceed(request)
                }
            }
        }
        return response
    }

    private fun addTokenToRequest(request: Request, token: String?): Request {
        return request.newBuilder()
            .header("Authorization", "Bearer $token")
            .build()
    }
}