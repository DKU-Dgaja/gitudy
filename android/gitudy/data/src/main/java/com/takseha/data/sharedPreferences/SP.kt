package com.takseha.data.sharedPreferences

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import javax.crypto.AEADBadTagException

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
            Log.e("SP", "Error 발생: ${e.message}. SP 초기화")
            clearPref(context)

            // SP 재생성
            EncryptedSharedPreferences.create(
                context.applicationContext,
                "encrypted_shared_prefs",
                MasterKey.Builder(context.applicationContext, MasterKey.DEFAULT_MASTER_KEY_ALIAS)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build(),
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        }
    }

    private fun clearPref(context: Context) {
        context.getSharedPreferences("encrypted_shared_prefs", Context.MODE_PRIVATE)
            .edit().clear().apply()
    }

    fun savePref(spKey: String, spValue: String) {
        prefs.edit().putString(spKey, spValue).apply()
    }
    fun loadPref(spKey: String, defValue: String): String {
        return prefs.getString(spKey, defValue) ?: defValue
    }
}

