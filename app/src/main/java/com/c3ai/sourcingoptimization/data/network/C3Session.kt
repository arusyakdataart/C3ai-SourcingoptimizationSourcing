package com.c3ai.sourcingoptimization.data.network

import android.content.Context

class C3Session private constructor(
    private val context: Context,
    var login: String = "",
    var password: String = "",
    var token: String?
) {

    fun isValid(): Boolean {
        return !token.isNullOrEmpty()
    }

    fun save() {
        val prefs = context.getSharedPreferences(SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE)
        prefs.edit().putString(AUTHORIZATION_KEY, token).apply()
    }

    fun clear() {
        val prefs = context.getSharedPreferences(SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE)
        prefs.edit().clear().apply()
    }

    companion object {
        private const val SHARED_PREFERENCES_KEY = "SESSION_HOLDER"
        private const val AUTHORIZATION_KEY = "Cookie"

        fun create(context: Context): C3Session {
            val prefs = context.getSharedPreferences(SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE)
            val token = prefs.getString(AUTHORIZATION_KEY, null)
            return C3Session(context = context, token = token)
        }
    }
}