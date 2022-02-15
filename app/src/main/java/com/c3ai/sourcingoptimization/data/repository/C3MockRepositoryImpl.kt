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

    override suspend fun getSuppliedItems(supplierId: String): C3Result<List<C3Item>> =
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

    override suspend fun getItemDetailsSuppliers(itemId: String): C3Result<List<C3Vendor>> {
        TODO("Not yet implemented")
    }

    override suspend fun getItemVendorRelation(
        itemId: String,
        supplierIds: List<String>
    ): C3Result<List<ItemVendorRelation>> {
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
}