package com.c3ai.sourcingoptimization.presentation.supplier_details

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.c3ai.sourcingoptimization.R
import com.c3ai.sourcingoptimization.common.components.*
import com.c3ai.sourcingoptimization.data.C3Result
import com.c3ai.sourcingoptimization.data.repository.C3MockRepositoryImpl
import com.c3ai.sourcingoptimization.presentation.views.UiItem
import com.c3ai.sourcingoptimization.presentation.views.UiPurchaseOrder
import com.c3ai.sourcingoptimization.ui.theme.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

/**
 * A display of the supplier details screen that has the lists.
 *
 * This sets up the scaffold with the top app bar, and surrounds the content with refresh,
 * loading and error handling.
 *
 * This helper function exists because [SupplierDetailsScreen] is big and have two states,
 * so we need to decompose it with additional function [SupplierDetailsDataScreen].
 */
@OptIn(
    ExperimentalComposeUiApi::class,
    ExperimentalAnimationApi::class,
    ExperimentalMaterialApi::class
)
@Composable
fun SupplierDetailsScreen(
    scaffoldState: BottomSheetScaffoldState,
    supplierId: String,
    uiState: SupplierDetailsUiState,
    onRefreshDetails: () -> Unit,
    onSearchInputChanged: (String) -> Unit,
    onTabItemClick: (Int) -> Unit,
    onExpandableItemClick: (String) -> Unit,
    onPOItemClick: (String) -> Unit,
    onC3ItemClick: (String) -> Unit,
    onAlertsClick: (String) -> Unit,
    onSortChanged: (String) -> Unit,
    onBackButtonClick: () -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    var selectedTabIndex: Int by remember {
        mutableStateOf(0)
    }

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                title = stringResource(R.string.supplier_, supplierId),
                selectedTabIndex,
                searchInput = uiState.searchInput,
                onBackButtonClick = onBackButtonClick,
                onSearchInputChanged = onSearchInputChanged,
                onClearClick = { onSearchInputChanged("") },
                onSortChanged = { onSortChanged(it) },
                onContactsClick = {
                    coroutineScope.launch {
                        if (scaffoldState.bottomSheetState.isCollapsed) {
                            scaffoldState.bottomSheetState.expand()
                        } else {
                            scaffoldState.bottomSheetState.collapse()
                        }
                    }
                }
            )
        },
        snackbarHost = { C3SnackbarHost(hostState = it) },
        sheetContent = { ContactSupplierBottomSheetContent("", "") },
        sheetPeekHeight = 0.dp
    ) { innerPadding ->
        val contentModifier = Modifier.padding(innerPadding)

        LoadingContent(
            empty = when (uiState) {
                is SupplierDetailsUiState.HasDetails -> false
                is SupplierDetailsUiState.NoDetails -> uiState.isLoading
            },
            emptyContent = { FullScreenLoading() },
            loading = uiState.isLoading,
            onRefresh = onRefreshDetails,
            content = {
                when (uiState) {
                    is SupplierDetailsUiState.HasDetails -> CollapsingContentList(
                        contentModifier = Modifier.height(156.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        items = when (uiState.tabIndex) {
                            1 -> {
                                selectedTabIndex = 1
                                uiState.items
                            }
                            else -> {
                                selectedTabIndex = 0
                                uiState.poLines
                            }
                        },
                        header = {
                            Tabs(
                                selectedTab = uiState.tabIndex,
                                TabItem(stringResource(R.string.po_lines)) { onTabItemClick(0) },
                                TabItem(stringResource(R.string.items_supplied)) { onTabItemClick(1) }
                            )
                        },
                        content = { SuppliersDetailsInfo(uiState) }
                    ) { item ->
                        when (item) {
                            is UiItem -> ItemsSuppliedList(
                                item,
                                onC3ItemClick,
                                onAlertsClick
                            )
                            is UiPurchaseOrder.Order -> ExpandableLayout(
                                expanded = uiState.expandedListItemIds.contains(item.id),
                                onClick = { onExpandableItemClick(item.id) },
                                content = { PoLinesListSimple(item, onPOItemClick) },
                                modifier = Modifier
                                    .background(MaterialTheme.colors.background)
                                    .padding(horizontal = 16.dp)
                            ) {
                                item.orderLines.map { poLine ->
                                    PoLinesListExpanded(poLine, onPOAlertsClick = onAlertsClick)
                                }
                            }
                        }
                    }
                    is SupplierDetailsUiState.NoDetails -> {
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
            }
            // Once the message is displayed and dismissed, notify the ViewModel
            onErrorDismissState()
        }
    }
}

@Composable
private fun SuppliersDetailsInfo(
    uiState: SupplierDetailsUiState.HasDetails,
) {
    val supplier = uiState.supplier
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        val activeColor = if (supplier.active) Green40 else Lila40
        val contractColor = if (supplier.hasActiveContracts) Green40 else Lila40
        val diversityColor = if (supplier.diversity) Green40 else Lila40
        SplitText(
            modifier = Modifier
                .padding(bottom = 10.dp),
            SpanStyle(activeColor) to stringResource(R.string.active).uppercase(),
            SpanStyle(contractColor) to stringResource(R.string.contract).uppercase(),
            SpanStyle(diversityColor) to stringResource(R.string.diversity).uppercase(),
        )
        Text(
            supplier.name,
            style = MaterialTheme.typography.h1,
            color = MaterialTheme.colors.primary,
            modifier = Modifier
                .padding(bottom = 10.dp),
        )
        Text(
            supplier.location.toString(),
            style = MaterialTheme.typography.subtitle1,
            color = MaterialTheme.colors.secondary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .padding(bottom = 10.dp),
        )
        Text(
            stringResource(R.string.open_po_value, supplier.allPOValue ?: ""),
            style = MaterialTheme.typography.subtitle1,
            color = MaterialTheme.colors.secondary,
            modifier = Modifier
        )
    }
}

@Composable
private fun PoLinesListSimple(
    item: UiPurchaseOrder.Order,
    onPOItemClick: (String) -> Unit,
) {
    Column {
        IconText(
            stringResource(R.string.po_, item.id),
            style = MaterialTheme.typography.h3,
            color = MaterialTheme.colors.primary,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 16.dp, end = 34.dp)
                .clickable { onPOItemClick(item.id) }

        ) {
            Icon(Icons.Filled.Link, "", tint = MaterialTheme.colors.primary)
        }
        C3SimpleCard {
            ConstraintLayout(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                // Create references for the composables to constrain
                val (status, totalCost, openedDate, closedDate) = createRefs()
                Text(
                    item.fulfilledStr,
                    style = MaterialTheme.typography.subtitle1,
                    color = Green40,
                    modifier = Modifier.constrainAs(status) {
                        top.linkTo(parent.top)
                    }
                )
                LabeledValue(
                    label = stringResource(R.string.total_cost),
                    value = item.totalCost,
                    valueStyle = MaterialTheme.typography.h2,
                    modifier = Modifier
                        .constrainAs(totalCost) {
                            top.linkTo(status.bottom, margin = 16.dp)
                            start.linkTo(parent.start)
                            end.linkTo(openedDate.start)
                            width = Dimension.fillToConstraints
                        },
                )
                LabeledValue(
                    label = stringResource(R.string.opened_date),
                    value = item.orderCreationDate,
                    modifier = Modifier
                        .constrainAs(openedDate) {
                            top.linkTo(status.bottom, margin = 16.dp)
                            start.linkTo(totalCost.end, margin = 8.dp)
                            end.linkTo(closedDate.start)
                            width = Dimension.fillToConstraints
                        },
                )
                LabeledValue(
                    label = stringResource(R.string.closed_date),
                    value = item.closedDate,
                    modifier = Modifier
                        .constrainAs(closedDate) {
                            top.linkTo(status.bottom, margin = 16.dp)
                            start.linkTo(openedDate.end, margin = 8.dp)
                            end.linkTo(parent.end)
                            width = Dimension.fillToConstraints
                        },
                )
            }
        }
    }
}

@Composable
private fun ItemsSuppliedList(
    item: UiItem,
    onC3ItemClick: (String) -> Unit,
    onAlertsClick: (String) -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        C3SimpleCard(onClick = { onC3ItemClick(item.id) }) {
            ConstraintLayout(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Create references for the composables to constrain
                val (
                    title,
                    alerts,
                    desc,
                    divider,
                    closedPOIV,
                    openedPOIV,
                    stubPOIV,
                    moq,
                    share,
                    avgUnitPrice,
                ) = createRefs()
                Text(
                    stringResource(R.string.item_, item.id),
                    style = MaterialTheme.typography.subtitle2,
                    color = MaterialTheme.colors.secondary,
                    modifier = Modifier.constrainAs(title) {
                        top.linkTo(parent.top)
                    }
                )
                Text(
                    item.description ?: "",
                    style = MaterialTheme.typography.h3,
                    color = MaterialTheme.colors.primary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .padding(end = 40.dp)
                        .constrainAs(desc) {
                            top.linkTo(title.bottom)
                        }
                )
                C3IconButton(
                    onClick = { onAlertsClick(item.id) },
                    badgeText = item.numberOfActiveAlerts,
                    modifier = Modifier
                        .constrainAs(alerts) {
                            top.linkTo(parent.top)
                            end.linkTo(parent.end)
                        }) {
                    Icon(
                        imageVector = Icons.Filled.Warning,
                        contentDescription = stringResource(R.string.cd_read_more)
                    )
                }
                ListDivider(Modifier.constrainAs(divider) { top.linkTo(desc.bottom) })
                LabeledValue(
                    label = stringResource(R.string.closed_po_item_value),
                    value = "",
                    modifier = Modifier
                        .constrainAs(closedPOIV) {
                            top.linkTo(desc.bottom, margin = 32.dp)
                            start.linkTo(parent.start)
                            end.linkTo(openedPOIV.start)
                            width = Dimension.fillToConstraints
                        },
                )
                LabeledValue(
                    label = stringResource(R.string.open_po_item_value),
                    value = "",
                    modifier = Modifier
                        .constrainAs(openedPOIV) {
                            top.linkTo(desc.bottom, margin = 32.dp)
                            start.linkTo(closedPOIV.end, margin = 12.dp)
                            end.linkTo(stubPOIV.start)
                            width = Dimension.fillToConstraints
                        },
                )
                LabeledValue(
                    label = "",
                    value = "",
                    modifier = Modifier
                        .constrainAs(stubPOIV) {
                            top.linkTo(desc.bottom, margin = 32.dp)
                            start.linkTo(openedPOIV.end, margin = 12.dp)
                            end.linkTo(parent.end)
                            width = Dimension.fillToConstraints
                        },
                )
                LabeledValue(
                    label = stringResource(R.string.moq),
                    value = "",
                    modifier = Modifier
                        .constrainAs(moq) {
                            top.linkTo(closedPOIV.bottom, margin = 20.dp)
                            start.linkTo(parent.start)
                            end.linkTo(share.start)
                            width = Dimension.fillToConstraints
                        },
                )
                LabeledValue(
                    label = stringResource(R.string._share),
                    value = "",
                    modifier = Modifier
                        .constrainAs(share) {
                            top.linkTo(closedPOIV.bottom, margin = 20.dp)
                            start.linkTo(moq.end, margin = 12.dp)
                            end.linkTo(avgUnitPrice.start)
                            width = Dimension.fillToConstraints
                        },
                )
                LabeledValue(
                    label = stringResource(R.string.avg_unit_price),
                    value = item.averageUnitPricePaid,
                    modifier = Modifier
                        .constrainAs(avgUnitPrice) {
                            top.linkTo(closedPOIV.bottom, margin = 20.dp)
                            start.linkTo(share.end, margin = 12.dp)
                            end.linkTo(parent.end)
                            width = Dimension.fillToConstraints
                        },
                )
            }
        }
    }
}

/**
 * TopAppBar for the suppliers details screen[SupplierDetailsScreen]
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun TopAppBar(
    title: String,
    selectedTabIndex: Int,
    searchInput: String,
    placeholderText: String = "",
    onBackButtonClick: () -> Unit,
    onSearchInputChanged: (String) -> Unit = {},
    onClearClick: () -> Unit = {},
    onContactsClick: () -> Unit,
    onSortChanged: (String) -> Unit = {}
) {
    var showClearButton by remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }
    var sortMenuExpanded by remember { mutableStateOf(false) }

    C3TopAppBar(
        title = title,
        onBackButtonClick = onBackButtonClick,
        actions = {
//            OutlinedTextField(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(vertical = 2.dp)
//                    .onFocusChanged { focusState ->
//                        showClearButton = (focusState.isFocused && searchInput.isNotEmpty())
//                    }
//                    .focusRequester(focusRequester),
//                value = searchInput,
//                onValueChange = onSearchInputChanged,
//                placeholder = {
//                    Text(text = placeholderText)
//                },
//                colors = TextFieldDefaults.textFieldColors(
//                    focusedIndicatorColor = Color.Transparent,
//                    unfocusedIndicatorColor = Color.Transparent,
//                    backgroundColor = Color.Transparent,
//                    cursorColor = LocalContentColor.current.copy(alpha = LocalContentAlpha.current)
//                ),
//                trailingIcon = {
//                    AnimatedVisibility(
//                        visible = showClearButton,
//                        enter = fadeIn(),
//                        exit = fadeOut()
//                    ) {
//                        IconButton(onClick = { onClearClick() }) {
//                            Icon(
//                                imageVector = Icons.Filled.Close,
//                                contentDescription = stringResource(R.string.cd_search_clear)
//                            )
//                        }
//
//                    }
//                },
//                maxLines = 1,
//                singleLine = true,
//                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
//                keyboardActions = KeyboardActions(onDone = {
//                    keyboardController?.hide()
//                }),
//            )
            IconButton(onClick = onContactsClick) {
                Icon(
                    imageVector = Icons.Filled.ContactPage,
                    contentDescription = stringResource(R.string.cd_contact_supplier)
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
                val resources = if (selectedTabIndex == 0) listOf(
                    "totalCost.value" to "Total Cost",
                    "unitPrice.value" to "Unit Price",
                    "totalQuantity.value" to "Quantity",
                    "orderCreationDate" to "Opened Date",
                    "closedDate" to "Closed Date",
                    "requestedDeliveryDate" to "Requested Delivery Date",
                    "promisedDeliveryDate" to "Promised Delivery Date",
                    "actualLeadTime" to "Actual Lead Time",
                    "plannedLeadTime" to "Planned Lead Time"
                ) else listOf(
                    "id" to "Item ID",
                    "name" to "Item Name",
                    "openPOValue.value" to "Open PO Line Value",
                    "closedPOValue.value" to "Closed PO Line Value",
                    "shareOpen" to "Share % (Open)",
                    "shareClosed" to "Share % (Closed)",
                    "moq" to "Minimum Order Quantity (MoQ)",
                    "averageUnitPricePaid.value" to "Average Unit Price"
                )

                resources.map { it ->
                    DropdownMenuItem(
                        onClick = {
                            sortMenuExpanded = false
                            onSortChanged(it.first)
                        },
                    ) {
                        Text(
                            it.second,
                            style = MaterialTheme.typography.subtitle1,
                            color = MaterialTheme.colors.secondaryVariant,
                        )
                    }
                }
            }
            IconButton(onClick = { /* TODO: Open search */ }) {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = stringResource(R.string.cd_search_menu)
                )
            }
        }
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Preview
@Composable
fun SupplierDetailsPreview() {
    val supplier = runBlocking {
        (C3MockRepositoryImpl().getSupplierDetails("") as C3Result.Success).data
    }
    C3AppTheme {
        SupplierDetailsScreen(
            scaffoldState = rememberBottomSheetScaffoldState(),
            supplierId = supplier.id,
            uiState = PreviewSupplierDetailsUiState(supplier),
            onRefreshDetails = {},
            onSearchInputChanged = {},
            onTabItemClick = {},
            onExpandableItemClick = {},
            onPOItemClick = {},
            onC3ItemClick = {},
            onAlertsClick = {},
            onSortChanged = {},
            onBackButtonClick = {},
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Preview
@Composable
fun SupplierDetailsItemsSuppliedTabPreview() {
    val supplier = runBlocking {
        (C3MockRepositoryImpl().getSupplierDetails("") as C3Result.Success).data
    }
    C3AppTheme {
        SupplierDetailsScreen(
            scaffoldState = rememberBottomSheetScaffoldState(),
            supplierId = supplier.id,
            uiState = PreviewSupplierDetailsUiState(supplier, 1),
            onRefreshDetails = {},
            onSearchInputChanged = {},
            onTabItemClick = {},
            onExpandableItemClick = {},
            onPOItemClick = {},
            onC3ItemClick = {},
            onAlertsClick = {},
            onSortChanged = {},
            onBackButtonClick = {},
        )
    }
}