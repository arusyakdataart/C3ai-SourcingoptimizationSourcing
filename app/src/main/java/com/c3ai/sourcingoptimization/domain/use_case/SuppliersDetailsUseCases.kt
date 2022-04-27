package com.c3ai.sourcingoptimization.domain.use_case

import com.c3ai.sourcingoptimization.data.C3Result
import com.c3ai.sourcingoptimization.data.repository.C3Repository
import com.c3ai.sourcingoptimization.domain.model.C3Item
import com.c3ai.sourcingoptimization.domain.model.C3Vendor
import com.c3ai.sourcingoptimization.domain.model.PurchaseOrder

class GetSupplierDetails(private val repository: C3Repository) {

    suspend operator fun invoke(supplierId: String): C3Result<C3Vendor> {
        return repository.getSupplierDetails(supplierId)
    }
}

class GetPOsForSupplier(private val repository: C3Repository) {

    suspend operator fun invoke(supplierId: String, order: String, page: Int): C3Result<List<PurchaseOrder.Order>> {
        return repository.getPOsForVendor(supplierId, order, page)
    }
}

class GetSuppliedItems(private val repository: C3Repository) {

    suspend operator fun invoke(supplierId: String, order: String, page: Int): C3Result<List<C3Item>> {
        return repository.getSuppliedItems(supplierId, order, page)
    }
}

data class SuppliersDetailsUseCases(
    val getSupplierDetails: GetSupplierDetails,
    val getPOsForSupplier: GetPOsForSupplier,
    val getSuppliedItems: GetSuppliedItems
)