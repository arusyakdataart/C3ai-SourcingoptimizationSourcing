package com.c3ai.sourcingoptimization.feature_home.data.remote

import com.c3ai.sourcingoptimization.feature_home.domain.model.SearchItem
import retrofit2.http.GET
import retrofit2.http.Path

interface C3Api {

    @GET("api/1/search/{searchQuery}")
    suspend fun getSearchResults(@Path("searchQuery") query: String): List<SearchItem>
}