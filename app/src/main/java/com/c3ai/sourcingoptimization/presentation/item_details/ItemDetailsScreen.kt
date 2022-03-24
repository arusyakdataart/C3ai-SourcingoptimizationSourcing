package com.c3ai.sourcingoptimization.presentation.item_details

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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.c3ai.sourcingoptimization.R
import com.c3ai.sourcingoptimization.common.SortType
import com.c3ai.sourcingoptimization.common.components.*
import com.c3ai.sourcingoptimization.data.C3Result
import com.c3ai.sourcingoptimization.data.repository.C3MockRepositoryImpl
import com.c3ai.sourcingoptimization.presentation.item_details.overview.ItemDetailsUiState
import com.c3ai.sourcingoptimization.presentation.item_details.overview.PreviewItemDetailsUiState
import com.c3ai.sourcingoptimization.presentation.supplier_details.SupplierDetailsUiState
import com.c3ai.sourcingoptimization.presentation.views.UiItem
import com.c3ai.sourcingoptimization.presentation.views.UiPurchaseOrder
import com.c3ai.sourcingoptimization.ui.theme.*
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
    uiState: ItemDetailsUiState,
    onRefreshDetails: () -> Unit,
    onTabItemClick: (Int) -> Unit,
    onBackButtonClick: () -> Unit,
    loadData: (itemId: String) -> Unit,
    onAlertsClick: (String) -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    val bottomState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)

    var phoneNumber: String by remember {
        mutableStateOf("")
    }

    var emailAddress: String by remember {
        mutableStateOf("")
    }

    LaunchedEffect(itemId) {
        loadData(itemId)
    }

    ModalBottomSheetLayout(
        sheetState = bottomState,
        sheetContent = {
            ContactSupplierBottomSheetContent(phoneNumber, emailAddress)
        }
    ) {
        Scaffold(
            scaffoldState = scaffoldState,
            topBar = {
                TopAppBar(
                    title = stringResource(R.string.item_, itemId),
                    onBackButtonClick = onBackButtonClick,
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
                    when (uiState) {
                        is ItemDetailsUiState.HasItem -> {
                            Column {
                                Tabs(
                                    selectedTab = uiState.tabIndex,
                                    TabItem(stringResource(R.string.overview)) {
                                        onTabItemClick(0)
                                    },
                                    TabItem(stringResource(R.string.po_lines)) {
                                        onTabItemClick(1)
                                    }
                                )
                                when(uiState.tabIndex) {
                                    0 -> {
                                        Column {
                                            ItemDetailsInfo(uiState, onAlertsClick)
                                        }

                                    }
                                    1 -> {

                                    }
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

@Composable
private fun ItemDetailsInfo(
    uiState: ItemDetailsUiState.HasItem,
    onAlertsClick: (String) -> Unit,
) {
    val item = uiState.item
    C3SimpleCard {
        ConstraintLayout(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Create references for the composables to constrain
            val (
                alerts,
                desc,
                divider,
                savingOpportunity,
                suppliersWithContract,
                openedPOIV,
                stubPOIV,
                moq,
                share,
                avgUnitPrice,
            ) = createRefs()
            Text(
                (item.name ?: "") + (item.description ?: ""),
                style = MaterialTheme.typography.h3,
                color = MaterialTheme.colors.primary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .padding(end = 40.dp)
                    .constrainAs(desc) {
                        top.linkTo(parent.top)
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
            LabeledValue(
                label = stringResource(R.string.saving_opportunity),
                value = "",
                modifier = Modifier
                    .constrainAs(savingOpportunity) {
                        top.linkTo(desc.bottom, margin = 16.dp)
                        start.linkTo(parent.start)
                        end.linkTo(openedPOIV.start)
                        width = Dimension.fillToConstraints
                    },
            )
            LabeledValue(
                label = stringResource(R.string.suppliers_with_contract),
                value = "",
                modifier = Modifier
                    .width(80.dp)
                    .constrainAs(suppliersWithContract) {
                        top.linkTo(desc.bottom, margin = 16.dp)
                        end.linkTo(parent.end)
                    },
            )
            ListDivider(Modifier.constrainAs(divider) { top.linkTo(desc.bottom) })
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

/**
 * TopAppBar for the suppliers details screen[ItemDetailsScreen]
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun TopAppBar(
    title: String,
    onBackButtonClick: () -> Unit,
) {
    C3TopAppBar(
        title = title,
        onBackButtonClick = onBackButtonClick,
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
            uiState = PreviewItemDetailsUiState(item),
            onRefreshDetails = {},
            onTabItemClick = {},
            onBackButtonClick = {},
            loadData = {},
            onAlertsClick = {},
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
            uiState = PreviewItemDetailsUiState(item, 1),
            onRefreshDetails = {},
            onTabItemClick = {},
            onBackButtonClick = {},
            loadData = {},
            onAlertsClick = {},
        )
    }
}