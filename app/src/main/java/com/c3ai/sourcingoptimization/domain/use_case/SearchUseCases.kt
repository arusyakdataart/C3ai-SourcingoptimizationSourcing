package com.c3ai.sourcingoptimization.domain.use_case

import com.c3ai.sourcingoptimization.data.C3Result
import com.c3ai.sourcingoptimization.data.repository.C3Repository
import com.c3ai.sourcingoptimization.domain.model.SearchItem
import com.c3ai.sourcingoptimization.utilities.BIG_PAGINATED_RESPONSE_LIMIT
import com.c3ai.sourcingoptimization.utilities.PAGINATED_RESPONSE_LIMIT

class Search(private val repository: C3Repository) {

    suspend operator fun invoke(
        query: String,
        filters: List<Int>?,
        offset: Int
    ): C3Result<List<SearchItem>> {
        return repository.search(query, filters = filters, offset = offset)
    }
}

data class SearchUseCases(
    val search: Search
)