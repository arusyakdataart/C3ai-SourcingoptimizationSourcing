package com.c3ai.sourcingoptimization.data.repository

import com.c3ai.sourcingoptimization.data.Result
import com.c3ai.sourcingoptimization.domain.model.C3Item
import com.c3ai.sourcingoptimization.domain.model.SearchItem

/**
 * General repository interface describes all methods for data that is needed in the application.
 * */
interface C3Repository {

    suspend fun search(query: String): Result<List<SearchItem>>

    suspend fun getItemDetails(itemId: String): Result<C3Item>
}