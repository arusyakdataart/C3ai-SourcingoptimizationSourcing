package com.c3ai.sourcingoptimization.presentation.common.search

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.c3ai.sourcingoptimization.domain.model.RecentSearchItem
import com.c3ai.sourcingoptimization.domain.model.SearchItem
import com.c3ai.sourcingoptimization.domain.settings.C3AppSettingsProvider
import com.c3ai.sourcingoptimization.domain.use_case.SearchUseCases
import com.c3ai.sourcingoptimization.presentation.common.C3PagingSource
import com.c3ai.sourcingoptimization.utilities.BIG_PAGINATED_RESPONSE_LIMIT
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * ViewModel class which provides all necessary functionality for searching.
 * */
@HiltViewModel
class SearchViewModel @Inject constructor(
    settingsProvider: C3AppSettingsProvider,
    val useCases: SearchUseCases
) : ViewModel() {

    private var state: SearchState<SearchItem, RecentSearchItem>? = null

    private val pagingSource = C3PagingSource { limit, offset ->
        state?.let { useCases.search(it.query.text, it.selectedFilters.toList(), offset) }
    }

    @Composable
    fun rememberState(): SearchState<SearchItem, RecentSearchItem> {
        if (state == null) {
            state = rememberSaveableSearchState(initialResults = emptyList())
        }
        return state as SearchState<SearchItem, RecentSearchItem>
    }


    fun search() {
        state?.searchResultsFlow = Pager(PagingConfig(BIG_PAGINATED_RESPONSE_LIMIT)) {
            pagingSource
        }.flow
    }

}