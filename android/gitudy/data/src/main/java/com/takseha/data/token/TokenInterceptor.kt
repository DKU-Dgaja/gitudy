package com.takseha.data.token

import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class TokenInterceptor(
    private val tokenManager: TokenManager,
    private val navigationHandler: NavigationHandler
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()

        // 토큰을 헤더에 추가
        request = addTokenToRequest(request, tokenManager.accessToken)

        // 원래 요청을 실행
        val response = chain.proceed(request)

        if (response.code() == 401) {
            synchronized(this) {
                response.close()  // 이전 응답 닫기

                val newToken = runBlocking { tokenManager.reissueTokens() }
                if (newToken != null) {
                    request = addTokenToRequest(request, newToken.accessToken)
                    return chain.proceed(request)
                } else {
                    // todo: 토큰 자동 재발급에 실패했을 때, 로그인 유효기간이 만료되어 로그인 화면으로 이동합니다. 같은 팝업창 띄우고, 해당 팝업창 종료 시 로그인 화면으로 이동하도록 하는 로직 구현
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