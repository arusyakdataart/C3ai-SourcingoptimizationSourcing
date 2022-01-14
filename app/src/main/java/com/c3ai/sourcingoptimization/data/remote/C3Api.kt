package com.c3ai.sourcingoptimization.data.remote

import com.c3ai.sourcingoptimization.domain.model.SearchItem
import retrofit2.http.GET
import retrofit2.http.Path

interface C3Api {

    @GET("api/1/search/{searchQuery}")
    suspend fun getSearchResults(@Path("searchQuery") query: String): List<SearchItem>
}