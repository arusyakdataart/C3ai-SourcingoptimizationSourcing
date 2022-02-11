package com.c3ai.sourcingoptimization.data.repository

import com.c3ai.sourcingoptimization.data.Result
import com.c3ai.sourcingoptimization.domain.model.*

/**
 * General repository interface describes all methods for data that is needed in the application.
 * */
interface C3Repository {

    suspend fun search(query: String): Result<List<SearchItem>>

    suspend fun getItemDetails(itemId: String): Result<C3Items>

    suspend fun getEvalMetricsForPOLineQty(itemId: String, expressions: List<String>, startDate: String,
                                           endDate: String, interval: String): Result<OpenClosedPOLineQtyItem>

    suspend fun getEvalMetricsForSavingsOpportunity(itemId: String, expressions: List<String>, startDate: String,
                                                    endDate: String, interval: String): Result<SavingsOpportunityItem>

    suspend fun getSuppliers(itemId: String): Result<Vendors>

}