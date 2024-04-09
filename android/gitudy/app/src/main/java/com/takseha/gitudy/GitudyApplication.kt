package com.takseha.gitudy

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class GitudyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
    }
}