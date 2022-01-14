package com.c3ai.sourcingoptimization.feature_home.data.repository

import com.c3ai.sourcingoptimization.feature_home.data.remote.C3Api
import com.c3ai.sourcingoptimization.feature_home.domain.model.SearchItem
import com.c3ai.sourcingoptimization.feature_home.domain.repository.C3Repository
import javax.inject.Inject

class C3RepositoryImpl @Inject constructor(private val api: C3Api) : C3Repository {

    override suspend fun getSearchResults(query: String): List<SearchItem> {
        return api.getSearchResults(query)
    }
}