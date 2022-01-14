package com.c3ai.sourcingoptimization.di

import com.c3ai.sourcingoptimization.common.Constants
import com.c3ai.sourcingoptimization.feature_home.data.remote.C3Api
import com.c3ai.sourcingoptimization.feature_home.data.repository.C3RepositoryImpl
import com.c3ai.sourcingoptimization.feature_home.domain.repository.C3Repository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideC3Api() : C3Api {
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(C3Api::class.java)
    }

    @Provides
    @Singleton
    fun provideC3Repository(api: C3Api) : C3Repository {
        return C3RepositoryImpl(api)
    }
}