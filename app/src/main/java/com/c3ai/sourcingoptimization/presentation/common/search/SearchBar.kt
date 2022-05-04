package com.c3ai.sourcingoptimization.presentation.common.search

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import com.c3ai.sourcingoptimization.R
import com.c3ai.sourcingoptimization.common.components.ListDivider
import com.c3ai.sourcingoptimization.common.components.OutlinedChip
import com.c3ai.sourcingoptimization.common.components.StaggeredGrid
import com.c3ai.sourcingoptimization.domain.model.RecentSearchItem
import com.c3ai.sourcingoptimization.domain.model.SearchItem
import com.c3ai.sourcingoptimization.presentation.search.RecentSearch
import com.c3ai.sourcingoptimization.presentation.search.SearchCardSimple

@ExperimentalAnimationApi
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SearchBar(
    modifier: Modifier = Modifier,
    viewModel: SearchViewModel = hiltViewModel(),
    fixed: Boolean = false,
    onBackClick: () -> Unit = {},
    onStateChanged: (Boolean) -> Unit = {},
    onSearchResultClick: (SearchItem) -> Unit,
    subContent: @Composable ((SearchState<SearchItem, RecentSearchItem>) -> Unit)? = null,
) {

    val state = viewModel.rememberState()
    val listState = state.rememberSaveableLazyListState()
    val keyboardController = LocalSoftwareKeyboardController.current


    val onSearch: (String, List<Int>?) -> Unit = { query, filters ->
        state.apply {
            if (query.isNotEmpty()) {
                keyboardController?.hide()
                viewModel.search()
                state.suggestions = state.suggestions.toMutableList().apply {
                    find { recentItem ->
                        recentItem.input == query
                                && filters?.let {
                            recentItem.filters?.containsAll(filters)
                        } ?: true
                    }?.let { remove(it) }
                    add(0, RecentSearchItem(query, filters))
                }
            }
        }
    }
    val lazyItems = state.searchResults?.collectAsLazyPagingItems()
    Surface(
        color = Color.Transparent,
        elevation = 6.dp
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = modifier
                    .border(1.dp, MaterialTheme.colors.onBackground, MaterialTheme.shapes.small)
                    .background(
                        color = if (state.opened) MaterialTheme.colors.surface
                        else MaterialTheme.colors.background
                    ),
            ) {
                SearchIconButton(selected = fixed || state.opened) {
                    state.query = TextFieldValue("")
                    state.searchResultsFlow = null
                    state.opened = false
                    onStateChanged(false)
                    onBackClick()
                }
                SearchTextField(
                    query = state.query,
                    onQueryChange = {
                        state.query = it
                        if (it.text.isEmpty()) state.searchResultsFlow = null
                    },
                    onSearchFocusChange = {
                        if (it) {
                            state.opened = it
                            onStateChanged(it)
                        }
                    },
                    onClearQuery = { state.query = TextFieldValue("") },
                    searching = state.searching,
                    modifier = modifier.weight(1f),
                    onSearch = { onSearch(state.query.text, state.selectedFilters.toList()) },
                )
            }
            subContent?.invoke(state)
            if (fixed || state.opened) {
                val loaded = !state.searchInProgress && state.query.text.isNotEmpty()
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    listState,
                    contentPadding = PaddingValues(horizontal = 16.dp),
                ) {
                    item {
                        Text(
                            stringResource(
                                if (loaded) R.string.search_results
                                else R.string.recent
                            ),
                            style = MaterialTheme.typography.h5,
                            color = MaterialTheme.colors.secondary,
                            modifier = Modifier.padding(vertical = 16.dp)
                        )
                    }
                    if (loaded) {
                        lazyItems?.let { items ->
                            items(items.itemCount) { index ->
                                val item = items[index]!!
                                SearchCardSimple(item) { onSearchResultClick(it) }
                                ListDivider()
                            }
                        }
                    } else {
                        items(state.suggestions) { item ->
                            RecentSearch(item) {
                                onSearch(it.input, it.filters)
                                state.query = TextFieldValue(it.input)
                                state.selectedFilters = it.filters?.toSet() ?: emptySet()
                            }
                            ListDivider()
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun SearchIconButton(
    selected: Boolean,
    onClose: () -> Unit,
) {
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    Box {
        AnimatedVisibility(visible = selected) {
            IconButton(
                onClick = {
                    focusManager.clearFocus()
                    keyboardController?.hide()
                    onClose()
                }) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
            }
        }
        AnimatedVisibility(visible = !selected) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                modifier = Modifier
                    .padding(12.dp)
                    .size(24.dp),
            )
        }
    }
}

/**
 * This is a stateless TextField for searching with a Hint when query is empty,
 * and clear and loading [IconButton]s to clear query or show progress indicator when
 * a query is in progress.
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun SearchTextField(
    modifier: Modifier = Modifier,
    query: TextFieldValue,
    onQueryChange: (TextFieldValue) -> Unit,
    onSearchFocusChange: (Boolean) -> Unit,
    onClearQuery: () -> Unit,
    searching: Boolean,
    onSearch: () -> Unit,
) {
    val focusRequester = remember { FocusRequester() }
    CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
        Box(
            contentAlignment = Alignment.CenterStart,
            modifier = modifier.height(48.dp),
        ) {

            if (query.text.isEmpty()) {
                SearchHint(modifier.padding(start = 8.dp, end = 8.dp))
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                BasicTextField(
                    value = query,
                    onValueChange = onQueryChange,
                    textStyle = TextStyle(MaterialTheme.colors.primary, 16.sp),
                    modifier = Modifier
                        .weight(1f)
                        .onFocusChanged {
                            onSearchFocusChange(it.isFocused)
                        }
                        .focusRequester(focusRequester)
                        .padding(end = 8.dp),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Search
                    ),
                    keyboardActions = KeyboardActions(
                        onSearch = {
                            onSearch()
                        }
                    )
                )

                when {
                    searching && query.text.isNotEmpty() -> {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .padding(horizontal = 6.dp)
                                .size(36.dp)
                        )
                    }
                    query.text.isNotEmpty() -> {
                        IconButton(onClick = onClearQuery) {
                            Icon(imageVector = Icons.Filled.Close, contentDescription = null)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchHint(modifier: Modifier = Modifier) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxSize()
            .then(modifier)

    ) {
        Text(
            text = stringResource(R.string.search_),
            color = MaterialTheme.colors.secondary
        )
    }
}

@Composable
fun FiltersGridLayout(
    modifier: Modifier = Modifier,
    state: SearchState<SearchItem, RecentSearchItem>,
) {
    StaggeredGrid(modifier = modifier) {
        state.filters.forEachIndexed { index, filter ->
            OutlinedChip(
                modifier = Modifier.padding(4.dp),
                text = filter,
                selected = state.selectedFilters.contains(index),
                onClick = {
                    state.selectedFilters = state.selectedFilters.toMutableSet().apply {
                        val isRemoved = remove(index)
                        isRemoved || add(index)
                    }
                },
            )
        }
    }
}