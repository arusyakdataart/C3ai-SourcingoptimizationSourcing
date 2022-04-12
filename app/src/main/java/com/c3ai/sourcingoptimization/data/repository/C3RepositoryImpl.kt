package com.c3ai.sourcingoptimization.data.repository

import com.c3ai.sourcingoptimization.data.C3Result
import com.c3ai.sourcingoptimization.data.network.C3ApiService
import com.c3ai.sourcingoptimization.data.network.requests.*
import com.c3ai.sourcingoptimization.domain.model.*
import com.c3ai.sourcingoptimization.utilities.PAGINATED_RESPONSE_LIMIT
import javax.inject.Inject

/**
 * General repository implementation provides all necessary data.
 * */
class C3RepositoryImpl @Inject constructor(private val api: C3ApiService) : C3Repository {

    override suspend fun search(query: String): C3Result<List<SearchItem>> = C3Result.on {
        api.search(query)
    }

    override suspend fun getItemDetails(itemId: String): C3Result<C3Item> = C3Result.on {
        api.getItemDetails(ItemDetailsParameters(itemId = itemId)).objs?.get(0) ?: C3Item("")
    }

    override suspend fun getSupplierDetails(supplierId: String): C3Result<C3Vendor> = C3Result.on {
        api.getSupplierDetails(SupplierDetailsParameters(supplierId)).objs?.get(0) ?: C3Vendor("")
    }

    override suspend fun getSuppliers(
        itemId: String,
        order: String,
        limit: Int,
        offset: Int
    ): C3Result<List<C3Vendor>> =
        C3Result.on {
            api.getSuppliers(SuppliersParameters(itemId, order, limit, offset)).objs ?: emptyList()
        }

    override suspend fun getPODetails(orderId: String): C3Result<PurchaseOrder.Order> =
        C3Result.on {
            api.getDetailedPO(DetailedPOParameters(orderId)).objs?.get(0) ?: PurchaseOrder.Order("")
        }

    override suspend fun getPOLines(
        itemId: String?,
        orderId: String?,
        order: String,
        limit: Int,
        offset: Int
    ): C3Result<List<PurchaseOrder.Line>> =
        C3Result.on {
            api.getPOLines(POLinesDetailsParameters(itemId, orderId, order, limit, offset)).objs ?: emptyList()
        }

    override suspend fun getPOsForVendor(
        vendorId: String,
        order: String
    ): C3Result<List<PurchaseOrder.Order>> =
        C3Result.on {
            api.getPOsForVendor(VendorPOParameters(vendorId, order)).objs ?: emptyList()
        }

    override suspend fun getSupplierContacts(id: String): C3Result<C3VendorContact> = C3Result.on {
        api.getSupplierContacts(SupplierContactsParameters(id)).objs?.get(0) ?: C3VendorContact("")
    }

    override suspend fun getBuyerContacts(id: String): C3Result<C3BuyerContact> = C3Result.on {
        api.getBuyerContacts(BuyerContactsParameters(id)).objs?.get(0) ?: C3BuyerContact("")
    }

    override suspend fun getSuppliedItems(vendorId: String, order: String): C3Result<List<C3Item>> =
        C3Result.on {
            api.getSuppliedItems(SuppliedItemParameters(vendorId, order)).objs ?: emptyList()
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

    override suspend fun getItemDetailsSuppliers(itemId: String, limit: Int): C3Result<List<C3Vendor>> =
        C3Result.on {
            api.getSuppliers(
                SuppliersParameters(
                    itemId = itemId,
                    limit = limit,
                    order = "descending(spend.value)"
                )
            ).objs ?: emptyList()
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
            ).objs ?: emptyList()
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

    override suspend fun getMarketPriceIndexes(): C3Result<List<MarketPriceIndex>> = C3Result.on {
        api.getMarketPriceIndexes().objs ?: emptyList()
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
        ).objs ?: emptyList()
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

    override suspend fun getAlertsForUser(order: String): C3Result<List<Alert>> = C3Result.on {
        api.getAlertsForUser(AlertsParameters(order)).objs ?: emptyList()
    }

    override suspend fun getAlertsFeedbacks(
        alertIds: List<String>,
        userId: String
    ): C3Result<List<AlertFeedback>> = C3Result.on {
        api.getAlertsFeedbacks(AlertFeedbackParameters(alertIds, userId)).objs ?: emptyList()
    }

    override suspend fun updateAlert(
        alertIds: List<String>,
        userId: String,
        statusType: String,
        statusValue: Boolean?
    ) = api.updateAlert(UpdateAlertParameters(alertIds, userId, statusType, statusValue))
}