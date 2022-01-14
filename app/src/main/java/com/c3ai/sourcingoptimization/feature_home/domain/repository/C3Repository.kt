package com.c3ai.sourcingoptimization.feature_home.domain.repository

import com.c3ai.sourcingoptimization.feature_home.domain.model.SearchItem

interface C3Repository {

    suspend fun getSearchResults(query: String): List<SearchItem>
}