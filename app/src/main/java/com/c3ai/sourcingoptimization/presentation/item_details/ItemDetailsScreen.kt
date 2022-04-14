package com.c3ai.sourcingoptimization.presentation.item_details

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.c3ai.sourcingoptimization.R
import com.c3ai.sourcingoptimization.common.SortType
import com.c3ai.sourcingoptimization.common.components.*
import com.c3ai.sourcingoptimization.data.C3Result
import com.c3ai.sourcingoptimization.data.repository.C3MockRepositoryImpl
import com.c3ai.sourcingoptimization.domain.model.C3Vendor
import com.c3ai.sourcingoptimization.presentation.item_details.components.OverviewComponent
import com.c3ai.sourcingoptimization.presentation.item_details.components.POLinesComponent
import com.c3ai.sourcingoptimization.presentation.item_details.components.SuppliersComponent
import com.c3ai.sourcingoptimization.ui.theme.*
import com.github.aachartmodel.aainfographics.aachartcreator.*
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


/**
 * A display of the item details screen that has the lists.
 *
 * This sets up the scaffold with the top app bar, and surrounds the content with refresh,
 * loading and error handling.
 *
 * This helper function exists because [ItemDetailsScreen] is big and have two states,
 * so we need to decompose it with additional function [ItemDetailsDataScreen].
 */
@OptIn(
    ExperimentalComposeUiApi::class,
    ExperimentalAnimationApi::class,
    ExperimentalMaterialApi::class
)
@Composable
fun ItemDetailsScreen(
    scaffoldState: ScaffoldState,
    itemId: String,
    suppliers: String?,
    uiState: ItemDetailsUiState,
    onRefreshDetails: () -> Unit,
    onTabItemClick: (Int) -> Unit,
    onBackButtonClick: () -> Unit,
    loadData: () -> Unit,
    onDateRangeSelected: (Int) -> Unit,
    onStatsTypeSelected: (Int) -> Unit,
    onSupplierClick: (String) -> Unit,
    onEditSuppliersClick: (String) -> Unit,
    onEditIndexClick: (String) -> Unit,
    onChartViewMoveOver: (Int) -> Unit,
    onSortChanged: (String) -> Unit = {},
    onAlertsClick: (String) -> Unit,
    onContactClick: (String) -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    val bottomState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
    val checkedSuppliers = Gson().fromJson(suppliers, Array<C3Vendor>::class.java)?.asList()

    LaunchedEffect(itemId) {
        loadData()
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
                    coroutineScope = coroutineScope,
                    title = stringResource(R.string.item_, itemId),
                    selectedTabIndex = uiState.tabIndex,
                    onBackButtonClick = onBackButtonClick,
                    onSortChanged = onSortChanged,
                    bottomState = bottomState
                )
            },
            snackbarHost = { C3SnackbarHost(hostState = it) },
        ) { innerPadding ->
            val contentModifier = Modifier.padding(innerPadding)

            LoadingContent(
                empty = when (uiState) {
                    is ItemDetailsUiState.NoItem -> uiState.isLoading
                    else -> false
                },
                emptyContent = { FullScreenLoading() },
                loading = uiState.isLoading,
                onRefresh = onRefreshDetails,
                content = {
                    Column {
                        Tabs(
                            selectedTab = uiState.tabIndex,
                            TabItem(stringResource(R.string.overview)) {
                                onTabItemClick(0)
                            },
                            TabItem(stringResource(R.string.po_lines)) {
                                onTabItemClick(1)
                            },
                            TabItem(stringResource(R.string.suppliers)) {
                                onTabItemClick(2)
                            }
                        )
                        when (uiState) {
                            is ItemDetailsUiState.HasItem -> {
                                when (uiState.tabIndex) {
                                    0 -> {
                                        OverviewComponent(
                                            uiState = uiState,
                                            onAlertsClick = onAlertsClick,
                                            onDateRangeSelected = onDateRangeSelected,
                                            onStatsTypeSelected = onStatsTypeSelected,
                                            onSupplierClick = onEditSuppliersClick,
                                            onIndexClick = onEditIndexClick,
                                            onChartViewMoveOver = onChartViewMoveOver
                                        )
                                    }
                                    1 -> {
                                        POLinesComponent(
                                            uiState = uiState,
                                            loadData = loadData,
                                            onAlertsClick = onAlertsClick,
                                        )
                                    }
                                    2 -> {
                                        SuppliersComponent(
                                            uiState = uiState,
                                            loadData = loadData,
                                            onSupplierClick = onSupplierClick,
                                            onAlertsClick = onAlertsClick,
                                            onContactClick = {
                                                onContactClick(it)
                                                coroutineScope.launch {
                                                    if (!bottomState.isVisible) {
                                                        bottomState.show()
                                                    }
                                                }
                                            }
                                        )
                                    }
                                }
                            }
                            is ItemDetailsUiState.NoItem -> {
//                            if (uiState.errorMessages.isEmpty()) {
//                                // if there are no posts, and no error, let the user refresh manually
//                                PButton(
//                                    modifier = Modifier.fillMaxWidth(),
//                                    text = stringResource(id = R.string.tap_to_load_content),
//                                    onClick = onRefreshDetails,
//                                )
//                            } else {
//                                // there's currently an error showing, don't show any content
//                                Box(contentModifier.fillMaxSize()) { /* empty screen */ }
//                            }
                            }
                        }
                    }
                }
            )
        }
    }

    // Process one error message at a time and show them as Snackbars in the UI
//    if (uiState.errorMessages.isNotEmpty()) {
//        // Remember the errorMessage to display on the screen
//        val errorMessage = remember(uiState) { uiState.errorMessages[0] }
//
//        // Get the text to show on the message from resources
//        val errorMessageText: String = stringResource(errorMessage.messageId)
//        val retryMessageText = stringResource(id = R.string.retry)
//
//        // If onRefreshPosts or onErrorDismiss change while the LaunchedEffect is running,
//        // don't restart the effect and use the latest lambda values.
//        val onRefreshPostsState by rememberUpdatedState({ })
//        val onErrorDismissState by rememberUpdatedState({ })
//
//        // Effect running in a coroutine that displays the Snackbar on the screen
//        // If there's a change to errorMessageText, retryMessageText or scaffoldState,
//        // the previous effect will be cancelled and a new one will start with the new values
//        LaunchedEffect(errorMessageText, retryMessageText, scaffoldState) {
//            val snackbarResult = scaffoldState.snackbarHostState.showSnackbar(
//                message = errorMessageText,
//                actionLabel = retryMessageText
//            )
//            if (snackbarResult == SnackbarResult.ActionPerformed) {
//                onRefreshPostsState()
//            }
//            // Once the message is displayed and dismissed, notify the ViewModel
//            onErrorDismissState()
//        }
//    }
}

/**
 * TopAppBar for the suppliers details screen[ItemDetailsScreen]
 */
@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterialApi::class)
@Composable
private fun TopAppBar(
    coroutineScope: CoroutineScope,
    title: String,
    selectedTabIndex: Int,
    onBackButtonClick: () -> Unit,
    onSortChanged: (String) -> Unit = {},
    bottomState: ModalBottomSheetState,
) {
    var sortMenuExpanded by remember { mutableStateOf(false) }
    var firstTabSortApplied by remember { mutableStateOf("") }
    var firstTabSortType by remember { mutableStateOf(SortType.ASCENDING) }
    var secondTabSortApplied by remember { mutableStateOf("") }
    var secondTabSortType by remember { mutableStateOf(SortType.ASCENDING) }

    C3TopAppBar(
        title = title,
        onBackButtonClick = onBackButtonClick,
        actions = {
            if (selectedTabIndex != 0) {
//                IconButton(onClick = { sortMenuExpanded = true }) {
//                    Icon(
//                        imageVector = Icons.Filled.Sort,
//                        contentDescription = stringResource(R.string.cd_sort_menu)
//                    )
//                }
//                DropdownMenu(
//                    modifier = Modifier,
//                    expanded = sortMenuExpanded,
//                    onDismissRequest = { sortMenuExpanded = false }
//                ) {
//                    val resources = if (selectedTabIndex == 1) listOf(
//                        "totalCost.value" to "Total Cost",
//                        "unitPrice.value" to "Unit Price",
//                        "totalQuantity.value" to "Quantity",
//                        "orderCreationDate" to "Opened Date",
//                        "closedDate" to "Closed Date",
//                        "requestedDeliveryDate" to "Requested Delivery Date",
//                        "promisedDeliveryDate" to "Promised Delivery Date",
//                        "actualLeadTime" to "Actual Lead Time",
//                        "plannedLeadTime" to "Planned Lead Time"
//                    ) else listOf(
//                        "id" to "Item ID",
//                        "name" to "Item Name",
//                        "openPOValue.value" to "Open PO Line Value",
//                        "closedPOValue.value" to "Closed PO Line Value",
//                        "averageUnitPricePaid.value" to "Average Unit Price"
//                    )
//
//                    resources.map {
//                        DropdownMenuItem(
//                            onClick = {
//                                sortMenuExpanded = false
//                                var orderType = ""
//                                when (selectedTabIndex) {
//                                    1 -> {
//                                        firstTabSortType = if (firstTabSortApplied == it.first) {
//                                            if (firstTabSortType == SortType.ASCENDING) SortType.DESCENDING else SortType.ASCENDING
//                                        } else {
//                                            SortType.DESCENDING
//                                        }
//                                        orderType =
//                                            if (firstTabSortType == SortType.DESCENDING) "descending" else "ascending"
//                                        firstTabSortApplied = it.first
//                                    }
//                                    2 -> {
//                                        secondTabSortType = if (secondTabSortApplied == it.first) {
//                                            if (secondTabSortType == SortType.ASCENDING) SortType.DESCENDING else SortType.ASCENDING
//                                        } else {
//                                            SortType.DESCENDING
//                                        }
//                                        orderType =
//                                            if (secondTabSortType == SortType.DESCENDING) "descending" else "ascending"
//                                        secondTabSortApplied = it.first
//                                    }
//                                }
//                                onSortChanged(orderType + "(" + it.first + ")")
//                            },
//                        ) {
//                            Row(modifier = Modifier.fillMaxSize()) {
//                                val sortApplied =
//                                    if (selectedTabIndex == 1) firstTabSortApplied else secondTabSortApplied
//                                if (sortApplied == it.first) {
//                                    val sortType =
//                                        if (selectedTabIndex == 1) firstTabSortType else secondTabSortType
//                                    Icon(
//                                        imageVector = if (sortType == SortType.ASCENDING) Icons.Filled.ArrowUpward else Icons.Filled.ArrowDownward,
//                                        contentDescription = "",
//                                        tint = Blue
//                                    )
//                                } else {
//                                    Spacer(modifier = Modifier.width(24.dp))
//                                }
//                                Spacer(modifier = Modifier.width(16.dp))
//                                Text(
//                                    it.second,
//                                    style = MaterialTheme.typography.subtitle1,
//                                    color = if (sortApplied == it.first) Blue else MaterialTheme.colors.secondaryVariant,
//                                )
//                            }
//                        }
//                    }
//                }
                IconButton(onClick = { /* TODO: Open search */ }) {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = stringResource(R.string.cd_search_menu)
                    )
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Preview
@Composable
fun ItemDetailsPreview() {
    val item = runBlocking {
        (C3MockRepositoryImpl().getItemDetails("") as C3Result.Success).data
    }
    C3AppTheme {
        ItemDetailsScreen(
            scaffoldState = rememberScaffoldState(),
            itemId = item.id,
            suppliers = "",
            uiState = PreviewItemDetailsUiState(item),
            onRefreshDetails = {},
            onTabItemClick = {},
            onBackButtonClick = {},
            loadData = {},
            onDateRangeSelected = {},
            onStatsTypeSelected = {},
            onSupplierClick = {},
            onEditSuppliersClick = {},
            onEditIndexClick = {},
            onChartViewMoveOver = {},
            onSortChanged = {},
            onAlertsClick = {},
            onContactClick = {},
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Preview
@Composable
fun ItemDetailsPOLinesTabPreview() {
    val item = runBlocking {
        (C3MockRepositoryImpl().getItemDetails("") as C3Result.Success).data
    }
    C3AppTheme {
        ItemDetailsScreen(
            scaffoldState = rememberScaffoldState(),
            itemId = item.id,
            suppliers = "",
            uiState = PreviewItemDetailsUiState(item, 1),
            onRefreshDetails = {},
            onTabItemClick = {},
            onBackButtonClick = {},
            loadData = {},
            onDateRangeSelected = {},
            onStatsTypeSelected = {},
            onSupplierClick = {},
            onEditSuppliersClick = {},
            onEditIndexClick = {},
            onChartViewMoveOver = {},
            onSortChanged = {},
            onAlertsClick = {},
            onContactClick = {},
        )
    }
}