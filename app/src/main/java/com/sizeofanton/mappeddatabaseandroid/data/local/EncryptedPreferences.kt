package com.sizeofanton.mappeddatabaseandroid.data.local

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys

class EncryptedPreferences(context: Context) {

    private val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
    private val sharedPreferences = EncryptedSharedPreferences.create(
        "AuthPreferences",
        masterKeyAlias,
        context,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun putString(key: String, s: String) {
        sharedPreferences.edit()
            .putString(key, s)
            .apply()
    }

    fun getString(key: String): String {
        return sharedPreferences.getString(key, "")!!
    }

    fun clearAuthData() {
        sharedPreferences.edit()
            .apply {
                putString("LOGIN", "")
                putString("PASSWORD", "")
            }
            .apply()
    }

    fun putFloat(key: String, f: Float) {
        sharedPreferences.edit()
            .putFloat(key, f)
            .apply()
    }

    fun getFloat(key: String): Float {
        return sharedPreferences.getFloat(key, 0.0f)
    }

    fun clearLocationData() {
        sharedPreferences.edit()
            .apply {
                putFloat("LATITUDE", 0.0f)
                putFloat("LONGITUDE", 0.0f)
            }
            .apply()
    }

    companion object {
        private var instance: EncryptedPreferences? = null

        fun getInstance(context: Context): EncryptedPreferences {
            if (instance == null) instance =
                EncryptedPreferences(
                    context
                )
            return instance!!
        }
    }
}
