package com.c3ai.sourcingoptimization.di

import android.content.Context
import com.c3ai.sourcingoptimization.data.network.C3ApiService
import com.c3ai.sourcingoptimization.data.repository.C3Repository
import com.c3ai.sourcingoptimization.data.repository.C3RepositoryImpl
import com.c3ai.sourcingoptimization.domain.settings.C3AppSettingsProvider
import com.c3ai.sourcingoptimization.domain.settings.C3AppSettingsProviderImpl
import com.c3ai.sourcingoptimization.domain.use_case.*
import com.c3ai.sourcingoptimization.utilities.AuthInterceptorOkHttpClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
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
    @Singleton
    fun provideC3SettingProvider(@ApplicationContext context: Context): C3AppSettingsProvider {
        return C3AppSettingsProviderImpl(context)
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
            getSupplierDetails = GetSupplierDetails(repository),
            getPOsForSupplier = GetPOsForSupplier(repository),
            getSuppliedItems = GetSuppliedItems(repository)
        )
    }

    @Provides
    fun providePODetailsUseCases(repository: C3Repository): PODetailsUseCases {
        return PODetailsUseCases(
            getPODetails = GetPODetails(repository),
            getPoLines = GetPOLines(repository),
            getSupplierContacts = GetSupplierContacts(repository),
            getBuyerContacts = GetBuyerContacts(repository)
        )
    }

    @Provides
    fun provideEditSuppliersUseCases(repository: C3Repository): EditSuppliersUseCases {
        return EditSuppliersUseCases(
            getSuppliers = GetSuppliersForItem(repository)
        )
    }

    @Provides
    fun provideEditIndexUseCases(repository: C3Repository): EditIndexUseCases {
        return EditIndexUseCases(
            getIndexes = GetMarketPriceIndexes(repository)
        )
    }
}