package com.c3ai.sourcingoptimization.di

import android.content.Context
import com.c3ai.sourcingoptimization.data.network.C3Session
import com.c3ai.sourcingoptimization.data.network.C3SessionProvider
import com.c3ai.sourcingoptimization.utilities.AuthInterceptorOkHttpClient
import com.c3ai.sourcingoptimization.utilities.DefaultInterceptorOkHttpClient
import com.facebook.stetho.okhttp3.StethoInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import javax.inject.Singleton

/**
 * A module for the application, specializes on an entities that is needed
 * for server interaction. The module[NetworkModule] defines the creation
 * of current session[C3Session], network clients.
 * @see OkHttpClient
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @AuthInterceptorOkHttpClient
    @Provides
    fun provideAuthInterceptorOkHttpClient(session: C3Session): OkHttpClient {
        val logger = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
        return OkHttpClient.Builder()
            .addInterceptor(logger)
            .addInterceptor(C3SessionProvider(session))
            .addNetworkInterceptor(StethoInterceptor())
            .build()
    }

    @DefaultInterceptorOkHttpClient
    @Provides
    fun provideDefaultInterceptorOkHttpClient(): OkHttpClient {
        val logger = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
        return OkHttpClient.Builder()
            .addInterceptor(logger)
            .addNetworkInterceptor(StethoInterceptor())
            .build()
    }

    @Singleton
    @Provides
    fun provideC3Session(@ApplicationContext context: Context): C3Session {
        return C3Session.create(context)
    }

}