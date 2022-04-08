package com.c3ai.sourcingoptimization.data.repository

import com.c3ai.sourcingoptimization.data.C3Result
import com.c3ai.sourcingoptimization.data.mock.fake
import com.c3ai.sourcingoptimization.domain.model.*

/**
 * Fake repository implementation provides all faked data.
 * */
class C3MockRepositoryImpl : C3Repository {

    override suspend fun search(query: String): C3Result<List<SearchItem>> = C3Result.on {
        emptyList()
    }

    override suspend fun getItemDetails(itemId: String): C3Result<C3Item> = C3Result.on {
        C3Item.fake()
    }

    override suspend fun getSupplierDetails(supplierId: String): C3Result<C3Vendor> = C3Result.on {
        C3Vendor.fake()
    }

    override suspend fun getPODetails(orderId: String): C3Result<PurchaseOrder.Order> =
        C3Result.on { PurchaseOrder.Order.fake() }

    override suspend fun getPOLines(
        itemId: String?,
        orderId: String?,
        order: String
    ): C3Result<List<PurchaseOrder.Line>> =
        C3Result.on {
            emptyList()
        }

    override suspend fun getPOsForVendor(
        vendorId: String,
        order: String
    ): C3Result<List<PurchaseOrder.Order>> =
        C3Result.on {
            emptyList()
        }

    override suspend fun getSupplierContacts(id: String): C3Result<C3VendorContact> = C3Result.on {
        C3VendorContact.fake()
    }

    override suspend fun getBuyerContacts(id: String): C3Result<C3BuyerContact> = C3Result.on {
        C3BuyerContact.fake()
    }

    override suspend fun getSuppliedItems(vendorId: String, order: String): C3Result<List<C3Item>> =
        C3Result.on {
            (1..20).map { C3Item.fake() }
        }

    override suspend fun getEvalMetricsForPOLineQty(
        itemId: String,
        expressions: List<String>,
        startDate: String,
        endDate: String,
        interval: String
    ): C3Result<OpenClosedPOLineQtyItem> {
        TODO("Not yet implemented")
    }

    override suspend fun getEvalMetricsForSavingsOpportunity(
        itemId: String,
        expressions: List<String>,
        startDate: String,
        endDate: String,
        interval: String
    ): C3Result<SavingsOpportunityItem> {
        TODO("Not yet implemented")
    }

    override suspend fun getItemDetailsSuppliers(itemId: String, limit: Int): C3Result<List<C3Vendor>> {
        TODO("Not yet implemented")
    }

    override suspend fun getItemVendorRelation(
        itemId: String,
        supplierIds: List<String>
    ): C3Result<List<ItemRelation>> {
        TODO("Not yet implemented")
    }

    override suspend fun getItemVendorRelationMetrics(
        ids: List<String>,
        expressions: List<String>,
        startDate: String,
        endDate: String,
        interval: String
    ): C3Result<ItemVendorRelationMetrics> {
        TODO("Not yet implemented")
    }

    override suspend fun getMarketPriceIndexes(): C3Result<List<MarketPriceIndex>> {
        TODO("Not yet implemented")
    }

    override suspend fun getItemMarketPriceIndexRelation(
        itemId: String,
        indexId: String
    ): C3Result<List<ItemRelation>> {
        TODO("Not yet implemented")
    }

    override suspend fun getItemMarketPriceIndexRelationMetrics(
        ids: List<String>,
        expressions: List<String>,
        startDate: String,
        endDate: String,
        interval: String
    ): C3Result<ItemMarketPriceIndexRelationMetrics> {
        TODO("Not yet implemented")
    }

    override suspend fun getAlertsForUser(order: String): C3Result<List<Alert>> {
        TODO("Not yet implemented")
    }

    override suspend fun getAlertsFeedbacks(
        alertIds: List<String>,
        userId: String
    ): C3Result<List<AlertFeedback>> {
        TODO("Not yet implemented")
    }

    override suspend fun updateAlert(
        alertIds: List<String>,
        userId: String,
        statusType: String,
        statusValue: Boolean?
    ) {
        TODO("Not yet implemented")
    }
}