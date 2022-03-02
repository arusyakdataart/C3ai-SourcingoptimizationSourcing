package com.c3ai.sourcingoptimization.data.repository

import com.c3ai.sourcingoptimization.data.C3Result
import com.c3ai.sourcingoptimization.data.network.C3ApiService
import com.c3ai.sourcingoptimization.data.network.requests.*
import com.c3ai.sourcingoptimization.domain.model.*
import javax.inject.Inject

/**
 * General repository implementation provides all necessary data.
 * */
class C3RepositoryImpl @Inject constructor(private val api: C3ApiService) : C3Repository {

    override suspend fun search(query: String): C3Result<List<SearchItem>> = C3Result.on {
        api.search(query)
    }

    override suspend fun getItemDetails(itemId: String): C3Result<C3Item> = C3Result.on {
        api.getItemDetails(ItemDetailsParameters(itemId = itemId)).objs[0]
    }

    override suspend fun getSupplierDetails(supplierId: String): C3Result<C3Vendor> = C3Result.on {
        api.getSupplierDetails(SupplierDetailsParameters(supplierId)).objs[0]
    }

    override suspend fun getPODetails(orderId: String): C3Result<PurchaseOrder.Order> =
        C3Result.on {
            api.getDetailedPO(DetailedPOParameters(orderId)).objs[0]
        }

    override suspend fun getPOLines(
        orderId: String,
        order: String
    ): C3Result<List<PurchaseOrder.Line>> =
        C3Result.on {
            api.getPOLines(POLinesDetailsParameters(orderId, order)).objs
        }

    override suspend fun getSuppliedItems(supplierId: String): C3Result<List<C3Item>> =
        C3Result.on {
            api.getSuppliedItems(SuppliedItemsParameters(supplierId))
        }

    override suspend fun getEvalMetricsForPOLineQty(
        itemId: String,
        expressions: List<String>,
        startDate: String,
        endDate: String,
        interval: String
    ): C3Result<OpenClosedPOLineQtyItem> = C3Result.on {
        api.getEvalMetricsForPOLineQty(
            EvalMetricsParameters(
                ids = listOf(itemId), expressions = expressions,
                startDate = startDate, endDate = endDate, interval = interval
            )
        )
    }

    override suspend fun getEvalMetricsForSavingsOpportunity(
        itemId: String,
        expressions: List<String>,
        startDate: String,
        endDate: String,
        interval: String
    ): C3Result<SavingsOpportunityItem> = C3Result.on {
        api.getEvalMetricsForSavingOpportunity(
            EvalMetricsParameters(
                ids = listOf(itemId), expressions = expressions,
                startDate = startDate, endDate = endDate, interval = interval
            )
        )
    }

    override suspend fun getItemDetailsSuppliers(itemId: String): C3Result<List<C3Vendor>> =
        C3Result.on {
            api.getSuppliers(
                SuppliersParameters(
                    itemId = itemId,
                    limit = 5,
                    order = "descending(spend.value)"
                )
            ).objs
        }

    override suspend fun getItemVendorRelation(
        itemId: String,
        supplierIds: List<String>
    ): C3Result<List<ItemRelation>> =
        C3Result.on {
            api.getItemVendorRelation(
                ItemVendorRelationParameters(
                    itemId = itemId,
                    supplierIds = supplierIds
                )
            ).objs
        }

    override suspend fun getItemVendorRelationMetrics(
        ids: List<String>,
        expressions: List<String>,
        startDate: String,
        endDate: String,
        interval: String
    ): C3Result<ItemVendorRelationMetrics> = C3Result.on {
        api.getItemVendorRelationMetrics(
            EvalMetricsParameters(
                ids = ids,
                expressions = expressions,
                startDate = startDate,
                endDate = endDate,
                interval = interval
            )
        )
    }

    override suspend fun getMarketPriceIndex(): C3Result<List<MarketPriceIndex>> = C3Result.on {
        api.getMarketPriceIndex().objs
    }

    override suspend fun getItemMarketPriceIndexRelation(
        itemId: String,
        indexId: String
    ): C3Result<List<ItemRelation>> = C3Result.on {
        api.getItemMarketPriceIndexRelation(
            ItemMarketPriceIndexRelationParameters(
                itemId = itemId,
                indexId = indexId
            )
        ).objs
    }

    override suspend fun getItemMarketPriceIndexRelationMetrics(
        ids: List<String>,
        expressions: List<String>,
        startDate: String,
        endDate: String,
        interval: String
    ): C3Result<ItemMarketPriceIndexRelationMetrics> = C3Result.on {
        api.getItemMarketPriceIndexRelationMetrics(
            EvalMetricsParameters(
                ids = ids,
                expressions = expressions,
                startDate = startDate,
                endDate = endDate,
                interval = interval
            )
        )
    }
}