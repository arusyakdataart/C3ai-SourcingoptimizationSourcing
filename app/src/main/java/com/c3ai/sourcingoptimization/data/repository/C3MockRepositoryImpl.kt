package com.c3ai.sourcingoptimization.data.repository

import com.c3ai.sourcingoptimization.data.C3Result
import com.c3ai.sourcingoptimization.data.mock.fake
import com.c3ai.sourcingoptimization.domain.model.C3Item
import com.c3ai.sourcingoptimization.domain.model.C3Vendor
import com.c3ai.sourcingoptimization.domain.model.PurchaseOrder
import com.c3ai.sourcingoptimization.domain.model.SearchItem

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
}