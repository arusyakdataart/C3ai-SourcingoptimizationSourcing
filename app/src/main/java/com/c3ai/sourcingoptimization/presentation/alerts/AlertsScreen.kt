package com.c3ai.sourcingoptimization.presentation.alerts

import android.annotation.SuppressLint
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.c3ai.sourcingoptimization.R
import com.c3ai.sourcingoptimization.common.SortType
import com.c3ai.sourcingoptimization.common.components.*
import com.c3ai.sourcingoptimization.presentation.views.UiAlert
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
    onPOClick: (String) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val bottomState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)

    var phoneNumber: String by remember {
        mutableStateOf("")
    }

    var emailAddress: String by remember {
        mutableStateOf("")
    }

    if (selectedCategories != null) {
        viewModel.onEvent(AlertsEvent.OnFilterChanged(selectedCategories))
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
                    val listState = rememberLazyListState()
                    LazyColumn(modifier = Modifier.fillMaxSize(), listState) {
                        when (uiState) {
                            is AlertsUiState.HasData -> {
                                val categoryList =
                                    uiState.filteredAlerts.groupBy { it.category?.name }
                                categoryList.keys.forEach {
                                    stickyHeader {
                                        val collapsableItemIds =
                                            uiState.filteredAlerts.mapNotNull { alert -> if (alert.category?.name == it) alert.id else null }
                                        val expanded =
                                            !uiState.collapsedListItemIds.contains(
                                                collapsableItemIds[0]
                                            )

                                        AlertCategoryScreen(
                                            it ?: "",
                                            expanded,
                                            collapsableItemIds,
                                            onCollapsableItemClick
                                        )
                                    }

                                    items(categoryList.getValue(it)) {
                                        phoneNumber = it.supplierContract?.phone ?: ""
                                        emailAddress = it.supplierContract?.email ?: ""
                                        if (it.readStatus != "Read") {
                                            it.readStatus = "Read"
                                            updateReadStatus(it.id, viewModel)
                                        }

                                        CollapsableLayout(
                                            expanded = !uiState.collapsedListItemIds.contains(
                                                it.id
                                            ),
                                        ) {
                                            RapidRatingsRiskAlert(
                                                it,
                                                {
                                                    updateFlaggedStatus(
                                                        it,
                                                        viewModel,
                                                        !(it.flagged ?: false)
                                                    )
                                                },
                                                { updateFeedbackHelpful(it, viewModel, true) },
                                                { updateFeedbackHelpful(it, viewModel, false) },
                                                {
                                                    updateDetailReadStatus(it.id, viewModel)
                                                    val id = it.redirectUrl?.substring(
                                                        it.redirectUrl.lastIndexOf("/") + 1
                                                    ) ?: ""
                                                    when (it.alertType) {
                                                        "Supplier" -> onSupplierClick(id)
                                                        "Item" -> onItemClick(id)
                                                        else -> onPOClick(id)
                                                    }
                                                },
                                                {
                                                    coroutineScope.launch {
                                                        if (!bottomState.isVisible) {
                                                            bottomState.show()
                                                        }
                                                    }
                                                }
                                            )
                                        }
//                                        when (it.category?.name) {
//                                            AlertTypes.NEW_LOWEST_PRICE.categoryName ->
//                                                CollapsableLayout(
//                                                    expanded = !uiState.collapsedListItemIds.contains(
//                                                        it.id
//                                                    ),
//                                                ) {
//                                                    PriceChangeAlert(
//                                                        it,
//                                                        {
//                                                            changeFeedbackHelpful(
//                                                                it,
//                                                                viewModel,
//                                                                true
//                                                            )
//                                                        },
//                                                        {
//                                                            changeFeedbackHelpful(
//                                                                it,
//                                                                viewModel,
//                                                                false
//                                                            )
//                                                        }
//                                                    )
//                                                }
//
//                                            AlertTypes.UNEXPECTED_PRICE_INCREASE.categoryName -> CollapsableLayout(
//                                                expanded = !uiState.collapsedListItemIds.contains(
//                                                    it.id
//                                                ),
//                                            ) {
//                                                PriceChangeAlert(
//                                                    it,
//                                                    { changeFeedbackHelpful(it, viewModel, true) },
//                                                    { changeFeedbackHelpful(it, viewModel, false) }
//                                                )
//                                            }
//                                            AlertTypes.REQUESTED_DELIVERY_DATE_CHANGE.categoryName -> CollapsableLayout(
//                                                expanded = !uiState.collapsedListItemIds.contains(
//                                                    it.id
//                                                ),
//                                            ) {
//                                                RequestedDeliveryDateChangeAlert(
//                                                    it,
//                                                    { changeFeedbackHelpful(it, viewModel, true) },
//                                                    { changeFeedbackHelpful(it, viewModel, false) }
//                                                )
//                                            }
//                                            AlertTypes.SHORT_CYCLED_PURCHASE_ORDER.categoryName -> CollapsableLayout(
//                                                expanded = !uiState.collapsedListItemIds.contains(
//                                                    it.id
//                                                ),
//                                            ) {
//                                                PurchaseOrderAlert(
//                                                    it,
//                                                    { changeFeedbackHelpful(it, viewModel, true) },
//                                                    { changeFeedbackHelpful(it, viewModel, false) }
//                                                )
//                                            }
//                                            AlertTypes.INDEX_PRICE_CHANGE.categoryName -> CollapsableLayout(
//                                                expanded = !uiState.collapsedListItemIds.contains(
//                                                    it.id
//                                                ),
//                                            ) {
//                                                IndexPriceChangeAlert(
//                                                    it,
//                                                    { changeFeedbackHelpful(it, viewModel, true) },
//                                                    { changeFeedbackHelpful(it, viewModel, false) }
//                                                )
//                                            }
//
//                                            AlertTypes.CORRELATED_INDEX_PRICING_ANOMALY.categoryName -> CollapsableLayout(
//                                                expanded = !uiState.collapsedListItemIds.contains(
//                                                    it.id
//                                                ),
//                                            ) {
//                                                IndexPriceAnomalyAlert(
//                                                    it,
//                                                    { changeFeedbackHelpful(it, viewModel, true) },
//                                                    { changeFeedbackHelpful(it, viewModel, false) }
//                                                )
//                                            }
//                                            AlertTypes.D_U_N_S_RISK.categoryName -> CollapsableLayout(
//                                                expanded = !uiState.collapsedListItemIds.contains(
//                                                    it.id
//                                                ),
//                                            ) {
//                                                DUNSRiskAlert(
//                                                    it,
//                                                    { changeFeedbackHelpful(it, viewModel, true) },
//                                                    { changeFeedbackHelpful(it, viewModel, false) }
//                                                )
//                                            }
//                                            AlertTypes.RAPID_RATINGS_RISK.categoryName -> CollapsableLayout(
//                                                expanded = !uiState.collapsedListItemIds.contains(
//                                                    it.id
//                                                ),
//                                            ) {
//                                                RapidRatingsRiskAlert(
//                                                    it,
//                                                    { changeFeedbackHelpful(it, viewModel, true) },
//                                                    { changeFeedbackHelpful(it, viewModel, false) })
//                                            }
//                                        }
                                    }
                                }
                            }
                            is AlertsUiState.NoData -> {
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
}

private fun updateFeedbackHelpful(alert: UiAlert, viewModel: AlertsViewModel, helpful: Boolean) {
    if (alert.feedback?.helpful == helpful) {
        return
    }
    viewModel.updateAlerts(listOf(alert.id), "feedback", helpful)
    viewModel.onEvent(AlertsEvent.OnFeedbackChanged(alert.id, helpful))
}

private fun updateFlaggedStatus(alert: UiAlert, viewModel: AlertsViewModel, flagged: Boolean) {
    viewModel.updateAlerts(listOf(alert.id), "flagged", flagged)
    viewModel.onEvent(AlertsEvent.OnFlaggedChanged(alert.id, flagged))
}

private fun updateReadStatus(alertId: String, viewModel: AlertsViewModel) {
    viewModel.updateAlerts(listOf(alertId), "read", null)
}

private fun updateDetailReadStatus(alertId: String, viewModel: AlertsViewModel) {
    viewModel.updateAlerts(listOf(alertId), "detailRead", null)
}

@SuppressLint("UnusedTransitionTargetStateParameter")
@Composable
private fun AlertCategoryScreen(
    category: String,
    expanded: Boolean,
    expandableIds: List<String>,
    onCollapsableItemClick: (String) -> Unit
) {
    val transitionState = remember {
        MutableTransitionState(expanded).apply {
            targetState = !expanded
        }
    }
    val transition = updateTransition(transitionState, label = "")

    val arrowRotationDegree by transition.animateFloat({
        tween(durationMillis = 300)
    }, label = "ArrowRotationAnimation") {
        if (expanded) 0f else 270f
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, top = 0.dp, end = 16.dp, bottom = 0.dp)
            .background(BackgroundColor)
            .clickable {
                expandableIds.forEach {
                    onCollapsableItemClick(it)
                }
            },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = category,
            style = MaterialTheme.typography.h3,
            color = MaterialTheme.colors.primary
        )
        IconButton(
            onClick = {
                expandableIds.forEach {
                    onCollapsableItemClick(it)
                }
            },
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_baseline_keyboard_arrow_down_24),
                contentDescription = "Collapse",
                modifier = Modifier.rotate(arrowRotationDegree),
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