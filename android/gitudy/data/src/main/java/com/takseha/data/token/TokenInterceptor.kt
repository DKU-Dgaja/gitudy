package com.takseha.data.token

import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class TokenInterceptor(
    private val tokenManager: TokenManager
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        request = addTokenToRequest(request, tokenManager.accessToken)
        val response: Response

        try {
            response = chain.proceed(request)
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }

        if (response.code() == 401 || response.code() == 403) {
            synchronized(this) {
                val isReissueSucceed = runBlocking { tokenManager.reissueTokens() }
                if (isReissueSucceed) {
                    request = addTokenToRequest(request, tokenManager.accessToken)
                    response.close()
                    return chain.proceed(request)
                } else {
                    return response
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