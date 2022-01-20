package com.c3ai.sourcingoptimization.data.network

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class C3SessionProvider constructor(private val session: C3Session): Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request: Request = chain.request().newBuilder()
            .addHeader("Content-type", "application/json")
            .addHeader("Accept", "application/json")
            .addHeader("Cookie", session.token)
            .build()

        return chain.proceed(request)
    }
}