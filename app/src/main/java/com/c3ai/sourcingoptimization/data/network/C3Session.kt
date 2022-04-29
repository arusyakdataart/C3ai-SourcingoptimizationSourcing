package com.c3ai.sourcingoptimization.data.network

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys

/**
 * Session class for the application, show current state of authorization.
 * Currently it works with cookies.
 * */
class C3Session private constructor(
    private val sharedPreferences: SharedPreferences,
    var userId: String,
    var login: String,
    var password: String = "",
    var cookie: String?,
) {

    fun isValid(): Boolean {
        return userId.isNotEmpty() && !cookie.isNullOrEmpty()
    }

    fun save() {
        sharedPreferences.edit()
            .putString(USER_ID_KEY, userId)
            .putString(LOGIN_KEY, login)
            .putString(COOKIE_KEY, cookie)
            .apply()
    }

    fun clear() {
        cookie = null
        sharedPreferences.edit()
            .clear()
            .apply()
    }

    companion object {
        private const val SHARED_PREFERENCES_KEY = "c3_session_storage"
        const val USER_ID_KEY = "user_id"
        const val LOGIN_KEY = "Login"
        const val COOKIE_KEY = "Cookie"

        @RequiresApi(Build.VERSION_CODES.M)
        private val keyGenParameterSpec = MasterKeys.AES256_GCM_SPEC

        @RequiresApi(Build.VERSION_CODES.M)
        private val mainKeyAlias = MasterKeys.getOrCreate(keyGenParameterSpec)

        private fun getSharedPreferences(context: Context) =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                EncryptedSharedPreferences.create(
                    SHARED_PREFERENCES_KEY,
                    mainKeyAlias,
                    context,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
                )
            else context.getSharedPreferences(SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE)

        fun create(context: Context): C3Session {
            val prefs = getSharedPreferences(context)
            val userId = prefs.getString(USER_ID_KEY, null)
            val login = prefs.getString(LOGIN_KEY, null)
            val cookie = prefs.getString(COOKIE_KEY, null)
            return C3Session(
                sharedPreferences = prefs,
                userId = userId ?: "",
                login = login ?: "unknown",
                cookie = cookie
            )
        }
    }
}