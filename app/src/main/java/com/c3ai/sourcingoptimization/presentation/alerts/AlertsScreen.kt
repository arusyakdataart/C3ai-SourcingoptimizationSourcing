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
                AlertsTopAppBar(
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
fun AlertsTopAppBar(
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
            AlertsActions(
                uiState = uiState,
                onSortChanged = onSortChanged,
                onChangeFilter = onChangeFilter
            )
        }
    )
}