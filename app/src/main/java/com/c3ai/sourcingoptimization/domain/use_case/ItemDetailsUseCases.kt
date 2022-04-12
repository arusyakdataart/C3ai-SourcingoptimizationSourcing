package com.c3ai.sourcingoptimization.domain.use_case

import com.c3ai.sourcingoptimization.data.C3Result
import com.c3ai.sourcingoptimization.data.repository.C3Repository
import com.c3ai.sourcingoptimization.domain.model.*
import com.c3ai.sourcingoptimization.utilities.PAGINATED_RESPONSE_LIMIT

class GetItemDetails(private val repository: C3Repository) {

    suspend operator fun invoke(itemId: String): C3Result<C3Item> {
        return repository.getItemDetails(itemId = itemId)
    }

}

class GetEvalMetricsForPOLineQty(private val repository: C3Repository) {

    suspend operator fun invoke(
        itemId: String,
        expressions: List<String>,
        startDate: String,
        endDate: String,
        interval: String
    ): C3Result<OpenClosedPOLineQtyItem> {
        return repository.getEvalMetricsForPOLineQty(
            itemId,
            expressions,
            startDate,
            endDate,
            interval
        )
    }

}

class GetEvalMetricsForSavingsOpportunity(private val repository: C3Repository) {

    suspend operator fun invoke(
        itemId: String,
        expressions: List<String>,
        startDate: String,
        endDate: String,
        interval: String
    ): C3Result<SavingsOpportunityItem> {
        return repository.getEvalMetricsForSavingsOpportunity(
            itemId,
            expressions,
            startDate,
            endDate,
            interval
        )
    }

}

class GetItemDetailsSuppliers(private val repository: C3Repository) {

    suspend operator fun invoke(itemId: String, limit: Int = PAGINATED_RESPONSE_LIMIT): C3Result<List<C3Vendor>> {
        return repository.getItemDetailsSuppliers(itemId = itemId, limit = limit)
    }

}

class GetMarketPriceIndex(private val repository: C3Repository) {

    suspend operator fun invoke(): C3Result<List<MarketPriceIndex>> {
        return repository.getMarketPriceIndexes()
    }

}

class GetVendorRelationMetrics(private val repository: C3Repository) {

    suspend operator fun invoke(
        itemId: String,
        supplierIds: List<String>,
        expressions: List<String>,
        startDate: String,
        endDate: String,
        interval: String
    ): C3Result<Map<String, List<Double>>> {
        val vendorRelationMetrics = mutableMapOf<String, List<Double>>()
        if (supplierIds.isNotEmpty()) {
            val itemVendorRelations =
                repository.getItemVendorRelation(itemId, supplierIds = supplierIds)
            when (itemVendorRelations) {
                is C3Result.Success -> {
                    val itemVendorRelationMetrics = repository.getItemVendorRelationMetrics(
                        itemVendorRelations.data.map { it.id },
                        expressions,
                        startDate,
                        endDate,
                        interval
                    )
                    when (itemVendorRelationMetrics) {
                        is C3Result.Success -> {
                            itemVendorRelations.data.forEach { relation ->
                                vendorRelationMetrics[relation.to.id] =
                                    itemVendorRelationMetrics.data.result[relation.id]
                                        ?.OrderLineValue?.data ?: listOf()
                            }
                        }
                        is C3Result.Error -> {
                            return C3Result.Error(itemVendorRelationMetrics.exception)
                        }
                    }
                }
                is C3Result.Error -> {
                    return C3Result.Error(itemVendorRelations.exception)
                }
            }
        }
        return C3Result.Success(vendorRelationMetrics)
    }

}

class GetMarketPriceIndexRelationMetrics(private val repository: C3Repository) {

    suspend operator fun invoke(
        itemId: String,
        indexId: String,
        ids: List<String>,
        expressions: List<String>,
        startDate: String,
        endDate: String,
        interval: String
    ): C3Result<ItemMarketPriceIndexRelationMetrics> {
        val itemMarketPriceIndexRelation =
            repository.getItemMarketPriceIndexRelation(itemId, indexId)
        when (itemMarketPriceIndexRelation) {
            is C3Result.Success -> {
                val marketPriceIndexRelationMetrics =
                    repository.getItemMarketPriceIndexRelationMetrics(
                        ids,
                        expressions,
                        startDate,
                        endDate,
                        interval
                    )
                return when (marketPriceIndexRelationMetrics) {
                    is C3Result.Success -> {
                        return marketPriceIndexRelationMetrics
                    }
                    is C3Result.Error -> {
                        C3Result.Error(marketPriceIndexRelationMetrics.exception)
                    }
                }
            }
            is C3Result.Error -> {
                return C3Result.Error(itemMarketPriceIndexRelation.exception)
            }
        }
    }

}

class GetPOLinesByItem(private val repository: C3Repository) {

    suspend operator fun invoke(
        itemId: String,
        order: String,
        limit: Int,
        offset: Int,
    ): C3Result<List<PurchaseOrder.Line>> {
        return repository.getPOLines(itemId, orderId = null, order = order, limit, offset)
    }

}

class GetSuppliers(private val repository: C3Repository) {

    suspend operator fun invoke(
        itemId: String,
        order: String,
        limit: Int,
        offset: Int,
    ): C3Result<List<C3Vendor>> {
        return repository.getSuppliers(itemId, order, limit, offset)
    }
}

data class ItemDetailsUseCases(
    val getItemDetails: GetItemDetails,
    val getEvalMetricsForPOLineQty: GetEvalMetricsForPOLineQty,
    val getEvalMetricsForSavingsOpportunity: GetEvalMetricsForSavingsOpportunity,
    val getItemDetailsSuppliers: GetItemDetailsSuppliers,
    val getMarketPriceIndex: GetMarketPriceIndex,
    val getVendorRelationMetrics: GetVendorRelationMetrics,
    val getMarketPriceIndexRelationMetrics: GetMarketPriceIndexRelationMetrics,
    val getPOLines: GetPOLinesByItem,
    val getSuppliers: GetSuppliers,
    val getSupplierContacts: GetSupplierContacts
)