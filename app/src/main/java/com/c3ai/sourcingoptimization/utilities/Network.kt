package com.c3ai.sourcingoptimization.utilities

import javax.inject.Qualifier

/**
 * An annotation for network client[OkHttpClient] with authorization.
 * @see C3SessionProvider
 * @see NetworkModule
 * */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AuthInterceptorOkHttpClient

/**
 * An annotation for network client[OkHttpClient] without authorization.
 * @see C3SessionProvider
 * @see NetworkModule
 * */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DefaultInterceptorOkHttpClient