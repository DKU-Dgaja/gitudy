package com.takseha.data.sharedPreferences

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class SP(context: Context){
    private val prefs: SharedPreferences by lazy {
        try {
            val masterKeyAlias = MasterKey
                .Builder(context.applicationContext, MasterKey.DEFAULT_MASTER_KEY_ALIAS)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()

            EncryptedSharedPreferences.create(
                context.applicationContext,
                "encrypted_shared_prefs",
                masterKeyAlias,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        } catch (e: Exception) {
            throw RuntimeException("Error initializing EncryptedSharedPreferences", e)
        }
    }

    fun savePref(spKey: String, spValue: String) {
        prefs.edit().putString(spKey, spValue).apply()
    }
    fun loadPref(spKey: String, defValue: String): String {
        return prefs.getString(spKey, defValue) ?: defValue
    }
}

