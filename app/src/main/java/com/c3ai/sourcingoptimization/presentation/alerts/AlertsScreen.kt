package com.c3ai.sourcingoptimization.presentation.alerts

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.c3ai.sourcingoptimization.R
import com.c3ai.sourcingoptimization.common.SortType
import com.c3ai.sourcingoptimization.common.components.*
import com.c3ai.sourcingoptimization.ui.theme.*
import com.google.gson.Gson
import kotlinx.coroutines.launch

/**
 * A display and edit of the alerts list.
 *
 * This sets up the scaffold with the top app bar, and surrounds the content with refresh,
 * loading and error handling.
 */

@ExperimentalMaterialApi
@ExperimentalFoundationApi
@Composable
fun AlertsScreen(
    scaffoldState: ScaffoldState,
    viewModel: AlertsViewModel,
    uiState: AlertsUiState,
    selectedCategories: List<String>?,
    onRefreshDetails: () -> Unit,
    onSearchInputChanged: (String) -> Unit,
    onSortChanged: (String) -> Unit,
    onChangeFilter: (String) -> Unit,
    onBackButtonClick: () -> Unit,
    onCollapsableItemClick: (String) -> Unit,
    onSupplierClick: (String) -> Unit,
    onItemClick: (String) -> Unit,
    onPOClick: (String) -> Unit,
    onContactClick: (String) -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    val bottomState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)

    if (selectedCategories != null) {
        viewModel.onEvent(AlertsEvent.OnFilterChanged(selectedCategories))
    }

    ModalBottomSheetLayout(
        sheetState = bottomState,
        sheetContent = {
            ContactSupplierBottomSheetContent(
                uiState.selectedSupplierContact?.phone ?: "",
                uiState.selectedSupplierContact?.email ?: "",
            )
        }
    ) {
        Scaffold(
            scaffoldState = scaffoldState,
            topBar = {
                TopAppBar(
                    title = stringResource(R.string.alerts),
                    uiState = uiState,
                    searchInput = uiState.searchInput,
                    onBackButtonClick = onBackButtonClick,
                    onSearchInputChanged = onSearchInputChanged,
                    onClearClick = { onSearchInputChanged("") },
                    onSortChanged = { onSortChanged(it) },
                    onChangeFilter = { onChangeFilter(it) },
                    onContactsClick = {
                        coroutineScope.launch {
                            if (!bottomState.isVisible) {
                                bottomState.show()
                            }
                        }
                    }
                )
            },
            snackbarHost = { C3SnackbarHost(hostState = it) },
        ) { innerPadding ->

            val contentModifier = Modifier.padding(innerPadding)
            LoadingContent(
                empty = when (uiState) {
                    is AlertsUiState.HasData -> false
                    is AlertsUiState.NoData -> uiState.isLoading
                },
                emptyContent = { FullScreenLoading() },
                loading = uiState.isLoading,
                onRefresh = onRefreshDetails,
                content = {
                    AlertsContent(
                        uiState = uiState,
                        viewModel = viewModel,
                        coroutineScope = coroutineScope,
                        bottomState = bottomState,
                        modifier = contentModifier,
                        onRefreshDetails = onRefreshDetails,
                        onCollapsableItemClick = onCollapsableItemClick,
                        onSupplierClick = onSupplierClick,
                        onItemClick = onItemClick,
                        onPOClick = onPOClick,
                        onContactClick = onContactClick
                    )
                }
            )
        }
    }
}



/**
 * TopAppBar for the alerts screen[AlertsScreen]
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun TopAppBar(
    title: String,
    uiState: AlertsUiState,
    searchInput: String,
    placeholderText: String = "",
    onBackButtonClick: () -> Unit,
    onSearchInputChanged: (String) -> Unit = {},
    onClearClick: () -> Unit = {},
    onContactsClick: () -> Unit,
    onSortChanged: (String) -> Unit = {},
    onChangeFilter: (String) -> Unit = {},
) {
    var showClearButton by remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }
    var sortMenuExpanded by remember { mutableStateOf(false) }
    var sortApplied by remember { mutableStateOf("") }
    var sortType by remember { mutableStateOf(SortType.ASCENDING) }

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
            IconButton(onClick = { sortMenuExpanded = true }) {
                Icon(
                    imageVector = Icons.Filled.Sort,
                    contentDescription = stringResource(R.string.cd_sort_menu)
                )
            }
            DropdownMenu(
                modifier = Modifier,
                expanded = sortMenuExpanded,
                onDismissRequest = { sortMenuExpanded = false }
            ) {
                val resources = listOf(
                    "timestamp" to "Alert Creation Date",
                    "flagged" to "Alert Flag Status",
                    "currentState" to "Alert State",
                    "readStatus" to "Alert Status"
                )
                resources.map { it ->
                    DropdownMenuItem(
                        onClick = {
                            sortMenuExpanded = false
                            if (sortApplied == it.first) {
                                sortType =
                                    if (sortType == SortType.ASCENDING) SortType.DESCENDING else SortType.ASCENDING
                            } else {
                                sortType = SortType.DESCENDING
                            }
                            val orderType =
                                if (sortType == SortType.DESCENDING) "descending" else "ascending"
                            sortApplied = it.first

                            onSortChanged(orderType + "(" + it.first + ")")
                        },
                    ) {
                        Row(modifier = Modifier.fillMaxSize()) {
                            if (sortApplied == it.first) {
                                Icon(
                                    imageVector = if (sortType == SortType.ASCENDING) Icons.Filled.ArrowUpward else Icons.Filled.ArrowDownward,
                                    contentDescription = "",
                                    tint = Blue
                                )
                            } else {
                                Spacer(modifier = Modifier.width(24.dp))
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                it.second,
                                style = MaterialTheme.typography.subtitle1,
                                color = if (sortApplied == it.first) Blue else MaterialTheme.colors.secondaryVariant,
                            )
                        }
                    }
                }
            }
            IconButton(
                onClick = {
                    onChangeFilter(Gson().toJson((uiState as AlertsUiState.HasData).selectedCategoriesList))
                }
            ) {
                Icon(
                    imageVector = Icons.Filled.Settings,
                    contentDescription = stringResource(R.string.cd_settings_menu)
                )
            }
        }
    )
}