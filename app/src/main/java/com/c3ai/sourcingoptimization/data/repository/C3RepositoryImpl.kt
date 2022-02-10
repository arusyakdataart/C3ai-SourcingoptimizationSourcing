package com.c3ai.sourcingoptimization.data.repository

import com.c3ai.sourcingoptimization.data.C3Result
import com.c3ai.sourcingoptimization.data.network.C3ApiService
import com.c3ai.sourcingoptimization.data.network.requests.ItemDetailsParameters
import com.c3ai.sourcingoptimization.data.network.requests.SuppliedItemsParameters
import com.c3ai.sourcingoptimization.data.network.requests.SupplierDetailsParameters
import com.c3ai.sourcingoptimization.domain.model.C3Item
import com.c3ai.sourcingoptimization.domain.model.C3Vendor
import com.c3ai.sourcingoptimization.domain.model.PurchaseOrder
import com.c3ai.sourcingoptimization.domain.model.SearchItem
import javax.inject.Inject

/**
 * General repository implementation provides all necessary data.
 * */
class C3RepositoryImpl @Inject constructor(private val api: C3ApiService) : C3Repository {

    override suspend fun search(query: String): C3Result<List<SearchItem>> = C3Result.on {
        api.search(query)
    }

    override suspend fun getItemDetails(itemId: String): C3Result<C3Item> = C3Result.on {
        api.getItemDetails(ItemDetailsParameters(itemId = itemId))
    }

    override suspend fun getSupplierDetails(supplierId: String): C3Result<C3Vendor> = C3Result.on {
        api.getSupplierDetails(SupplierDetailsParameters(supplierId)).objs[0]
    }

    override suspend fun getPODetails(orderId: String): C3Result<PurchaseOrder.Order> {
        TODO("Not yet implemented")
    }

    override suspend fun getSuppliedItems(supplierId: String): C3Result<List<C3Item>> =
        C3Result.on {
            api.getSuppliedItems(SuppliedItemsParameters(supplierId))
        }
}