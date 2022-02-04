package com.c3ai.sourcingoptimization.domain.use_case

import com.c3ai.sourcingoptimization.data.C3Result
import com.c3ai.sourcingoptimization.data.repository.C3Repository
import com.c3ai.sourcingoptimization.domain.model.SearchItem

class Search(private val repository: C3Repository) {

    suspend operator fun invoke(query: String): C3Result<List<SearchItem>> {
        return repository.search(query)
    }
}

data class SearchUseCases(
    val search: Search
)