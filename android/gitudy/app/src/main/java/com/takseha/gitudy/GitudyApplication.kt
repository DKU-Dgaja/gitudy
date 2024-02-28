package com.takseha.gitudy

import android.app.Application
import com.takseha.common.util.SharedPreferences
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class GitudyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
    }
}