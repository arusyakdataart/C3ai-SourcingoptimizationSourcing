package com.c3ai.sourcingoptimization.data.network

import com.c3ai.sourcingoptimization.data.network.converters.C3SearchItemJsonDeserializer
import com.c3ai.sourcingoptimization.data.network.converters.C3SpecJsonSerializer
import com.c3ai.sourcingoptimization.data.network.requests.*
import com.c3ai.sourcingoptimization.domain.model.*
import com.c3ai.sourcingoptimization.utilities.MAIN_API_URL
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

/**
 * Api service for the application with internal building of client[OkHttpClient].
 * It provides all server request int the application
 */
interface C3ApiService {

    @Headers("Accept: application/json")
    @POST("${MAIN_API_URL}SoSearchHelper?action=unifiedSearchFetch")
    suspend fun search(@Body request: SearchParameters): C3Response<SearchItem>

    @Headers("Accept: application/json")
    @POST("${MAIN_API_URL}Item?action=fetch")
    suspend fun getItemDetails(@Body request: ItemDetailsParameters): C3Response<C3Item>

    @Headers("Accept: application/json")
    @POST("${MAIN_API_URL}Item?action=evalMetrics")
    suspend fun getEvalMetricsForPOLineQty(@Body request: EvalMetricsParameters): OpenClosedPOLineQtyItem

    @Headers("Accept: application/json")
    @POST("${MAIN_API_URL}Item?action=evalMetrics")
    suspend fun getEvalMetricsForSavingOpportunity(@Body request: EvalMetricsParameters): SavingsOpportunityItem

    @Headers("Accept: application/json")
    @POST("${MAIN_API_URL}Vendor?action=fetch")
    suspend fun getSuppliers(@Body request: SuppliersParameters): C3Response<C3Vendor>

    @Headers("Accept: application/json")
    @POST("${MAIN_API_URL}PurchaseOrderLine?action=fetch")
    suspend fun getPOLines(@Body request: POLinesDetailsParameters): C3Response<PurchaseOrder.Line>

    @Headers("Accept: application/json")
    @POST("${MAIN_API_URL}PurchaseOrder?action=fetch")
    suspend fun getDetailedPO(@Body request: DetailedPOParameters): C3Response<PurchaseOrder.Order>

    @Headers("Accept: application/json")
    @POST("${MAIN_API_URL}PurchaseOrder?action=fetch")
    suspend fun getPOsForVendor(@Body request: VendorPOParameters): C3Response<PurchaseOrder.Order>

    @Headers("Accept: application/json")
    @POST("${MAIN_API_URL}Item?action=fetch")
    suspend fun getSuppliedItems(@Body request: SuppliedItemParameters): C3Response<C3Item>

    @Headers("Accept: application/json")
    @POST("${MAIN_API_URL}Vendor?action=fetch")
    suspend fun getSupplierContacts(@Body request: SupplierContactsParameters): C3Response<C3VendorContact>

    @Headers("Accept: application/json")
    @POST("${MAIN_API_URL}Buyer?action=fetch")
    suspend fun getBuyerContacts(@Body request: BuyerContactsParameters): C3Response<C3BuyerContact>

    @Headers("Accept: application/json")
    @POST("${MAIN_API_URL}Vendor?action=fetch")
    suspend fun getSupplierDetails(@Body request: SupplierDetailsParameters): C3Response<C3Vendor>

    @Headers("Accept: application/json")
    @POST("${MAIN_API_URL}Vendor?action=fetch")
    suspend fun getSuppliersByItem(@Body request: SuppliersByItemParameters): List<C3Vendor>

    @Headers("Accept: application/json")
    @POST("${MAIN_API_URL}ItemVendorRelation?action=fetch")
    suspend fun getItemVendorRelation(@Body request: ItemVendorRelationParameters): C3Response<ItemRelation>

    @Headers("Accept: application/json")
    @POST("${MAIN_API_URL}ItemVendorRelation?action=evalMetrics")
    suspend fun getItemVendorRelationMetrics(@Body request: EvalMetricsParameters): ItemVendorRelationMetrics

    @Headers("Accept: application/json")
    @POST("${MAIN_API_URL}MarketPriceIndex?action=fetch")
    suspend fun getMarketPriceIndexes(): C3Response<MarketPriceIndex>

    @Headers("Accept: application/json")
    @POST("${MAIN_API_URL}ItemMarketPriceIndexRelation?action=fetch")
    suspend fun getItemMarketPriceIndexRelation(@Body request: ItemMarketPriceIndexRelationParameters): C3Response<ItemRelation>

    @Headers("Accept: application/json")
    @POST("${MAIN_API_URL}ItemMarketPriceIndexRelation?action=evalMetrics")
    suspend fun getItemMarketPriceIndexRelationMetrics(@Body request: EvalMetricsParameters): ItemMarketPriceIndexRelationMetrics

    @Headers("Accept: application/json")
    @POST("${MAIN_API_URL}SoAlertHelper?action=fetchForCurrentUser")
    suspend fun getAlertsForUser(@Body request: AlertsParameters): C3Response<Alert>

    @Headers("Accept: application/json")
    @POST("${MAIN_API_URL}SoAlertFeedbackHistory?action=fetch")
    suspend fun getAlertsFeedbacks(@Body request: AlertFeedbackParameters): C3Response<AlertFeedback>

    @Headers("Accept: application/json")
    @POST("${MAIN_API_URL}SoAlert?action=updateStatusHistoryForUser")
    suspend fun updateAlert(@Body request: UpdateAlertParameters)

    companion object {

        fun create(okHttpClient: OkHttpClient): C3ApiService {
            val gson = GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                .registerTypeAdapter(C3Spec::class.java, C3SpecJsonSerializer())
                .registerTypeAdapter(SearchItem::class.java, C3SearchItemJsonDeserializer())
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