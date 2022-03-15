package com.c3ai.sourcingoptimization.presentation.po_details

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.c3ai.sourcingoptimization.R
import com.c3ai.sourcingoptimization.common.BottomSheetType
import com.c3ai.sourcingoptimization.common.components.*
import com.c3ai.sourcingoptimization.data.C3Result
import com.c3ai.sourcingoptimization.data.repository.C3MockRepositoryImpl
import com.c3ai.sourcingoptimization.ui.theme.C3AppTheme
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

/**
 * A display of the purchase order details screen that has the lists.
 *
 * This sets up the scaffold with the top app bar, and surrounds the content with refresh,
 * loading and error handling.
 *
 * This helper function exists because [PODetailsScreen] is big and have two states,
 * so we need to decompose it with additional function [PODetailsDataScreen].
 */

@ExperimentalMaterialApi
@OptIn(ExperimentalComposeUiApi::class, ExperimentalAnimationApi::class)
@Composable
fun PODetailsScreen(
    scaffoldState: ScaffoldState,
    orderId: String,
    uiState: PODetailsUiState,
    onRefreshDetails: () -> Unit,
    onSearchInputChanged: (String) -> Unit,
    onSortChanged: (String) -> Unit,
    onBackButtonClick: () -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    val bottomState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)

    var currentBottomSheet: BottomSheetType by remember {
        mutableStateOf(BottomSheetType.CONTACT_SUPPLIER)
    }

    var phoneNumber: String by remember {
        mutableStateOf("")
    }

    var emailAddress: String by remember {
        mutableStateOf("")
    }

    ModalBottomSheetLayout(
        sheetState = bottomState,
        sheetContent = {
            currentBottomSheet.let {
                SheetLayout(bottomSheetType = it, phoneNumber, emailAddress)
            }
        }
    ) {
        Scaffold(
            scaffoldState = scaffoldState,
            snackbarHost = { C3SnackbarHost(hostState = it) },
            topBar = {
                PODetailsAppBar(
                    title = stringResource(R.string.po_, orderId),
                    searchInput = uiState.searchInput,
                    onBackButtonClick = onBackButtonClick,
                    onSearchInputChanged = onSearchInputChanged,
                    onSortChanged = onSortChanged,
                    onClearClick = { onSearchInputChanged("") }
                )
            },
        ) { innerPadding ->
            val contentModifier = Modifier.padding(innerPadding)

            LoadingContent(
                empty = when (uiState) {
                    is PODetailsUiState.HasDetails -> false
                    is PODetailsUiState.NoDetails -> uiState.isLoading
                },
                emptyContent = { FullScreenLoading() },
                loading = uiState.isLoading,
                onRefresh = onRefreshDetails,
                content = {
                    val listState = rememberLazyListState()
                    LazyColumn(modifier = Modifier.fillMaxSize(), listState) {
                        when (uiState) {
                            is PODetailsUiState.HasDetails -> {
                                item("PO Detail") {
                                    val item = uiState.order

                                    PODetailsDataScreen(
                                        uiState = uiState,
                                        onContactBuyerClick = {
                                            currentBottomSheet =
                                                BottomSheetType.CONTACT_BUYER
                                            phoneNumber =
                                                item.buyerContact?.preferredPhoneNumber?.number
                                                    ?: ""
                                            emailAddress =
                                                item.buyerContact?.preferredEmail?.communicationIdentifier
                                                    ?: ""
                                            coroutineScope.launch { bottomState.show() }
                                        },
                                        onContactSupplierClick = {
                                            currentBottomSheet =
                                                BottomSheetType.CONTACT_SUPPLIER
                                            phoneNumber =
                                                item.vendorContact?.preferredPhoneNumber?.number
                                                    ?: ""
                                            emailAddress =
                                                item.vendorContact?.preferredEmail?.communicationIdentifier
                                                    ?: ""
                                            coroutineScope.launch { bottomState.show() }
                                        })
                                }
                                item("PO Lines Header") {
                                    POLinesHeaderScreen(
                                        uiState = uiState,
                                    )
                                }
                                items(uiState.poLines) {
                                    PoLinesListExpanded(it, padding = 16.dp, onPOAlertsClick = { })
                                }
                            }
                            is PODetailsUiState.NoDetails -> {
                                item("") {
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
                    }
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
            }
            // Once the message is displayed and dismissed, notify the ViewModel
            onErrorDismissState()
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class, ExperimentalAnimationApi::class)
@Composable
private fun POLinesHeaderScreen(
    uiState: PODetailsUiState.HasDetails,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 8.dp)
    ) {
        Text(
            text = stringResource(R.string.po_lines_, uiState.poLines.size),
            style = MaterialTheme.typography.h3,
            color = MaterialTheme.colors.primary
        )
    }
}

/**
 * TopAppBar for the po details screen[PODetailsScreen]
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun PODetailsAppBar(
    title: String,
    searchInput: String,
    placeholderText: String = "",
    onBackButtonClick: () -> Unit,
    onSearchInputChanged: (String) -> Unit = {},
    onSortChanged: (String) -> Unit = {},
    onClearClick: () -> Unit = {}
) {
    var showClearButton by remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }
    var sortMenuExpanded by remember { mutableStateOf(false) }

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
                    "totalCost.value" to "PO Line Value",
                    "unitPrice.value" to "Unit Price",
                    "totalQuantity.value" to "Quantity",
                    "orderCreationDate" to "Opened Date",
                    "closedDate" to "Closed Date",
                    "requestedDeliveryDate" to "Requested Delivery Date",
                    "promisedDeliveryDate" to "Promised Delivery Date",
                    "actualLeadTime" to "Actual Lead Time",
                    "plannedLeadTime" to "Planned Lead Time"
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
        }
    )
}

@Composable
fun SheetLayout(
    bottomSheetType: BottomSheetType,
    phoneNumber: String,
    email: String
) {

    when (bottomSheetType) {
        BottomSheetType.CONTACT_SUPPLIER -> ContactSupplierBottomSheetContent(phoneNumber, email)
        BottomSheetType.CONTACT_BUYER -> ContactBuyerBottomSheetContent(phoneNumber, email)
    }
}

@ExperimentalMaterialApi
@Preview
@Composable
fun ComposablePreview() {
    val order = runBlocking {
        (C3MockRepositoryImpl().getPODetails("") as C3Result.Success).data
    }
    C3AppTheme {
        PODetailsScreen(
            scaffoldState = rememberScaffoldState(),
            orderId = order.id,
            uiState = PreviewPODetailsUiState(order),
            onRefreshDetails = {},
            onSearchInputChanged = {},
            onSortChanged = {},
            onBackButtonClick = {},
        )
    }
}