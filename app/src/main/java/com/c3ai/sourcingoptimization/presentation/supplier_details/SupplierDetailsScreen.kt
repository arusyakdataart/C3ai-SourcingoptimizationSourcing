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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
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
    onBackButtonClick: () -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                title = stringResource(R.string.supplier_, supplierId),
                searchInput = uiState.searchInput,
                onBackButtonClick = onBackButtonClick,
                onSearchInputChanged = onSearchInputChanged,
                onClearClick = { onSearchInputChanged("") },
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
        sheetContent = { ContactsBottomSheetContent() },
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
                            1 -> uiState.items
                            else -> uiState.poLines
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
                                    PoLinesListExpanded(poLine, onAlertsClick)
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
private fun PoLinesListExpanded(
    item: UiPurchaseOrder.Line,
    onPOAlertsClick: (String) -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
    ) {
        C3SimpleCard(
            backgroundColor = MaterialTheme.colors.surface.copy(alpha = ContentAlpha.medium)
        ) {
            ConstraintLayout(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                // Create references for the composables to constrain
                val (
                    totalCost,
                    alerts,
                    status,
                    openedDate,
                    closedDate,
                    stubDate,
                    leadTime,
                    rDeliveryDate,
                    pDeliveryDate,
                    divider,
                    facility,
                    readMore,
                ) = createRefs()
                LabeledValue(
                    label = stringResource(R.string.po_line_, item.id),
                    value = item.totalCost,
                    valueStyle = MaterialTheme.typography.h1,
                    modifier = Modifier.constrainAs(totalCost) {
                        top.linkTo(parent.top)
                    }
                )
                C3IconButton(
                    onClick = { onPOAlertsClick(item.id) },
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
                SplitText(
                    modifier = Modifier.constrainAs(status) {
                        top.linkTo(totalCost.bottom, margin = 8.dp)
                    },
                    SpanStyle(if (item.fulfilled) Lila40 else Green40) to item.fulfilledStr,
                    null to stringResource(R.string.unit_price_, item.unitPrice),
                    null to stringResource(R.string.quantity_, item.totalQuantity),
                )
                LabeledValue(
                    label = stringResource(R.string.opened_date),
                    value = item.orderCreationDate,
                    modifier = Modifier
                        .constrainAs(openedDate) {
                            top.linkTo(status.bottom, margin = 16.dp)
                            start.linkTo(parent.start)
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
                            end.linkTo(stubDate.start)
                            width = Dimension.fillToConstraints
                        },
                )
                LabeledValue(
                    label = "",
                    value = "",
                    modifier = Modifier
                        .constrainAs(stubDate) {
                            top.linkTo(status.bottom, margin = 16.dp)
                            start.linkTo(closedDate.end, margin = 8.dp)
                            end.linkTo(parent.end)
                            width = Dimension.fillToConstraints
                        },
                )
                Column(modifier = Modifier
                    .constrainAs(leadTime) {
                        top.linkTo(openedDate.bottom, margin = 16.dp)
                        start.linkTo(parent.start)
                        end.linkTo(rDeliveryDate.start)
                        width = Dimension.fillToConstraints
                    }
                ) {
                    Text(
                        stringResource(R.string.lead_time),
                        style = MaterialTheme.typography.h5,
                        color = MaterialTheme.colors.secondary,
                        modifier = Modifier.height(32.dp)
                    )
                    Text(
                        stringResource(R.string.days_actual_, item.actualLeadTime),
                        style = MaterialTheme.typography.subtitle2,
                        color = MaterialTheme.colors.primary,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                    Text(
                        stringResource(R.string.days_plan_, item.requestedLeadTime),
                        style = MaterialTheme.typography.subtitle2,
                        color = MaterialTheme.colors.secondary,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
                LabeledValue(
                    label = stringResource(R.string.requested_delivery_date),
                    value = item.requestedDeliveryDate,
                    modifier = Modifier
                        .constrainAs(rDeliveryDate) {
                            top.linkTo(openedDate.bottom, margin = 16.dp)
                            start.linkTo(leadTime.end, margin = 8.dp)
                            end.linkTo(pDeliveryDate.start)
                            width = Dimension.fillToConstraints
                        },
                )
                LabeledValue(
                    label = stringResource(R.string.promised_delivery_date),
                    value = item.promisedDeliveryDate,
                    modifier = Modifier
                        .constrainAs(pDeliveryDate) {
                            top.linkTo(openedDate.bottom, margin = 16.dp)
                            start.linkTo(rDeliveryDate.end, margin = 8.dp)
                            end.linkTo(parent.end)
                            width = Dimension.fillToConstraints
                        },
                )
                ListDivider(Modifier.constrainAs(divider) { top.linkTo(leadTime.bottom) })
                BusinessCard(
                    label = stringResource(R.string.delivery_facility),
                    title = item.order?.to?.name ?: "",
                    subtitle = "",
                    modifier = Modifier
                        .constrainAs(facility) {
                            top.linkTo(divider.bottom)
                        }
                )
                var expanded by remember { mutableStateOf(false) }
                IconButton(
                    onClick = { expanded = true },
                    Modifier
                        .size(40.dp)
                        .constrainAs(readMore) {
                            top.linkTo(facility.bottom)
                            end.linkTo(parent.end)
                        }
                ) {
                    Icon(
                        imageVector = Icons.Filled.ReadMore,
                        contentDescription = stringResource(R.string.cd_read_more)
                    )
                    PoLinesListReadMore(item = item, expanded = expanded) {
                        expanded = false
                    }
                }
            }
        }
    }
}

@Composable
private fun PoLinesListReadMore(
    item: UiPurchaseOrder.Line,
    expanded: Boolean,
    onDismissRequest: () -> Unit,
) {
    DropdownMenu(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .width(300.dp),
        expanded = expanded,
        onDismissRequest = onDismissRequest
    ) {
        item.order?.buyer?.let { buyer ->
            DropdownMenuItem(
                onClick = {},
            ) {
                BusinessCard(
                    label = stringResource(R.string.buyer_, buyer.id),
                    title = buyer.name,
                    subtitle = "",
                )
            }
            ListDivider()
        }
        item.order?.vendor?.let { vendor ->
            DropdownMenuItem(
                onClick = {},
            ) {
                BusinessCard(
                    label = stringResource(R.string.supplier_, vendor.id),
                    title = vendor.name,
                    subtitle = vendor.location.toString(),
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

@Composable
private fun ContactsBottomSheetContent() {
    BottomSheetContent(
        BottomSheetItem(
            image = Icons.Filled.Call,
            contentDescription = stringResource(R.string.cd_make_call),
            text = stringResource(R.string.make_call),
        ),
        BottomSheetItem(
            image = Icons.Filled.Sms,
            contentDescription = stringResource(R.string.cd_send_sms),
            text = stringResource(R.string.send_sms),
        ),
        BottomSheetItem(
            image = Icons.Filled.Email,
            contentDescription = stringResource(R.string.cd_send_email),
            text = stringResource(R.string.send_email),
        ),
    )
}

/**
 * TopAppBar for the suppliers details screen[SupplierDetailsScreen]
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun TopAppBar(
    title: String,
    searchInput: String,
    placeholderText: String = "",
    onBackButtonClick: () -> Unit,
    onSearchInputChanged: (String) -> Unit = {},
    onClearClick: () -> Unit = {},
    onContactsClick: () -> Unit,
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
                stringArrayResource(R.array.sort_po).map { title ->
                    DropdownMenuItem(
                        onClick = {},
                    ) {
                        Text(
                            title,
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
            onBackButtonClick = {},
        )
    }
}