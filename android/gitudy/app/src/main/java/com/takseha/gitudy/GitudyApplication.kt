package com.takseha.gitudy

import android.app.Application
import com.takseha.common.util.SharedPreferences
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class GitudyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
    }
}