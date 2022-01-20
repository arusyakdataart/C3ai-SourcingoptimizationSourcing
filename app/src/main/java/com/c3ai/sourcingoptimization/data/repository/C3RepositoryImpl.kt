package com.c3ai.sourcingoptimization.data.repository

import com.c3ai.sourcingoptimization.data.network.C3ApiService
import com.c3ai.sourcingoptimization.domain.model.SearchItem
import javax.inject.Inject

class C3RepositoryImpl @Inject constructor(private val api: C3ApiService) : C3Repository {

    override suspend fun search(query: String): List<SearchItem> {
        return api.search(query)
    }
}