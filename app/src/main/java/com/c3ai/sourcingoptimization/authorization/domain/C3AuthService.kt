package com.c3ai.sourcingoptimization.authorization.domain

import com.c3ai.sourcingoptimization.authorization.domain.C3AuthService.Companion.create
import com.c3ai.sourcingoptimization.data.network.C3Session
import com.c3ai.sourcingoptimization.utilities.API_DOMAIN
import com.c3ai.sourcingoptimization.utilities.SCHEMA
import com.facebook.stetho.okhttp3.StethoInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.POST

/**
 * Api service for authorization, has configuration method[create]
 * with internal building of client[OkHttpClient]
 */
interface C3AuthService {

    @POST("/auth/1/token")
    suspend fun authorize()

    companion object {

        fun create(session: C3Session): C3AuthService {
            val logger =
                HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }

            val client = OkHttpClient.Builder()
                .addInterceptor(logger)
                .addInterceptor(AuthInterceptor(session))
                .addNetworkInterceptor(StethoInterceptor())
                .build()

            return Retrofit.Builder()
                .baseUrl("$SCHEMA$API_DOMAIN")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(C3AuthService::class.java)
        }
    }
}