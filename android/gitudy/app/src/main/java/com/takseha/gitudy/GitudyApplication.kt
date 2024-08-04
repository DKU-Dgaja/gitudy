package com.takseha.gitudy

import android.app.Application
import com.takseha.data.api.gitudy.RetrofitInstance
import com.takseha.presentation.handler.LoginNavigationHandler

class GitudyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        RetrofitInstance.init(this, LoginNavigationHandler(this))
    }
}