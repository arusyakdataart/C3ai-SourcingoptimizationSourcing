package com.c3ai.sourcingoptimization.domain.use_case

import com.c3ai.sourcingoptimization.data.network.C3ApiService
import com.c3ai.sourcingoptimization.data.network.requests.SuppliedItemsParameters
import com.c3ai.sourcingoptimization.domain.model.C3Item

class GetSuppliedItems(private val service: C3ApiService) {

    suspend operator fun invoke(supplierId: String): List<C3Item> {
        return service.getSuppliedItems(SuppliedItemsParameters(supplierId))
    }
}

data class SuppliersDetailsUseCases(
    val getSuppliedItems: GetSuppliedItems
)