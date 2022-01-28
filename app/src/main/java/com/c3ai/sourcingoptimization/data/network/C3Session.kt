package com.c3ai.sourcingoptimization.data.network

import android.content.Context

/**
 * Session class for the application, show current state of authorization.
 * Currently it works with cookies.
 * */
class C3Session private constructor(
    private val context: Context,
    var login: String = "",
    var password: String = "",
    var cookie: String?,
) {

    fun isValid(): Boolean {
        return !cookie.isNullOrEmpty()
    }

    fun save() {
        val prefs = context.getSharedPreferences(SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE)
        prefs.edit()
            .putString(COOKIE_KEY, cookie)
            .apply()
    }

    fun clear() {
        val prefs = context.getSharedPreferences(SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE)
        prefs.edit().clear().apply()
    }

    companion object {
        private const val SHARED_PREFERENCES_KEY = "SESSION_HOLDER"
        const val COOKIE_KEY = "Cookie"

        fun create(context: Context): C3Session {
            val prefs = context.getSharedPreferences(SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE)
            val cookie = prefs.getString(COOKIE_KEY, null)
            return C3Session(context = context, cookie = cookie)
        }
    }
}