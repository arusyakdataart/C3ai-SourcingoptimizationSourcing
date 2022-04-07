package com.c3ai.sourcingoptimization.domain.use_case

import com.c3ai.sourcingoptimization.data.C3Result
import com.c3ai.sourcingoptimization.data.repository.C3Repository
import com.c3ai.sourcingoptimization.domain.model.C3Vendor

class GetSuppliersForItem(private val repository: C3Repository) {

    suspend operator fun invoke(itemId: String): C3Result<List<C3Vendor>> {
        return repository.getItemDetailsSuppliers(itemId)
    }
}

data class EditSuppliersUseCases(
    val getSuppliers: GetSuppliersForItem
)