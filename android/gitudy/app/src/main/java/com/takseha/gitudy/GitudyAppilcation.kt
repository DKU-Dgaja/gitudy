package com.takseha.gitudy

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class GitudyAppilcation : Application() {
    override fun onCreate() {
        super.onCreate()
    }
}