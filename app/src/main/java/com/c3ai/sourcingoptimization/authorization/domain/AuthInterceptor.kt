package com.c3ai.sourcingoptimization.authorization.domain

import android.util.Base64
import com.c3ai.sourcingoptimization.data.network.C3Session
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class AuthInterceptor constructor(private val session: C3Session) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val base64String = Base64.encodeToString(
            "${session.login}:${session.password}".toByteArray(Charsets.UTF_8),
            Base64.NO_WRAP
        )

        val request: Request = chain.request().newBuilder()
            .addHeader("Content-type", "application/json")
            .addHeader("Authorization", "Basic $base64String")
            .build()

        val response = chain.proceed(request)
        session.cookie = response.headers
            .filter { (key, _) -> key == "Set-Cookie" }
            .joinToString("; ") { (_, value) -> value.split(";")[0] }

        session.save()
        return response
    }
}