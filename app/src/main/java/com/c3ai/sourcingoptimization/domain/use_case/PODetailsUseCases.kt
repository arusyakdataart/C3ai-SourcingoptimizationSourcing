package com.c3ai.sourcingoptimization.domain.use_case

import com.c3ai.sourcingoptimization.data.C3Result
import com.c3ai.sourcingoptimization.data.repository.C3Repository
import com.c3ai.sourcingoptimization.domain.model.PurchaseOrder

class GetPODetails(private val repository: C3Repository) {

    suspend operator fun invoke(orderId: String): C3Result<PurchaseOrder.Order> {
        return repository.getPODetails(orderId)
    }
}

class GetPOLines(private val repository: C3Repository) {

    suspend operator fun invoke(orderId: String): C3Result<List<PurchaseOrder.Line>> {
        return repository.getPOLines(orderId)
    }
}

data class PODetailsUseCases(
    val getPODetails: GetPODetails,
    val getPoLines: GetPOLines
)