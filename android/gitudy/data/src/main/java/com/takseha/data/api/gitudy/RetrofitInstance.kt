package com.takseha.data.api.gitudy

import android.content.Context
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.takseha.data.BuildConfig
import com.takseha.data.token.NavigationHandler
import com.takseha.data.token.TokenInterceptor
import com.takseha.data.token.TokenManager
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private lateinit var tokenManager: TokenManager
    private lateinit var navigationHandler: NavigationHandler

    fun init(context: Context, navigationHandler: NavigationHandler) {
        tokenManager = TokenManager(context)
        this.navigationHandler = navigationHandler
    }

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(TokenInterceptor(tokenManager, navigationHandler))
            .build()
    }

    private val gson: Gson by lazy {
        GsonBuilder()
            .setLenient()
            .create()
    }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BuildConfig.GITUDY_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    fun getInstance(): Retrofit {
        return retrofit
    }
}