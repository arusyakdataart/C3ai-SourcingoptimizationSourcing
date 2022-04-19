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
import androidx.compose.ui.res.stringArrayResource
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
    query: TextFieldValue,
    onQueryChange: (TextFieldValue) -> Unit,
    onSearchFocusChange: (Boolean) -> Unit,
    onClearQuery: () -> Unit,
    onBack: () -> Unit,
    searching: Boolean,
    focused: Boolean,
    modifier: Modifier = Modifier
) {

    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

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
                        color = if (focused) MaterialTheme.colors.surface
                        else MaterialTheme.colors.background
                    ),
            ) {
                AnimatedVisibility(visible = focused) {
                    IconButton(
                        onClick = {
                            focusManager.clearFocus()
                            keyboardController?.hide()
                            onBack()
                        }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
                    }
                }
                AnimatedVisibility(visible = !focused) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        modifier = Modifier
                            .padding(12.dp)
                            .size(24.dp),
                    )
                }
                SearchTextField(
                    query,
                    onQueryChange,
                    onSearchFocusChange,
                    onClearQuery,
                    searching,
                    focused,
                    modifier.weight(1f)
                )
            }
            FiltersGridLayout(
                filters = stringArrayResource(R.array.searchFilters).toList(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 30.dp)
                    .horizontalScroll(rememberScrollState())
            ) {

            }
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
    onFilterClick: (Int) -> Unit
) {
    Box(modifier = modifier) {
        filters.forEachIndexed { index, filter ->
            OutlinedChip(
                modifier = Modifier.padding(4.dp),
                text = filter,
                onClick = {
                    onFilterClick(index)
                },
            )
        }
    }
}