package com.c3ai.sourcingoptimization.domain.use_case

import com.c3ai.sourcingoptimization.data.C3Result
import com.c3ai.sourcingoptimization.data.repository.C3Repository
import com.c3ai.sourcingoptimization.domain.model.PurchaseOrder

class GetPOLinesByItem(private val repository: C3Repository) {

    suspend operator fun invoke(itemId: String, order: String
    ): C3Result<List<PurchaseOrder.Line>> {
        return repository.getPOLines(itemId = itemId, orderId = null, order = order)
    }
}

data class ItemDetailsUseCases(
    val getPOLines: GetPOLinesByItem,
)