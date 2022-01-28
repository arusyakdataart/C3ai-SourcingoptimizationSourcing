package com.c3ai.sourcingoptimization.authorization.di

import com.c3ai.sourcingoptimization.authorization.domain.C3AuthService
import com.c3ai.sourcingoptimization.authorization.domain.use_case.AuthUseCases
import com.c3ai.sourcingoptimization.authorization.domain.use_case.SignIn
import com.c3ai.sourcingoptimization.data.network.C3Session
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

/**
 * Module for auth flow. It provides the service to authorize on server[C3AuthService] and
 * structural components.
 */
@Module
@InstallIn(ViewModelComponent::class)
object C3AuthModule {

    @Provides
    fun provideC3AuthService(session: C3Session): C3AuthService {
        return C3AuthService.create(session)
    }

    @Provides
    fun provideAuthUseCases(service: C3AuthService): AuthUseCases {
        return AuthUseCases(
            SignIn(service)
        )
    }
}