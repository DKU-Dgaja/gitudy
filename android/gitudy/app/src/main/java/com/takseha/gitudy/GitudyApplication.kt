package com.takseha.gitudy

import android.app.Application
import com.takseha.data.api.gitudy.RetrofitInstance
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class GitudyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        RetrofitInstance.init(this)
    }
}