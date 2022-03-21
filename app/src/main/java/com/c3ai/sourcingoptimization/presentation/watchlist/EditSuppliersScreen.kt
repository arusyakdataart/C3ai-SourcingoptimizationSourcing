package com.c3ai.sourcingoptimization.presentation.watchlist

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.c3ai.sourcingoptimization.ui.theme.Gray70

/**
 * A display and edit of the suppliers list for item.
 *
 * This sets up the scaffold with the top app bar, and surrounds the content with refresh,
 * loading and error handling.
 */

@ExperimentalFoundationApi
@Composable
fun EditSuppliersScreen(
    scaffoldState: ScaffoldState,
    uiState: EditSuppliersUiState,
    itemId: String,
    onRefreshDetails: () -> Unit,
    onSearchInputChanged: (String) -> Unit,
    onSupplierClick: (String) -> Unit,
    onCheckSupplier: (String) -> Unit,
    onUncheckSupplier: (String) -> Unit,
    onBackButtonClick: () -> Unit,
) {
    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                title = stringResource(R.string.edit_suppliers),
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
                is EditSuppliersUiState.HasDetails -> false
                is EditSuppliersUiState.NoDetails -> uiState.isLoading
            },
            emptyContent = { FullScreenLoading() },
            loading = uiState.isLoading,
            onRefresh = onRefreshDetails,
            content = {
                when (uiState) {
                    is EditSuppliersUiState.HasDetails -> {
                        val listState = rememberLazyListState()
                        LazyColumn(modifier = Modifier.fillMaxSize(), listState) {
                            stickyHeader {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(BackgroundColor)
                                ) {
                                    Text(
                                        text = stringResource(R.string.select_suppliers),
                                        style = MaterialTheme.typography.h3,
                                        color = MaterialTheme.colors.secondary,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp)
                                    )
                                }
                            }
                            items(items = uiState.suppliers, itemContent = {
                                val checkedState = remember { mutableStateOf(true) }
                                ConstraintLayout(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(
                                            start = 16.dp,
                                            top = 24.dp,
                                            end = 16.dp,
                                            bottom = 0.dp
                                        )
                                ) {
                                    val (header, image, titleText, subtitleText, checkBox, divider) = createRefs()
                                    val nameShort = it.name.split(" ")
                                        .joinToString("") { it[0].toString() }.uppercase()
                                    Text(
                                        text = stringResource(
                                            R.string.supplier_,
                                            it.id
                                        ).uppercase(),
                                        style = MaterialTheme.typography.subtitle2,
                                        color = MaterialTheme.colors.secondary,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .constrainAs(header) {
                                                top to parent.top
                                                start to parent.start
                                            }
                                    )

                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier
                                            .size(48.dp)
                                            .clip(CircleShape)
                                            .background(Gray70)
                                            .constrainAs(image) {
                                                top.linkTo(header.bottom, margin = 16.dp)
                                                start to parent.start
                                            }) {
                                        Text(
                                            nameShort,
                                            style = MaterialTheme.typography.h1,
                                            color = MaterialTheme.colors.primary,
                                        )
                                    }

                                    Text(
                                        it.name,
                                        style = MaterialTheme.typography.h3,
                                        color = MaterialTheme.colors.primary,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier
                                            .constrainAs(titleText) {
                                                top.linkTo(header.bottom, margin = 16.dp)
                                                start.linkTo(image.end, margin = 16.dp)
                                            }
                                    )
                                    Text(
                                        it.location?.address?.components?.joinToString {
                                            it.name ?: ""
                                        } ?: "",
                                        style = MaterialTheme.typography.subtitle1,
                                        color = MaterialTheme.colors.secondary,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier
                                            .constrainAs(subtitleText) {
                                                top.linkTo(titleText.bottom)
                                                start.linkTo(image.end, margin = 16.dp)
                                            }
                                    )
                                    Checkbox(
                                        checked = checkedState.value,
                                        onCheckedChange = { checkedState.value = it },
                                        modifier = Modifier
                                            .padding(start = 16.dp)
                                            .size(24.dp)
                                            .constrainAs(checkBox) {
                                                top.linkTo(header.bottom, margin = 16.dp)
                                                end.linkTo(parent.end)
                                            },
                                        colors = CheckboxDefaults.colors(Blue)
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
                                                top.linkTo(image.bottom)
                                                end.linkTo(parent.end)
                                            }
                                    )
                                }

                            })
                        }
                    }
                    is EditSuppliersUiState.NoDetails -> {
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
 * TopAppBar for the edit suppliers screen[EditSupplierScreen]
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