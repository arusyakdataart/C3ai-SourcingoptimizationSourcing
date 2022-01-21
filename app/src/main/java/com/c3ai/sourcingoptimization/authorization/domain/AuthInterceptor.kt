package com.c3ai.sourcingoptimization.authorization.domain

import android.util.Base64
import android.util.Log
import com.c3ai.sourcingoptimization.data.network.C3Session
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class AuthInterceptor constructor(private val session: C3Session): Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val base64String = Base64.encodeToString(
            "${session.login}:${session.password}".toByteArray(Charsets.UTF_8),
            Base64.NO_WRAP
        )
        Log.e("base64String", base64String)
        val request: Request = chain.request().newBuilder()
            .addHeader("Authorization", "Basic $base64String",)
            .build()

        return chain.proceed(request)
    }
}