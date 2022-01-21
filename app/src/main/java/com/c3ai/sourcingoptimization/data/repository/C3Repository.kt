package com.c3ai.sourcingoptimization.data.repository

import com.c3ai.sourcingoptimization.domain.model.SearchItem

interface C3Repository {

    suspend fun search(query: String): List<SearchItem>
}