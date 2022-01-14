package com.c3ai.sourcingoptimization.domain.use_case.get_search_results

import com.c3ai.sourcingoptimization.common.Resource
import com.c3ai.sourcingoptimization.domain.model.SearchItem
import com.c3ai.sourcingoptimization.domain.repository.C3Repository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class GetSearchResults @Inject constructor(private val repository: C3Repository) {

    operator fun invoke(searchQuery: String) : Flow<Resource<List<SearchItem>>> = flow {
        try {
            emit(Resource.Loading())
            val coin = repository.getSearchResults(searchQuery)
            emit(Resource.Success(coin))
        } catch (ex: HttpException) {
            emit(Resource.Error(ex.localizedMessage ?: "An unexpected error occurred"))
        } catch (ex: IOException) {
            emit(Resource.Error("Couldn't reach the server. Check your internet connection"))
        }
    }
}