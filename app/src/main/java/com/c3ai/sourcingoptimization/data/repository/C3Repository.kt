package com.c3ai.sourcingoptimization.data.repository

import com.c3ai.sourcingoptimization.data.C3Result
import com.c3ai.sourcingoptimization.domain.model.*
import com.c3ai.sourcingoptimization.utilities.PAGINATED_RESPONSE_LIMIT

/**
 * General repository interface describes all methods for data that is needed in the application.
 * */
interface C3Repository {

    suspend fun search(query: String, filters: List<Int>): C3Result<List<SearchItem>>

    suspend fun getItemDetails(itemId: String): C3Result<C3Item>

    suspend fun getSupplierDetails(supplierId: String): C3Result<C3Vendor>

    suspend fun getSuppliers(
        itemId: String,
        order: String,
        limit: Int = PAGINATED_RESPONSE_LIMIT,
        offset: Int = 0
    ): C3Result<List<C3Vendor>>

    suspend fun getPODetails(orderId: String): C3Result<PurchaseOrder.Order>

    suspend fun getPOLines(
        itemId: String?,
        orderId: String? = null,
        order: String,
        limit: Int = PAGINATED_RESPONSE_LIMIT,
        offset: Int = 0
    ): C3Result<List<PurchaseOrder.Line>>

    suspend fun getPOsForVendor(
        vendorId: String,
        order: String
    ): C3Result<List<PurchaseOrder.Order>>

    suspend fun getSupplierContacts(id: String): C3Result<C3VendorContact>

    suspend fun getBuyerContacts(id: String): C3Result<C3BuyerContact>

    suspend fun getSuppliedItems(vendorId: String, order: String): C3Result<List<C3Item>>

    suspend fun getEvalMetricsForPOLineQty(
        itemId: String, expressions: List<String>, startDate: String,
        endDate: String, interval: String
    ): C3Result<OpenClosedPOLineQtyItem>

    suspend fun getEvalMetricsForSavingsOpportunity(
        itemId: String, expressions: List<String>, startDate: String,
        endDate: String, interval: String
    ): C3Result<SavingsOpportunityItem>

    suspend fun getItemDetailsSuppliers(
        itemId: String,
        limit: Int = PAGINATED_RESPONSE_LIMIT
    ): C3Result<List<C3Vendor>>

    suspend fun getItemVendorRelation(itemId: String, supplierIds: List<String>):
            C3Result<List<ItemRelation>>

    suspend fun getItemVendorRelationMetrics(
        ids: List<String>,
        expressions: List<String>,
        startDate: String,
        endDate: String,
        interval: String
    ): C3Result<ItemVendorRelationMetrics>

    suspend fun getMarketPriceIndexes(): C3Result<List<MarketPriceIndex>>

    suspend fun getItemMarketPriceIndexRelation(
        itemId: String,
        indexId: String
    ): C3Result<List<ItemRelation>>

    suspend fun getItemMarketPriceIndexRelationMetrics(
        ids: List<String>,
        expressions: List<String>,
        startDate: String,
        endDate: String,
        interval: String
    ): C3Result<ItemMarketPriceIndexRelationMetrics>

    suspend fun getAlertsForUser(order: String): C3Result<List<Alert>>

    suspend fun getAlertsFeedbacks(
        alertIds: List<String>,
        userId: String
    ): C3Result<List<AlertFeedback>>

    suspend fun updateAlert(
        alertIds: List<String>,
        userId: String,
        statusType: String,
        statusValue: Boolean?
    )
}