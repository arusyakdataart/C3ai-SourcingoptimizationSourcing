package com.c3ai.sourcingoptimization.data.repository

import com.c3ai.sourcingoptimization.data.Result
import com.c3ai.sourcingoptimization.data.network.C3ApiService
import com.c3ai.sourcingoptimization.data.network.requests.ItemDetailsParameters
import com.c3ai.sourcingoptimization.domain.model.C3Item
import com.c3ai.sourcingoptimization.domain.model.SearchItem
import javax.inject.Inject

class C3RepositoryImpl @Inject constructor(private val api: C3ApiService) : C3Repository {

    override suspend fun search(query: String): Result<List<SearchItem>> = Result.on {
        api.search(query)
    }

    override suspend fun getItemDetails(itemId: String): Result<C3Item> = Result.on {
        api.getItemDetails(ItemDetailsParameters(itemId = itemId))
    }
}