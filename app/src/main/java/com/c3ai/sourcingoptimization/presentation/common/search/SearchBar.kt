package com.c3ai.sourcingoptimization.presentation.common.search

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
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
import com.c3ai.sourcingoptimization.common.components.OutlinedChip
import com.c3ai.sourcingoptimization.common.components.StaggeredGrid

@ExperimentalAnimationApi
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SearchBar(
    modifier: Modifier = Modifier,
    fglModifier: Modifier = Modifier,
    filters: List<String>? = null,
    fixed: Boolean = false,
    singLine: Boolean = false,
    onQueryChange: (TextFieldValue) -> Unit = {},
    onSearchFocusChange: (Boolean) -> Unit = {},
    onBackClick: () -> Unit = {},
    onSearch: (String, Set<Int>) -> Unit,
) {

    val state = rememberSearchState(
        initialResults = emptyList<Any>(),
        suggestions = emptyList<Any>(),
        timeoutMillis = 600,
    ) { query: TextFieldValue ->
        emptyList<Any>()
    }

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
                    state.query,
                    {
                        state.query = it
                        onQueryChange(it)
                    },
                    {
                        state.focused = it
                        onSearchFocusChange(it)
                    },
                    { state.query = TextFieldValue("") },
                    state.searching,
                    state.focused,
                    modifier.weight(1f)
                )
            }
            filters?.let {
                if (singLine) fglModifier.horizontalScroll(rememberScrollState())
                FiltersGridLayout(
                    filters = filters,
                    selected = state.selectedFilters,
                    modifier = fglModifier
                ) {
                    state.selectedFilters = state.selectedFilters.toMutableSet().apply {
                        val isRemoved = remove(it)
                        isRemoved || add(it)
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
    query: TextFieldValue,
    onQueryChange: (TextFieldValue) -> Unit,
    onSearchFocusChange: (Boolean) -> Unit,
    onClearQuery: () -> Unit,
    searching: Boolean,
    focused: Boolean,
    modifier: Modifier = Modifier
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