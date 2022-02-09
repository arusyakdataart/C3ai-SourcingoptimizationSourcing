package com.c3ai.sourcingoptimization.data.repository

import com.c3ai.sourcingoptimization.data.Result
import com.c3ai.sourcingoptimization.data.network.C3ApiService
import com.c3ai.sourcingoptimization.data.network.requests.EvalMetricsParameters
import com.c3ai.sourcingoptimization.data.network.requests.ItemDetailsParameters
import com.c3ai.sourcingoptimization.domain.model.C3Items
import com.c3ai.sourcingoptimization.domain.model.OpenClosedPOLineQtyItem
import com.c3ai.sourcingoptimization.domain.model.SavingsOpportunityItem
import com.c3ai.sourcingoptimization.domain.model.SearchItem
import javax.inject.Inject

/**
 * General repository implementation provides all necessary data.
 * */
class C3RepositoryImpl @Inject constructor(private val api: C3ApiService) : C3Repository {

    override suspend fun search(query: String): Result<List<SearchItem>> = Result.on {
        api.search(query)
    }

    override suspend fun getItemDetails(itemId: String): Result<C3Items> = Result.on {
        api.getItemDetails(ItemDetailsParameters(itemId = itemId))
    }

    override suspend fun getEvalMetricsForPOLineQty(
        itemId: String,
        expressions: List<String>,
        startDate: String,
        endDate: String,
        interval: String
    ): Result<OpenClosedPOLineQtyItem> = Result.on {
        api.getEvalMetricsForPOLineQty(EvalMetricsParameters(itemId = itemId, expressions = expressions,
            startDate = startDate, endDate = endDate, interval = interval))
    }

    override suspend fun getEvalMetricsForSavingsOpportunity(
        itemId: String,
        expressions: List<String>,
        startDate: String,
        endDate: String,
        interval: String
    ): Result<SavingsOpportunityItem> = Result.on {
        api.getEvalMetricsForSavingOpportunity(
            EvalMetricsParameters(
                itemId = itemId, expressions = expressions,
                startDate = startDate, endDate = endDate, interval = interval
            )
        )
    }
}