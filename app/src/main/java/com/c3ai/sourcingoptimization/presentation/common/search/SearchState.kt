package com.c3ai.sourcingoptimization.presentation.common.search

import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.text.input.TextFieldValue
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
 * @param searchResults results of latest search
 *
 */
@Composable
fun rememberSaveableSearchState(
    initialResults: List<SearchItem> = emptyList(),
    suggestions: List<RecentSearchItem> = emptyList(),
    searchResults: List<SearchItem> = emptyList(),
    timeoutMillis: Long = 0,
): SearchState<SearchItem, RecentSearchItem> {
    return rememberSaveable(saver = StateSaver()) {
        SearchState(
            initialResults = initialResults,
            suggestions = suggestions,
            searchResults = searchResults,
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
                .mapLatest { query: TextFieldValue ->
                    state.searching = true
                    // This delay is for showing circular progress bar, it's optional
                    delay(300)
                }
                .collect { result ->
                    state.searching = false
                }
        }
    }
}

/**
 * Creates a [FilterState] that is remembered across compositions and cache itself.
 *
 * @param filters filters for displaying.
 * @param selected selected filters by the user.
 *
 */
@Composable
fun rememberSaveableFilterState(
    filters: List<String>,
    selected: Set<Int> = emptySet(),
): FilterState<String, Int> {
    return rememberSaveable(saver = FiltersStateSaver()) {
        FilterState(
            filters = filters,
            selected = selected,
        )
    }
}

/**
 * Creates a [SearchState] that is remembered across compositions.
 *
 * @param initialResults results that can be displayed before doing a search
 * @param suggestions chip or cards that can be suggested to user when Search composable is focused
 * but query is empty
 * @param searchResults results of latest search
 *
 */
@Composable
fun <R, S> rememberSearchState(
    initialResults: List<R> = emptyList(),
    suggestions: List<S> = emptyList(),
    searchResults: List<R> = emptyList(),
): SearchState<R, S> {
    return remember {
        SearchState(
            initialResults = initialResults,
            suggestions = suggestions,
            searchResults = searchResults,
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
 * @param searchResults results of latest search
 * @param timeoutMillis timeout before user finishes typing. After this
 * timeout [SearchState.searching]
 * is set to true.
 *
 */
@Composable
fun <R, S> rememberSearchState(
    initialResults: List<R> = emptyList(),
    suggestions: List<S> = emptyList(),
    searchResults: List<R> = emptyList(),
    timeoutMillis: Long = 0,
): SearchState<R, S> {

    return remember {
        SearchState(
            initialResults = initialResults,
            suggestions = suggestions,
            searchResults = searchResults,
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
                .mapLatest { query: TextFieldValue ->
                    state.searching = true
                    // This delay is for showing circular progress bar, it's optional
                    delay(300)
                }
                .collect { result ->
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
 * @param searchResults results of latest search
 */
@Parcelize
class SearchState<R, S> internal constructor(
    initialResults: List<R> = emptyList(),
    suggestions: List<S>,
    searchResults: List<R>,
    initialQuery: String = "",
    opened: Boolean = false
) {
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
     * Results of a search action. If this list is empty [searchDisplay] is
     * [SearchDisplay.NoResults] state otherwise in [SearchDisplay.Results] state.
     */
    var searchResults by mutableStateOf(searchResults)

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
            !searchInProgress && searchResults.isEmpty() -> {
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

/**
 *  A state object that can be hosted to control and observe filters changing.
 *
 * Create instance using [rememberSaveableFilterState].
 *
 * @param filters results that can be displayed before doing a search
 * @param selected chip or cards that can be suggested to user when Search composable is focused
 * but query is empty
 */
@Parcelize
class FilterState<F, S> internal constructor(
    filters: List<F>,
    selected: Set<S> = emptySet(),
) {
    /**
     * filters that should be displayed.
     */
    var filters by mutableStateOf(filters)

    /**
     * selected filters by the user.
     */
    var selected by mutableStateOf(selected)
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

private class SearchItemJsonSerializer : JsonSerializer<SearchItem> {

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
    val searchResults: List<SearchItem>,
    val query: String,
    val opened: Boolean,
)

private fun StateSaver(): Saver<SearchState<SearchItem, RecentSearchItem>, String> {
    val gson = GsonBuilder()
        .registerTypeAdapter(SearchItem::class.java, C3SearchItemJsonDeserializer())
        .registerTypeAdapter(SearchItem::class.java, SearchItemJsonSerializer())
        .create()
    return Saver(
        save = {
            val state = SearchStateSaveable(
                suggestions = it.suggestions,
                searchResults = it.searchResults,
                query = it.query.text,
                opened = it.opened,
            )
            val json = gson.toJson(state)
            json
        },
        restore = { json ->
            val state = gson.fromJson(json, SearchStateSaveable::class.java)
            SearchState(
                suggestions = state.suggestions,
                searchResults = state.searchResults,
                initialQuery = state.query,
                opened = state.opened,
            )
        }
    )
}

private data class FiltersStateSaveable(
    val filters: List<String>,
    val selected: List<Int>,
)

private fun FiltersStateSaver(): Saver<FilterState<String, Int>, String> {
    val gson = GsonBuilder().create()
    return Saver(
        save = {
            val state = FiltersStateSaveable(
                filters = it.filters,
                selected = it.selected.toList(),
            )
            val json = gson.toJson(state)
            json
        },
        restore = { json ->
            val state = gson.fromJson(json, FiltersStateSaveable::class.java)
            FilterState(
                filters = state.filters,
                selected = state.selected.toSet(),
            )
        }
    )
}