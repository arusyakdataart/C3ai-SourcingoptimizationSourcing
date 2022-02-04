package com.c3ai.sourcingoptimization.domain.use_case

import com.c3ai.sourcingoptimization.data.C3Result
import com.c3ai.sourcingoptimization.data.repository.C3Repository
import com.c3ai.sourcingoptimization.domain.model.C3Supplier

class GetSupplierDetails(private val repository: C3Repository) {

    suspend operator fun invoke(supplierId: String): C3Result<C3Supplier> {
        return repository.getSupplierDetails(supplierId)
    }
}

data class SuppliersDetailsUseCases(
    val getSupplierDetails: GetSupplierDetails
)