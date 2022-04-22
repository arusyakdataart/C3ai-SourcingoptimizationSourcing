package com.c3ai.sourcingoptimization.presentation.common.search

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import com.c3ai.sourcingoptimization.R
import com.c3ai.sourcingoptimization.common.components.ListDivider
import com.c3ai.sourcingoptimization.common.components.OutlinedChip
import com.c3ai.sourcingoptimization.common.components.StaggeredGrid
import com.c3ai.sourcingoptimization.domain.model.RecentSearchItem
import com.c3ai.sourcingoptimization.presentation.search.RecentSearch

@ExperimentalAnimationApi
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SearchBar(
    modifier: Modifier = Modifier,
    suggestions: List<RecentSearchItem> = emptyList(),
    fixed: Boolean = false,
    onBackClick: () -> Unit = {},
    onQueryChange: (TextFieldValue) -> Unit = {},
    onSearchFocusChange: (Boolean) -> Unit = {},
    onSearch: () -> Unit,
    subContent: @Composable (() -> Unit)?
) {

    val state = rememberSearchState(
        initialResults = emptyList<Any>(),
        timeoutMillis = 600,
    ) { query: TextFieldValue ->
        emptyList<Any>()
    }
    val listState = rememberLazyListState()

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
                        color = if (state.focused) MaterialTheme.colors.surface
                        else MaterialTheme.colors.background
                    ),
            ) {
                SearchIconButton(selected = fixed || state.focused) {
                    state.query = TextFieldValue("")
                    onBackClick()
                }
                SearchTextField(
                    query = state.query,
                    onQueryChange = {
                        state.query = it
                        onQueryChange(it)
                    },
                    onSearchFocusChange = {
                        state.focused = it
                        onSearchFocusChange(it)
                    },
                    onClearQuery = { state.query = TextFieldValue("") },
                    searching = state.searching,
                    focused = state.focused,
                    modifier = modifier.weight(1f),
                    onSearch = onSearch,
                )
            }
            subContent?.invoke()
            if (suggestions.isNotEmpty() && (fixed || state.focused)) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    listState,
                    contentPadding = PaddingValues(horizontal = 16.dp),
                ) {
                    item {
                        Text(
                            stringResource(R.string.recent),
                            style = MaterialTheme.typography.h5,
                            color = MaterialTheme.colors.secondary,
                            modifier = Modifier.padding(vertical = 16.dp)
                        )
                    }
                    items(suggestions) { item ->
                        RecentSearch(item)
                        ListDivider()
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
fun SearchTextField(
    modifier: Modifier = Modifier,
    query: TextFieldValue,
    onQueryChange: (TextFieldValue) -> Unit,
    onSearchFocusChange: (Boolean) -> Unit,
    onClearQuery: () -> Unit,
    searching: Boolean,
    focused: Boolean,
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
                            Log.e("onSearch", "call")
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
    filters: List<String>,
    selected: Set<Int>,
    onFilterClick: (Int) -> Unit
) {
    StaggeredGrid(modifier = modifier) {
        filters.forEachIndexed { index, filter ->
            OutlinedChip(
                modifier = Modifier.padding(4.dp),
                text = filter,
                selected = selected.contains(index),
                onClick = { onFilterClick(index) },
            )
        }
    }
}