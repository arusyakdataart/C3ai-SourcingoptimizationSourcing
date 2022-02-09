package com.c3ai.sourcingoptimization.presentation.supplier_details

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.c3ai.sourcingoptimization.R
import com.c3ai.sourcingoptimization.common.components.*
import com.c3ai.sourcingoptimization.data.C3Result
import com.c3ai.sourcingoptimization.data.repository.C3MockRepositoryImpl
import com.c3ai.sourcingoptimization.domain.settings.FakeC3AppSettingsProvider
import com.c3ai.sourcingoptimization.presentation.models.UiPurchaseOrder
import com.c3ai.sourcingoptimization.ui.theme.C3AppTheme
import com.c3ai.sourcingoptimization.ui.theme.Green40
import com.c3ai.sourcingoptimization.ui.theme.Lila40
import kotlinx.coroutines.runBlocking
import java.util.*

/**
 * A display of the supplier details screen that has the lists.
 *
 * This sets up the scaffold with the top app bar, and surrounds the content with refresh,
 * loading and error handling.
 *
 * This helper function exists because [SupplierDetailsScreen] is big and have two states,
 * so we need to decompose it with additional function [SupplierDetailsDataScreen].
 */
@OptIn(ExperimentalComposeUiApi::class, ExperimentalAnimationApi::class)
@Composable
fun SupplierDetailsScreen(
    navController: NavController,
    scaffoldState: ScaffoldState,
    supplierId: String,
    uiState: SupplierDetailsUiState,
    onRefreshDetails: () -> Unit,
    onSearchInputChanged: (String) -> Unit,
    onExpandableItemClick: (String) -> Unit,
) {
    Scaffold(
        scaffoldState = scaffoldState,
        snackbarHost = { C3SnackbarHost(hostState = it) },
        topBar = {
            SuppliersDetailsAppBar(
                navController,
                title = stringResource(R.string.supplier_, supplierId),
                searchInput = uiState.searchInput,
                onSearchInputChanged = onSearchInputChanged,
                onClearClick = { onSearchInputChanged("") }
            )
        },
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
                    is SupplierDetailsUiState.HasDetails -> SupplierDetailsDataScreen(
                        uiState = uiState,
                        onExpandableItemClick = onExpandableItemClick,
                    )
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

@OptIn(ExperimentalComposeUiApi::class, ExperimentalAnimationApi::class)
@Composable
private fun SupplierDetailsDataScreen(
    uiState: SupplierDetailsUiState.HasDetails,
    onExpandableItemClick: (String) -> Unit,
) {
    CollapsingContentList(
        contentModifier = Modifier
            .height(212.dp),
        items = uiState.poLines,
        content = { SuppliersDetailsInfo(uiState) }
    ) { item ->
        ExpandableLayout(
            expanded = !uiState.expandedListItemIds.contains(item.id),
            onClick = { onExpandableItemClick(item.id) },
            content = { PoLinesListSimple(item) },
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .background(MaterialTheme.colors.background)
        ) {
            item.orderLines.map { poLine ->
                PoLinesListExpanded(poLine)
            }
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
        val activeColor = if (supplier.active == true) Green40 else Lila40
        val contractColor = if (supplier.hasActiveContracts == true) Green40 else Lila40
        val diversityColor = if (supplier.diversity == true) Green40 else Lila40
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
            stringResource(R.string.open_po_value, supplier.allPOValue),
            style = MaterialTheme.typography.subtitle1,
            color = MaterialTheme.colors.secondary,
            modifier = Modifier
                .padding(bottom = 10.dp),
        )
        PButton(
            text = stringResource(R.string.contact_supplier),
            onClick = {}
        )
    }
}

@Composable
private fun LabeledValue(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    valueStyle: TextStyle = MaterialTheme.typography.subtitle2,
    valueColor: Color = MaterialTheme.colors.primary,
) {
    Column(modifier = modifier) {
        Text(label, style = MaterialTheme.typography.h4, color = MaterialTheme.colors.secondary)
        Text(value, style = valueStyle, color = valueColor, modifier = Modifier.padding(top = 4.dp))
    }
}

@Composable
private fun PoLinesListSimple(
    item: UiPurchaseOrder.Order,
) {
    Column {
        IconText(
            item.id,
            style = MaterialTheme.typography.h3,
            color = MaterialTheme.colors.primary,
            modifier = Modifier.padding(bottom = 16.dp)
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
) {
    Box(
        modifier = Modifier
            .padding(top = 16.dp)
            .fillMaxWidth()
    ) {
        C3SimpleCard {
            ConstraintLayout(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                // Create references for the composables to constrain
                val (
                    title,
                    totalCost,
                    status,
                    openedDate,
                    closedDate,
                ) = createRefs()
                LabeledValue(
                    label = stringResource(R.string.po_line_, item.id),
                    value = item.totalCost,
                    valueStyle = MaterialTheme.typography.h1,
                    modifier = Modifier.constrainAs(title) {
                        top.linkTo(parent.top)
                    }
                )
                SplitText(
                    modifier = Modifier.constrainAs(status) {
                        top.linkTo(title.bottom, margin = 8.dp)
                    },
                    SpanStyle(if (item.fulfilled) Lila40 else Green40) to item.fulfilledStr,
                    null to stringResource(R.string.unit_price_, item.unitPrice),
                    null to stringResource(R.string.quantity_, item.totalQuantity),
                )
                LabeledValue(
                    label = stringResource(R.string.opened_date),
                    value = item.orderCreationDate,
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

/**
 * TopAppBar for the suppliers details screen[SupplierDetailsScreen]
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun SuppliersDetailsAppBar(
    navController: NavController,
    title: String,
    searchInput: String,
    placeholderText: String = "",
    onSearchInputChanged: (String) -> Unit = {},
    onClearClick: () -> Unit = {},
) {
    var showClearButton by remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }

    C3AppBar(
        navController = navController,
        title = title,
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
            IconButton(onClick = { /* TODO: Open search */ }) {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = stringResource(R.string.cd_search_menu)
                )
            }
            IconButton(onClick = { /* TODO: Open sort */ }) {
                Icon(
                    imageVector = Icons.Filled.Sort,
                    contentDescription = stringResource(R.string.cd_sort_menu)
                )
            }
        }
    )
}

@Preview
@Composable
fun ComposablePreview() {
    val supplier = runBlocking {
        (C3MockRepositoryImpl().getSupplierDetails("") as C3Result.Success).data
    }
    C3AppTheme {
        SupplierDetailsScreen(
            navController = rememberNavController(),
            scaffoldState = rememberScaffoldState(),
            supplierId = supplier.id,
            uiState = SupplierDetailsViewModelState(
                settings = FakeC3AppSettingsProvider(),
                supplier = supplier,
                isLoading = false,
                errorMessages = emptyList(),
                searchInput = ""
            ).toUiState(),
            onRefreshDetails = {},
            onSearchInputChanged = {},
            onExpandableItemClick = {},
        )
    }
}