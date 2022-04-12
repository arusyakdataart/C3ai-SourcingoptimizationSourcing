package com.c3ai.sourcingoptimization.presentation.common

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.c3ai.sourcingoptimization.data.C3Result
import com.c3ai.sourcingoptimization.utilities.PAGINATED_RESPONSE_LIMIT

class C3PagingSource<T : Any>(
    private val loadData: suspend (Int, Int) -> C3Result<List<T>>,
) : PagingSource<Int, T>() {

    override fun getRefreshKey(state: PagingState<Int, T>): Int? {
        return null
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, T> {
        val nextPage = params.key ?: 1
        val offset = (nextPage - 1) * PAGINATED_RESPONSE_LIMIT
        val response = loadData(PAGINATED_RESPONSE_LIMIT, offset)
        return when (response) {
            is C3Result.Success -> {
                LoadResult.Page(
                    data = response.data,
                    prevKey = if (nextPage > 1) nextPage - 1 else null,
                    nextKey = if (response.data.isNotEmpty()) nextPage.plus(1) else null
                )
            }
            is C3Result.Error -> LoadResult.Error(response.exception)
        }
    }
}