package com.c3ai.sourcingoptimization.data.repository

import com.c3ai.sourcingoptimization.data.C3Result
import com.c3ai.sourcingoptimization.domain.model.*

/**
 * General repository interface describes all methods for data that is needed in the application.
 * */
interface C3Repository {

    suspend fun search(query: String): C3Result<List<SearchItem>>

    suspend fun getItemDetails(itemId: String): C3Result<C3Item>

    suspend fun getSupplierDetails(supplierId: String): C3Result<C3Vendor>

    suspend fun getPODetails(orderId: String): C3Result<PurchaseOrder.Order>

    suspend fun getSuppliedItems(supplierId: String): C3Result<List<C3Item>>

    suspend fun getEvalMetricsForPOLineQty(
        itemId: String, expressions: List<String>, startDate: String,
        endDate: String, interval: String
    ): C3Result<OpenClosedPOLineQtyItem>

    suspend fun getEvalMetricsForSavingsOpportunity(
        itemId: String, expressions: List<String>, startDate: String,
        endDate: String, interval: String
    ): C3Result<SavingsOpportunityItem>

    suspend fun getItemDetailsSuppliers(itemId: String): C3Result<List<C3Vendor>>

}