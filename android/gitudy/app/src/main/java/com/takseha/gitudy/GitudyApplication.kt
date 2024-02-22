package com.takseha.gitudy

import android.app.Application
import com.takseha.common.util.SharedPreferences
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class GitudyApplication : Application() {
    companion object{
        lateinit var prefsManager : SharedPreferences
    }
    override fun onCreate() {
        prefsManager = SharedPreferences(applicationContext)
        super.onCreate()
    }
}