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
    onRetry: () -> Unit,
    onError: () -> Unit
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
    // Process one error message at a time and show them as Snackbars in the UI
    if (uiState.errorMessages.isNotEmpty()) {
        // Remember the errorMessage to display on the screen
        val errorMessage = remember(uiState) { uiState.errorMessages[0] }

        // Get the text to show on the message from resources
        val errorMessageText: String = stringResource(errorMessage.messageId)
        val retryMessageText = stringResource(id = R.string.retry)

        // If onRefreshPosts or onErrorDismiss change while the LaunchedEffect is running,
        // don't restart the effect and use the latest lambda values.
        val onRefreshPostsState by rememberUpdatedState({ })
        val onErrorDismissState by rememberUpdatedState({ })

        // Effect running in a coroutine that displays the Snackbar on the screen
        // If there's a change to errorMessageText, retryMessageText or scaffoldState,
        // the previous effect will be cancelled and a new one will start with the new values
        LaunchedEffect(errorMessageText, retryMessageText, scaffoldState) {
            val snackbarResult = scaffoldState.snackbarHostState.showSnackbar(
                message = errorMessageText,
                actionLabel = retryMessageText
            )
            if (snackbarResult == SnackbarResult.ActionPerformed) {
                onRefreshPostsState()
                onRetry()
            }
            // Once the message is displayed and dismissed, notify the ViewModel
            onErrorDismissState()
            onError()
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