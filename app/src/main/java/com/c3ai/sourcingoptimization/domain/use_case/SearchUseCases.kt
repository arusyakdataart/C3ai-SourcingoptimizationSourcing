package com.c3ai.sourcingoptimization.domain.use_case

import com.c3ai.sourcingoptimization.data.network.C3ApiService
import com.c3ai.sourcingoptimization.domain.model.SearchItem

class Search(private val service: C3ApiService) {

    suspend operator fun invoke(query: String): List<SearchItem> {
        return service.search(query)
    }
}

data class SearchUseCases(
    val search: Search
)