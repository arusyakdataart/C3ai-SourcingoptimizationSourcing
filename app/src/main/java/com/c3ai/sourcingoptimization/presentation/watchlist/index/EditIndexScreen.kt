package com.c3ai.sourcingoptimization.presentation.watchlist.index

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.c3ai.sourcingoptimization.R
import com.c3ai.sourcingoptimization.common.components.*
import com.c3ai.sourcingoptimization.ui.theme.BackgroundColor
import com.c3ai.sourcingoptimization.ui.theme.Blue

/**
 * A display and edit of the indexes list for item.
 *
 * This sets up the scaffold with the top app bar, and surrounds the content with refresh,
 * loading and error handling.
 */

@ExperimentalFoundationApi
@Composable
fun EditIndexScreen(
    scaffoldState: ScaffoldState,
    uiState: EditIndexUiState,
    indexId: String,
    onRefreshDetails: () -> Unit,
    onSearchInputChanged: (String) -> Unit,
    onBackButtonClick: () -> Unit,
) {
    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                title = stringResource(R.string.edit_index),
                searchInput = uiState.searchInput,
                onBackButtonClick = onBackButtonClick,
                onSearchInputChanged = onSearchInputChanged,
                onClearClick = { onSearchInputChanged("") }

            )
        },
        snackbarHost = { C3SnackbarHost(hostState = it) },
    ) { innerPadding ->
        val contentModifier = Modifier.padding(innerPadding)
        LoadingContent(
            empty = when (uiState) {
                is EditIndexUiState.HasData -> false
                is EditIndexUiState.NoData -> uiState.isLoading
            },
            emptyContent = { FullScreenLoading() },
            loading = uiState.isLoading,
            onRefresh = onRefreshDetails,
            content = {
                when (uiState) {
                    is EditIndexUiState.HasData -> {
                        val listState = rememberLazyListState()
                        val selectedState = remember { mutableStateOf(indexId) }

                        LazyColumn(modifier = Modifier.fillMaxSize(), listState) {
                            stickyHeader {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(BackgroundColor)
                                ) {
                                    Text(
                                        text = stringResource(R.string.select_index),
                                        style = MaterialTheme.typography.h5,
                                        color = MaterialTheme.colors.secondary,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp)
                                    )
                                }
                            }
                            items(items = uiState.indexes, itemContent = {
                                ConstraintLayout(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(
                                            start = 16.dp,
                                            top = 16.dp,
                                            end = 16.dp,
                                            bottom = 0.dp
                                        )
                                ) {
                                    val (titleText, radioButton, divider) = createRefs()
                                    Text(
                                        it.name,
                                        style = MaterialTheme.typography.h3,
                                        color = MaterialTheme.colors.primary,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier
                                            .constrainAs(titleText) {
                                                top.linkTo(parent.top, margin = 16.dp)
                                                start.linkTo(parent.start, margin = 16.dp)
                                            }
                                    )

                                    RadioButton(
                                        selected = selectedState.value == it.id,
                                        onClick = { selectedState.value = it.id },
                                        modifier = Modifier
                                            .padding(start = 16.dp)
                                            .size(24.dp)
                                            .constrainAs(radioButton) {
                                                top.linkTo(parent.top, margin = 16.dp)
                                                end.linkTo(parent.end)
                                            },
                                        colors = RadioButtonDefaults.colors(Blue)
                                    )

                                    Divider(
                                        modifier = Modifier
                                            .padding(
                                                start = 0.dp,
                                                top = 24.dp,
                                                end = 0.dp,
                                                bottom = 0.dp
                                            )
                                            .constrainAs(divider) {
                                                top.linkTo(titleText.bottom)
                                                end.linkTo(parent.end)
                                            }
                                    )
                                }

                            })
                        }
                    }
                    is EditIndexUiState.NoData -> {
                        if (uiState.errorMessages.isEmpty()) {
                            // if there are no posts, and no error, let the user refresh manually
                            PButton(
                                modifier = Modifier.fillMaxWidth(),
                                text = stringResource(id = R.string.tap_to_load_content),
                                onClick = onRefreshDetails,
                            )
                        } else {
                            // there's currently an error showing, don't show any content
                            Box(contentModifier.fillMaxSize()) { /* empty screen */ }
                        }
                    }
                }
            }
        )
    }
}

/**
 * TopAppBar for the edit suppliers screen[EditIndexScreen]
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun TopAppBar(
    title: String,
    searchInput: String,
    placeholderText: String = "",
    onBackButtonClick: () -> Unit,
    onSearchInputChanged: (String) -> Unit = {},
    onClearClick: () -> Unit = {}
) {
    var showClearButton by remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }

    C3TopAppBar(
        title = title,
        onBackButtonClick = onBackButtonClick,
        actions = {
            IconButton(onClick = { /* TODO: Open search */ }) {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = stringResource(R.string.cd_search_menu)
                )
            }
        }
    )
}