package com.c3ai.sourcingoptimization.data.network

import com.c3ai.sourcingoptimization.data.network.requests.ItemDetailsParameters
import com.c3ai.sourcingoptimization.domain.model.C3Item
import com.c3ai.sourcingoptimization.domain.model.SearchItem
import com.c3ai.sourcingoptimization.utilities.MAIN_API_URL
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface C3ApiService {

    @GET("${MAIN_API_URL}search/{searchQuery}")
    suspend fun search(@Path("searchQuery") query: String): List<SearchItem>

    @POST("${MAIN_API_URL}Item?action=fetch")
    suspend fun getItemDetails(@Body request: ItemDetailsParameters): C3Item

    companion object {

        fun create(okHttpClient: OkHttpClient): C3ApiService {
            val gson = GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS").create()

            return Retrofit.Builder()
                .baseUrl(MAIN_API_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
                .create(C3ApiService::class.java)
        }
    }
}