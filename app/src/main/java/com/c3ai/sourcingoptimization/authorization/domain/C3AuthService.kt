package com.c3ai.sourcingoptimization.authorization.domain

import com.c3ai.sourcingoptimization.data.network.C3Session
import com.c3ai.sourcingoptimization.utilities.API_DOMAIN
import com.c3ai.sourcingoptimization.utilities.SCHEMA
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.POST

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