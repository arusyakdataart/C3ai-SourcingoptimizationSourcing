package com.c3ai.sourcingoptimization.domain.repository

import com.c3ai.sourcingoptimization.domain.model.SearchItem

interface C3Repository {

    suspend fun getSearchResults(query: String): List<SearchItem>
}