package com.c3ai.sourcingoptimization.data.network

import android.content.Context

class C3Session private constructor(
    var login: String = "",
    var password: String = "",
    var token: String
) {

    companion object {
        private const val SHARED_PREFERENCES_KEY = "SESSION_HOLDER"
        private const val AUTHORIZATION_KEY = "Cookie"
        private const val TOKEN_DEFAULT = "jwt"

        fun create(context: Context): C3Session {

            val prefs = context.getSharedPreferences(SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE)

            val token = prefs.getString(AUTHORIZATION_KEY, TOKEN_DEFAULT)!!
            return C3Session(token = token)
        }

        fun clear(context: Context) {
            val prefs = context.getSharedPreferences(SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE)
            prefs.edit().clear().apply()
        }
    }
}