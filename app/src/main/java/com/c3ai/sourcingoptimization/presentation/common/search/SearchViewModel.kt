package com.c3ai.sourcingoptimization.presentation.common.search

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.c3ai.sourcingoptimization.R
import com.c3ai.sourcingoptimization.domain.model.RecentSearchItem
import com.c3ai.sourcingoptimization.domain.model.SearchItem
import com.c3ai.sourcingoptimization.domain.settings.C3AppSettingsProvider
import com.c3ai.sourcingoptimization.domain.use_case.SearchUseCases
import com.c3ai.sourcingoptimization.presentation.common.C3PagingSource
import com.c3ai.sourcingoptimization.utilities.BIG_PAGINATED_RESPONSE_LIMIT
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
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
        useCases.search(state!!.query.text, state!!.selectedFilters.toList(), offset)
    }

    private var searchResultsFlow: Flow<PagingData<SearchItem>>? by mutableStateOf(null)

    /**
     * Results of a search action.
     */
    private var searchResults: LazyPagingItems<SearchItem>? = null

    @Suppress("SENSELESS_COMPARISON")
    @Composable
    fun rememberState(): SearchState<SearchItem, RecentSearchItem> {
        state = rememberSaveableSearchState(
            initialResults = emptyList(),
            filters = stringArrayResource(R.array.searchFilters).toList(),
        )
        return state as SearchState<SearchItem, RecentSearchItem>
    }

    fun search() {
        searchResults = null
        searchResultsFlow = Pager(PagingConfig(BIG_PAGINATED_RESPONSE_LIMIT)) {
            pagingSource
        }.flow
    }

    @Composable
    fun collectAsLazyPagingItems(): LazyPagingItems<SearchItem>? {
        if (searchResults == null) {
            searchResults = searchResultsFlow?.collectAsLazyPagingItems()
        }
        return searchResults
    }

    fun clear() {
        state?.query = TextFieldValue("")
        searchResultsFlow = null
        searchResults = null
    }

    fun close() {
        clear()
        state?.opened = false
    }
}