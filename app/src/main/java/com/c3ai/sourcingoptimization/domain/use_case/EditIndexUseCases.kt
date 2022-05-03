package com.c3ai.sourcingoptimization.domain.use_case

import com.c3ai.sourcingoptimization.data.C3Result
import com.c3ai.sourcingoptimization.data.repository.C3Repository
import com.c3ai.sourcingoptimization.domain.model.MarketPriceIndex

class GetMarketPriceIndexes(private val repository: C3Repository) {

    suspend operator fun invoke(page: Int): C3Result<List<MarketPriceIndex>> {
        return repository.getMarketPriceIndexes(page)
    }
}

data class EditIndexUseCases(
    val getIndexes: GetMarketPriceIndexes
)