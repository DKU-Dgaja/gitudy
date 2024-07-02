package com.takseha.data

import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class TokenInterceptor(private val tokenManager: TokenManager) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()

        // 토큰을 헤더에 추가
        request = addTokenToRequest(request, "${tokenManager.accessToken} ${tokenManager.refreshToken}")

        // 원래 요청을 실행
        val response = chain.proceed(request)

        // 토큰이 만료된 경우
        if (response.code() == 401) {
            synchronized(this) {
                // 토큰 갱신 시도
                val newToken = runBlocking { tokenManager.reissueTokens() }
                if (newToken != null) {
                    // 갱신된 토큰으로 요청 재시도
                    request = addTokenToRequest(request, "${newToken.accessToken} ${newToken.refreshToken}")
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