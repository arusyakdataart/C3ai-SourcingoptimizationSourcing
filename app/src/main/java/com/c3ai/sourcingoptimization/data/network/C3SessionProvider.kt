package com.c3ai.sourcingoptimization.data.network

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class C3SessionProvider constructor(private val session: C3Session): Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        Log.e("C3SessionProvider", session.cookie ?: "")
        val request: Request = chain.request().newBuilder()
            .addHeader(C3Session.COOKIE_KEY, session.cookie ?: "")
            .build()

        return chain.proceed(request)
    }
}