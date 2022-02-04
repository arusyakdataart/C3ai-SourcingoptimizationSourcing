package com.c3ai.sourcingoptimization.di

import com.c3ai.sourcingoptimization.data.network.C3ApiService
import com.c3ai.sourcingoptimization.data.repository.C3Repository
import com.c3ai.sourcingoptimization.data.repository.C3RepositoryImpl
import com.c3ai.sourcingoptimization.domain.use_case.GetSupplierDetails
import com.c3ai.sourcingoptimization.domain.use_case.Search
import com.c3ai.sourcingoptimization.domain.use_case.SearchUseCases
import com.c3ai.sourcingoptimization.domain.use_case.SuppliersDetailsUseCases
import com.c3ai.sourcingoptimization.utilities.AuthInterceptorOkHttpClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import javax.inject.Singleton

/**
 * A module for the application, provides most major services and structural components
 * of the application. The module[AppModule] defines the creation and behavior of some entities.
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideC3ApiService(@AuthInterceptorOkHttpClient okHttpClient: OkHttpClient): C3ApiService {
        return C3ApiService.create(okHttpClient)
    }

    @Provides
    @Singleton
    fun provideC3Repository(api: C3ApiService): C3Repository {
        return C3RepositoryImpl(api)
    }

    @Provides
    fun provideSearchUseCases(repository: C3Repository): SearchUseCases {
        return SearchUseCases(
            Search(repository)
        )
    }

    @Provides
    fun provideSuppliersDetailsUseCases(repository: C3Repository): SuppliersDetailsUseCases {
        return SuppliersDetailsUseCases(
            GetSupplierDetails(repository)
        )
    }
}