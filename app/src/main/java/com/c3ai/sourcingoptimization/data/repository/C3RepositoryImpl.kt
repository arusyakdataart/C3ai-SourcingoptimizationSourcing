package com.c3ai.sourcingoptimization.data.repository

import com.c3ai.sourcingoptimization.data.remote.C3Api
import com.c3ai.sourcingoptimization.domain.model.SearchItem
import com.c3ai.sourcingoptimization.domain.repository.C3Repository
import javax.inject.Inject

class C3RepositoryImpl @Inject constructor(private val api: C3Api) : C3Repository {

    override suspend fun getSearchResults(query: String): List<SearchItem> {
        return api.getSearchResults(query)
    }
}