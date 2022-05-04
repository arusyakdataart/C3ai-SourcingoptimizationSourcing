package com.c3ai.sourcingoptimization.presentation.common.search

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.text.input.TextFieldValue
import androidx.paging.PagingData
import com.c3ai.sourcingoptimization.data.network.converters.C3SearchItemJsonDeserializer
import com.c3ai.sourcingoptimization.domain.model.*
import com.c3ai.sourcingoptimization.presentation.common.search.SearchDisplay.*
import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import kotlinx.android.parcel.Parcelize
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import java.lang.reflect.Type

/**
 * Creates a [SearchState] that is remembered across compositions and cache itself.
 *
 * @param initialResults results that can be displayed before doing a search
 * @param suggestions chip or cards that can be suggested to user when Search composable is focused
 * but query is empty
 *
 */
@Composable
fun rememberSaveableSearchState(
    initialResults: List<SearchItem> = emptyList(),
    suggestions: List<RecentSearchItem> = emptyList(),
    filters: List<String> = emptyList(),
    timeoutMillis: Long = 0,
): SearchState<SearchItem, RecentSearchItem> {
    return rememberSaveable(saver = StateSaver()) {
        SearchState(
            initialResults = initialResults,
            suggestions = suggestions,
            filters = filters,
        )
    }.also { state ->
        LaunchedEffect(key1 = Unit) {

            snapshotFlow { state.query }
                .distinctUntilChanged()
                .filter { query: TextFieldValue ->
                    query.text.isNotEmpty() && !state.sameAsPreviousQuery()
                }
                .map { query: TextFieldValue ->
                    if (timeoutMillis > 0) {
                        state.searching = false
                    }
                    query
                }
                .debounce(timeoutMillis)
                .mapLatest {
                    state.searching = true
                    // This delay is for showing circular progress bar, it's optional
                    delay(300)
                }
                .collect {
                    state.searching = false
                }
        }
    }
}

/**
 * Creates a [SearchState] that is remembered across compositions.
 *
 * @param initialResults results that can be displayed before doing a search
 * @param suggestions chip or cards that can be suggested to user when Search composable is focused
 * but query is empty
 *
 */
@Composable
fun <R : Any, S> rememberSearchState(
    initialResults: List<R> = emptyList(),
    suggestions: List<S> = emptyList(),
    filters: List<String> = emptyList(),
): SearchState<R, S> {
    return remember {
        SearchState(
            initialResults = initialResults,
            suggestions = suggestions,
            filters = filters
        )
    }
}

/**
 * Creates a [SearchState] that is remembered across compositions. Uses [LaunchedEffect]
 *  and [snapshotFlow] to set states of search and return result or error state.
 *
 *  * First state when **Search Composable** is not focused is [SearchDisplay.InitialResults].
 *
 *  * When search gets focus state goes into [SearchDisplay.Suggestions] which some suggestion
 *  can be displayed to user.
 *
 *  * Immediately after user starts typing [SearchState.searchInProgress] sets to `true`
 *  to not get results while recomposition happens.
 *
 *  After [timeoutMillis] has passed [SearchState.searching] is set to `true`, progress icon
 *   can be displayed here.
 *
 * @param initialResults results that can be displayed before doing a search
 * @param suggestions chip or cards that can be suggested to user when Search composable is focused
 * but query is empty
 * @param timeoutMillis timeout before user finishes typing. After this
 * timeout [SearchState.searching]
 * is set to true.
 *
 */
@Composable
fun <R : Any, S> rememberSearchState(
    initialResults: List<R> = emptyList(),
    suggestions: List<S> = emptyList(),
    filters: List<String> = emptyList(),
    timeoutMillis: Long = 0,
): SearchState<R, S> {

    return remember {
        SearchState(
            initialResults = initialResults,
            suggestions = suggestions,
            filters = filters
        )
    }.also { state ->
        LaunchedEffect(key1 = Unit) {

            snapshotFlow { state.query }
                .distinctUntilChanged()
                .filter { query: TextFieldValue ->
                    query.text.isNotEmpty() && !state.sameAsPreviousQuery()
                }
                .map { query: TextFieldValue ->
                    if (timeoutMillis > 0) {
                        state.searching = false
                    }
                    query
                }
                .debounce(timeoutMillis)
                .mapLatest {
                    state.searching = true
                    // This delay is for showing circular progress bar, it's optional
                    delay(300)
                }
                .collect {
                    state.searching = false
                }
        }
    }
}

/**
 *  A state object that can be hoisted to control and observe scrolling for [SearchBar]
 *  or [SearchTextField].
 *
 * Create instance using [rememberSearchState].
 *
 * @param initialResults results that can be displayed before doing a search
 * @param suggestions chip or cards that can be suggested to user when Search composable is focused
 * but query is empty
 */
@Parcelize
class SearchState<R : Any, S> internal constructor(
    initialResults: List<R> = emptyList(),
    suggestions: List<S>,
    filters: List<String>,
    selected: Set<Int> = emptySet(),
    opened: Boolean = false,
    initialQuery: String = "",
    val firstVisibleItemIndex: Int = 0,
    val firstVisibleItemScrollOffset: Int = 0,
) {
    var lazyListState: LazyListState? = null
    /**
     * Query [TextFieldValue] that contains text and query selection position.
     */
    var query by mutableStateOf(TextFieldValue(initialQuery))

    /**
     * Flag  Search composable(TextField) focus state.
     */
    var opened by mutableStateOf(opened)

    /**
     * Initial results to show initially before any search commenced. Show these items when
     * [searchDisplay] is in [SearchDisplay.InitialResults].
     */
    var initialResults by mutableStateOf(initialResults)

    /**
     * Suggestions might contain keywords and display chips to show when Search Composable
     * is focused but query is empty.
     */
    var suggestions by mutableStateOf(suggestions)

    /**
     * filters that should be displayed.
     */
    var filters by mutableStateOf(filters)

    /**
     * selected filters by the user.
     */
    var selectedFilters by mutableStateOf(selected)

    internal var searchResultsFlow: Flow<PagingData<R>>? by mutableStateOf(null)
    /**
     * Results of a search action. If this list is empty [searchDisplay] is
     * [SearchDisplay.NoResults] state otherwise in [SearchDisplay.Results] state.
     */
    val searchResults: Flow<PagingData<R>>?
        get() = searchResultsFlow

    /**
     * Last query text, it might be used to prevent doing search when current query and previous
     * query texts are same.
     */
    var previousQueryText = ""
        private set

    /**
     * Check if search initial conditions are met and a search operation is going on.
     * This flag is for showing progressbar.
     */
    var searching by mutableStateOf(false)

    /**
     * Check if a search is initiated. Search is initiated after a specific condition for example
     * a query with more than 2 chars is passed but user is still typing.
     * If  debounce or delay before user stops typing is not needed it can be
     * set to value of [searching].
     */
    var searchInProgress = searching


    val searchDisplay: SearchDisplay
        get() = when {
            !opened && query.text.isEmpty() -> InitialResults
            opened && query.text.isEmpty() -> Suggestions
            searchInProgress -> SearchInProgress
            !searchInProgress && searchResults != null -> {
                previousQueryText = query.text
                NoResults
            }
            else -> {
                previousQueryText = query.text
                Results
            }
        }

    override fun toString(): String {
        return "🚀 STATE\n" +
                "query: ${query.text}, focused: $opened\n" +
                "searchInProgress: $searchInProgress searching: $searching\n" +
                " searchDisplay: $searchDisplay\n\n"
    }

    /**
     * Check if user is running same query as the previous one
     */
    fun sameAsPreviousQuery() = query.text == previousQueryText
}

@Composable
fun SearchState<SearchItem, RecentSearchItem>.rememberSaveableLazyListState(): LazyListState {
    lazyListState = rememberLazyListState(firstVisibleItemIndex, firstVisibleItemScrollOffset)
    return lazyListState as LazyListState

}

/**
 * Enum class with different values to set search state based on text, focus, initial state and
 * results from search.
 *
 *
 * *  **[InitialResults]** represents the initial state before search or when search results are
 * empty and focus is not on a search Composable
 *
 * * **[Suggestions]** represents the state where search Composable gained focus but query is empty.
 *
 * * **[SearchInProgress]** represents initiation of search but not actively searching. For instance
 * search might require at least 3 letters or some specific condition. After condition is passed
 * [SearchState.searching] is true. This is useful for not having a search when first Composable
 * is composed.
 *
 * * **[Results]** represents the state after a successful search operation that returned non
 * empty results
 *
 * * **[NoResults]** represents the state which there are no results returned from a search operation
 *
 */
enum class SearchDisplay {
    InitialResults, Suggestions, SearchInProgress, Results, NoResults
}

private class C3SearchItemJsonSerializer : JsonSerializer<SearchItem> {

    override fun serialize(
        src: SearchItem,
        typeOfSrc: Type,
        context: JsonSerializationContext
    ): JsonElement {
        return when (src) {
            is ItemSearchItem -> context.serialize(src, ItemSearchItem::class.java)
            is SupplierSearchItem -> context.serialize(src, SupplierSearchItem::class.java)
            is AlertSearchItem -> context.serialize(src, AlertSearchItem::class.java)
            is POSearchItem -> context.serialize(src, POSearchItem::class.java)
            is POLSearchItem -> context.serialize(src, POLSearchItem::class.java)
            else -> context.serialize(src, UnknownSearchItem::class.java)
        }
    }

}

private data class SearchStateSaveable(
    val suggestions: List<RecentSearchItem>,
    val filters: List<String>,
    val selected: List<Int>,
    val query: String,
    val opened: Boolean,
    val firstVisibleItemIndex: Int,
    val firstVisibleItemScrollOffset: Int,
)

private fun StateSaver(): Saver<SearchState<SearchItem, RecentSearchItem>, String> {
    val gson = GsonBuilder()
        .registerTypeAdapter(SearchItem::class.java, C3SearchItemJsonDeserializer())
        .registerTypeAdapter(SearchItem::class.java, C3SearchItemJsonSerializer())
        .create()
    return Saver(
        save = {
            val state = SearchStateSaveable(
                suggestions = it.suggestions,
                filters = it.filters,
                selected = it.selectedFilters.toList(),
                query = it.query.text,
                opened = it.opened,
                firstVisibleItemIndex = it.lazyListState?.firstVisibleItemIndex ?: 0,
                firstVisibleItemScrollOffset = it.lazyListState?.firstVisibleItemScrollOffset ?: 0,
            )
            val json = gson.toJson(state)
            json
        },
        restore = { json ->
            val state = gson.fromJson(json, SearchStateSaveable::class.java)
            SearchState(
                suggestions = state.suggestions,
                filters = state.filters,
                selected = state.selected.toSet(),
                initialQuery = state.query,
                opened = state.opened,
                firstVisibleItemIndex = state.firstVisibleItemIndex,
                firstVisibleItemScrollOffset = state.firstVisibleItemScrollOffset,
            )
        }
    )
}