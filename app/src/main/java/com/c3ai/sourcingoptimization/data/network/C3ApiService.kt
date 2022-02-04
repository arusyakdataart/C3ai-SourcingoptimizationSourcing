package com.c3ai.sourcingoptimization.data.network

import com.c3ai.sourcingoptimization.data.network.converters.C3SpecJsonSerializer
import com.c3ai.sourcingoptimization.data.network.requests.*
import com.c3ai.sourcingoptimization.domain.model.C3Item
import com.c3ai.sourcingoptimization.domain.model.POLine
import com.c3ai.sourcingoptimization.domain.model.SearchItem
import com.c3ai.sourcingoptimization.domain.model.C3Supplier
import com.c3ai.sourcingoptimization.utilities.MAIN_API_URL
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.text.DateFormat

/**
 * Api service for the application with internal building of client[OkHttpClient].
 * It provides all server request int the application
 */
interface C3ApiService {

    @GET("${MAIN_API_URL}search/{searchQuery}")
    suspend fun search(@Path("searchQuery") query: String): List<SearchItem>

    @Headers("Accept: application/json")
    @POST("${MAIN_API_URL}Item?action=fetch")
    suspend fun getItemDetails(@Body request: ItemDetailsParameters): C3Item

    @Headers("Accept: application/json")
    @POST("${MAIN_API_URL}PurchaseOrder?action=fetch")
    suspend fun getPOLines(@Body request: POLinesDetailsParameters): POLine

    @Headers("Accept: application/json")
    @POST("${MAIN_API_URL}PurchaseOrder?action=fetch")
    suspend fun getDetailedPO(@Body request: DetailedPOParameters): POLine

    @Headers("Accept: application/json")
    @POST("${MAIN_API_URL}Vendor?action=fetch")
    suspend fun getSupplierDetails(@Body request: SupplierDetailsParameters): C3Response<C3Supplier>

    @Headers("Accept: application/json")
    @POST("${MAIN_API_URL}Vendor?action=fetch")
    suspend fun getSuppliersByItem(@Body request: SuppliersByItemParameters): List<C3Supplier>

    @Headers("Accept: application/json")
    @POST("${MAIN_API_URL}Vendor?action=fetch")
    suspend fun getSuppliedItems(@Body request: SuppliedItemsParameters): List<C3Item>

    companion object {

        fun create(okHttpClient: OkHttpClient): C3ApiService {
            val gson = GsonBuilder()
                .setDateFormat(DateFormat.LONG)
                .registerTypeAdapter(C3Spec::class.java, C3SpecJsonSerializer())
                .create()

            return Retrofit.Builder()
                .baseUrl(MAIN_API_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
                .create(C3ApiService::class.java)
        }
    }
}