package com.takseha.common.util

import android.content.Context

class SharedPreferences(context: Context){
    private val prefs = context.getSharedPreferences("shared_preferences", Context.MODE_PRIVATE)

    fun savePref(spKey: String, spValue: String) {
        prefs.edit().putString(spKey, spValue).apply()
    }
    fun loadPref(spKey: String, spValue: String): String {
        return prefs.getString(spKey, spValue).toString()
    }
}