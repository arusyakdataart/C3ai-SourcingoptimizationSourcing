package com.c3ai.sourcingoptimization.presentation.alerts

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.navigation.NavController
import com.c3ai.sourcingoptimization.R
import com.c3ai.sourcingoptimization.common.AlertTypes
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

@RequiresApi(Build.VERSION_CODES.N)
@ExperimentalMaterialApi
@ExperimentalFoundationApi
@Composable
fun AlertsScreen(
    scaffoldState: ScaffoldState,
    uiState: AlertsUiState,
    onRefreshDetails: () -> Unit,
    onSearchInputChanged: (String) -> Unit,
    onSortChanged: (String) -> Unit,
    onChangeFilter: (String) -> Unit,
    onBackButtonClick: () -> Unit,
    onCollapsableItemClick: (String) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val bottomState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)

    var phoneNumber: String by remember {
        mutableStateOf("")
    }

    var emailAddress: String by remember {
        mutableStateOf("")
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
                                val categoryList = uiState.alerts.groupBy { it.category?.name }
                                categoryList.forEach { it, it1 ->
                                    stickyHeader {
                                        val collapsableItemIds =
                                            uiState.alerts.mapNotNull { alert -> if (alert.category?.name == it) alert.id else null }
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

                                    items(it1) {
                                        when (it.category?.name) {
                                            AlertTypes.NEW_LOWEST_PRICE.categoryName ->
                                                CollapsableLayout(
                                                    expanded = !uiState.collapsedListItemIds.contains(
                                                        it.id
                                                    ),
                                                ) {
                                                    PriceChangeAlert(it)
                                                }

                                            AlertTypes.UNEXPECTED_PRICE_INCREASE.categoryName -> CollapsableLayout(
                                                expanded = !uiState.collapsedListItemIds.contains(
                                                    it.id
                                                ),
                                            ) {
                                                PriceChangeAlert(it)
                                            }
                                            AlertTypes.REQUESTED_DELIVERY_DATE_CHANGE.categoryName -> CollapsableLayout(
                                                expanded = !uiState.collapsedListItemIds.contains(
                                                    it.id
                                                ),
                                            ) {
                                                RequestedDeliveryDateChangeAlert(
                                                    it
                                                )
                                            }
                                            AlertTypes.SHORT_CYCLED_PURCHASE_ORDER.categoryName -> CollapsableLayout(
                                                expanded = !uiState.collapsedListItemIds.contains(
                                                    it.id
                                                ),
                                            ) {
                                                PurchaseOrderAlert(
                                                    it
                                                )
                                            }
                                            AlertTypes.INDEX_PRICE_CHANGE.categoryName -> CollapsableLayout(
                                                expanded = !uiState.collapsedListItemIds.contains(
                                                    it.id
                                                ),
                                            ) {
                                                IndexPriceChangeAlert(
                                                    it
                                                )
                                            }

                                            AlertTypes.CORRELATED_INDEX_PRICING_ANOMALY.categoryName -> CollapsableLayout(
                                                expanded = !uiState.collapsedListItemIds.contains(
                                                    it.id
                                                ),
                                            ) {
                                                IndexPriceAnomalyAlert(
                                                    it
                                                )
                                            }
                                            AlertTypes.D_U_N_S_RISK.categoryName -> CollapsableLayout(
                                                expanded = !uiState.collapsedListItemIds.contains(
                                                    it.id
                                                ),
                                            ) {
                                                DUNSRiskAlert(it)
                                            }
                                            AlertTypes.RAPID_RATINGS_RISK.categoryName -> CollapsableLayout(
                                                expanded = !uiState.collapsedListItemIds.contains(
                                                    it.id
                                                ),
                                            ) {
                                                RapidRatingsRiskAlert(
                                                    it
                                                )
                                            }
                                        }
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
                //viewModel.onEvent(ToggleOrderSection)
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

@Composable
private fun PriceChangeAlert(alert: UiAlert) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 16.dp)
    ) {
        C3SimpleCard {
            ConstraintLayout(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                val (description, flag, status, savings, divider1, oldPrice, newPrice, priceChange, divider2, helpful, notHelpful) = createRefs()
                Text(
                    alert.description,
                    style = MaterialTheme.typography.h3,
                    color = MaterialTheme.colors.primary,
                    modifier = Modifier
                        .constrainAs(description) {
                            top.linkTo(parent.top)
                            start.linkTo(parent.start)
                            end.linkTo(flag.start)
                        }
                        .padding(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 0.dp)
                )
                C3IconButton(
                    onClick = { },
                    modifier = Modifier
                        .constrainAs(flag) {
                            top.linkTo(parent.top)
                            end.linkTo(parent.end)
                        }) {
                    Icon(
                        painter = painterResource(id = R.drawable.flag),
                        tint = MaterialTheme.colors.onBackground,
                        contentDescription = "flag"
                    )
                }
                SplitText(
                    modifier = Modifier.constrainAs(status) {
                        top.linkTo(description.bottom, margin = 8.dp)
                    },
                    SpanStyle(Orange) to (alert.currentState?.name ?: ""),
                    null to (alert.timestamp ?: ""),
                )
                SplitText(
                    modifier = Modifier.constrainAs(savings) {
                        top.linkTo(status.bottom, margin = 0.dp)
                    },
                    // TODO!!! not clear which is savings opp from api data.
                    SpanStyle(Lila40) to stringResource(id = R.string.savings_opportunity),
                    null to "-",
                )
                ListDivider(Modifier.constrainAs(divider1) { top.linkTo(savings.bottom) })
                LabeledValue(
                    label = stringResource(R.string.old_lowest_price),
                    value = "-",
                    modifier = Modifier
                        .constrainAs(oldPrice) {
                            top.linkTo(divider1.bottom)
                            start.linkTo(parent.start)
                            end.linkTo(newPrice.start)
                            width = Dimension.fillToConstraints
                        },
                )
                LabeledValue(
                    label = stringResource(R.string.new_lowest_price),
                    value = "-", // TODO!!! not clear which api data.
                    modifier = Modifier
                        .constrainAs(newPrice) {
                            top.linkTo(divider1.bottom)
                            start.linkTo(oldPrice.end, margin = 8.dp)
                            end.linkTo(priceChange.start)
                            width = Dimension.fillToConstraints
                        },
                )
                LabeledValue(
                    label = stringResource(R.string.price_change),
                    value = "-\n(-)", // TODO!!! not clear which api data.
                    modifier = Modifier
                        .constrainAs(priceChange) {
                            top.linkTo(divider1.bottom)
                            start.linkTo(newPrice.end, margin = 8.dp)
                            end.linkTo(parent.end)
                            width = Dimension.fillToConstraints
                        },
                )
                ListDivider(Modifier.constrainAs(divider2) { top.linkTo(priceChange.bottom) })
                Row(
                    modifier = Modifier
                        .constrainAs(helpful) {
                            top.linkTo(divider2.bottom)
                            start.linkTo(parent.start)
                        },
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_baseline_thumb_up_alt_24),
                        tint = SecondaryVariantColor,
                        contentDescription = "Helpful"
                    )
                    Text(
                        text = stringResource(id = R.string.helpful),
                        style = MaterialTheme.typography.h3,
                        color = SecondaryVariantColor,
                        modifier = Modifier.padding(
                            start = 4.dp,
                            top = 0.dp,
                            end = 0.dp,
                            bottom = 0.dp
                        )
                    )
                }
                Row(
                    modifier = Modifier
                        .constrainAs(notHelpful) {
                            top.linkTo(divider2.bottom)
                            start.linkTo(helpful.end)
                        }
                        .padding(start = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_baseline_thumb_down_alt_24),
                        tint = SecondaryVariantColor,
                        contentDescription = "Not helpful"
                    )
                    Text(
                        text = stringResource(id = R.string.not_helpful),
                        style = MaterialTheme.typography.h3,
                        color = SecondaryVariantColor,
                        modifier = Modifier.padding(
                            start = 4.dp,
                            top = 0.dp,
                            end = 0.dp,
                            bottom = 0.dp
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun IndexPriceChangeAlert(alert: UiAlert) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 16.dp)
    ) {
        C3SimpleCard {
            ConstraintLayout(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                val (description, flag, status, poValue, divider1, oldIndex, newIndex, indexChange, divider2, helpful, notHelpful) = createRefs()
                Text(
                    alert.description,
                    style = MaterialTheme.typography.h3,
                    color = MaterialTheme.colors.primary,
                    modifier = Modifier
                        .constrainAs(description) {
                            top.linkTo(parent.top)
                            start.linkTo(parent.start)
                            end.linkTo(flag.start)
                        }
                        .padding(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 0.dp)
                )
                C3IconButton(
                    onClick = { },
                    modifier = Modifier
                        .constrainAs(flag) {
                            top.linkTo(parent.top)
                            end.linkTo(parent.end)
                        }) {
                    Icon(
                        painter = painterResource(id = R.drawable.flag),
                        tint = MaterialTheme.colors.onBackground,
                        contentDescription = "flag"
                    )
                }
                SplitText(
                    modifier = Modifier.constrainAs(status) {
                        top.linkTo(description.bottom, margin = 8.dp)
                    },
                    SpanStyle(Orange) to (alert.currentState?.name ?: ""),
                    null to (alert.timestamp ?: ""),
                )
                SplitText(
                    modifier = Modifier.constrainAs(poValue) {
                        top.linkTo(status.bottom, margin = 0.dp)
                    },
                    // TODO!!! not clear which api data.
                    SpanStyle(Lila40) to stringResource(id = R.string.open_po_line_value),
                    null to "-",
                )
                ListDivider(Modifier.constrainAs(divider1) { top.linkTo(poValue.bottom) })
                LabeledValue(
                    label = stringResource(R.string.old_index_price),
                    value = "-",
                    modifier = Modifier
                        .constrainAs(oldIndex) {
                            top.linkTo(divider1.bottom)
                            start.linkTo(parent.start)
                            end.linkTo(newIndex.start)
                            width = Dimension.fillToConstraints
                        },
                )
                LabeledValue(
                    label = stringResource(R.string.new_index_price),
                    value = "-", // TODO!!! not clear which api data.
                    modifier = Modifier
                        .constrainAs(newIndex) {
                            top.linkTo(divider1.bottom)
                            start.linkTo(oldIndex.end, margin = 8.dp)
                            end.linkTo(indexChange.start)
                            width = Dimension.fillToConstraints
                        },
                )
                LabeledValue(
                    label = stringResource(R.string.index_price_change),
                    value = "-", // TODO!!! not clear which api data.
                    modifier = Modifier
                        .constrainAs(indexChange) {
                            top.linkTo(divider1.bottom)
                            start.linkTo(newIndex.end, margin = 8.dp)
                            end.linkTo(parent.end)
                            width = Dimension.fillToConstraints
                        },
                )
                ListDivider(Modifier.constrainAs(divider2) { top.linkTo(indexChange.bottom) })
                Row(
                    modifier = Modifier
                        .constrainAs(helpful) {
                            top.linkTo(divider2.bottom)
                            start.linkTo(parent.start)
                        },
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_baseline_thumb_up_alt_24),
                        tint = SecondaryVariantColor,
                        contentDescription = "Helpful"
                    )
                    Text(
                        text = stringResource(id = R.string.helpful),
                        style = MaterialTheme.typography.h3,
                        color = SecondaryVariantColor,
                        modifier = Modifier.padding(
                            start = 4.dp,
                            top = 0.dp,
                            end = 0.dp,
                            bottom = 0.dp
                        )
                    )
                }
                Row(
                    modifier = Modifier
                        .constrainAs(notHelpful) {
                            top.linkTo(divider2.bottom)
                            start.linkTo(helpful.end)
                        }
                        .padding(start = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_baseline_thumb_down_alt_24),
                        tint = SecondaryVariantColor,
                        contentDescription = "Not helpful"
                    )
                    Text(
                        text = stringResource(id = R.string.not_helpful),
                        style = MaterialTheme.typography.h3,
                        color = SecondaryVariantColor,
                        modifier = Modifier.padding(
                            start = 4.dp,
                            top = 0.dp,
                            end = 0.dp,
                            bottom = 0.dp
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun IndexPriceAnomalyAlert(alert: UiAlert) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 16.dp)
    ) {
        C3SimpleCard {
            ConstraintLayout(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                val (description, flag, status, poValue, divider1, lastPrice, index, divider2, helpful, notHelpful) = createRefs()
                Text(
                    alert.description,
                    style = MaterialTheme.typography.h3,
                    color = MaterialTheme.colors.primary,
                    modifier = Modifier
                        .constrainAs(description) {
                            top.linkTo(parent.top)
                            start.linkTo(parent.start)
                            end.linkTo(flag.start)
                        }
                        .padding(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 0.dp)
                )
                C3IconButton(
                    onClick = { },
                    modifier = Modifier
                        .constrainAs(flag) {
                            top.linkTo(parent.top)
                            end.linkTo(parent.end)
                        }) {
                    Icon(
                        painter = painterResource(id = R.drawable.flag),
                        tint = MaterialTheme.colors.onBackground,
                        contentDescription = "flag"
                    )
                }
                SplitText(
                    modifier = Modifier.constrainAs(status) {
                        top.linkTo(description.bottom, margin = 8.dp)
                    },
                    SpanStyle(Orange) to (alert.currentState?.name ?: ""),
                    null to (alert.timestamp ?: ""),
                )
                SplitText(
                    modifier = Modifier.constrainAs(poValue) {
                        top.linkTo(status.bottom, margin = 0.dp)
                    },
                    // TODO!!! not clear which api data.
                    SpanStyle(Lila40) to stringResource(id = R.string.open_po_line_value),
                    null to "-",
                )
                ListDivider(Modifier.constrainAs(divider1) { top.linkTo(poValue.bottom) })
                LabeledValue(
                    label = stringResource(R.string.last_item_price),
                    value = "-",
                    modifier = Modifier
                        .constrainAs(lastPrice) {
                            top.linkTo(divider1.bottom)
                            start.linkTo(parent.start)
                            end.linkTo(index.start)
                            width = Dimension.fillToConstraints
                        },
                )
                LabeledValue(
                    label = stringResource(R.string.index),
                    value = "-", // TODO!!! not clear which api data.
                    modifier = Modifier
                        .constrainAs(index) {
                            top.linkTo(divider1.bottom)
                            start.linkTo(lastPrice.end, margin = 8.dp)
                            end.linkTo(parent.end)
                            width = Dimension.fillToConstraints
                        },
                )
                ListDivider(Modifier.constrainAs(divider2) { top.linkTo(lastPrice.bottom) })
                Row(
                    modifier = Modifier
                        .constrainAs(helpful) {
                            top.linkTo(divider2.bottom)
                            start.linkTo(parent.start)
                        },
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_baseline_thumb_up_alt_24),
                        tint = SecondaryVariantColor,
                        contentDescription = "Helpful"
                    )
                    Text(
                        text = stringResource(id = R.string.helpful),
                        style = MaterialTheme.typography.h3,
                        color = SecondaryVariantColor,
                        modifier = Modifier.padding(
                            start = 4.dp,
                            top = 0.dp,
                            end = 0.dp,
                            bottom = 0.dp
                        )
                    )
                }
                Row(
                    modifier = Modifier
                        .constrainAs(notHelpful) {
                            top.linkTo(divider2.bottom)
                            start.linkTo(helpful.end)
                        }
                        .padding(start = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_baseline_thumb_down_alt_24),
                        tint = SecondaryVariantColor,
                        contentDescription = "Not helpful"
                    )
                    Text(
                        text = stringResource(id = R.string.not_helpful),
                        style = MaterialTheme.typography.h3,
                        color = SecondaryVariantColor,
                        modifier = Modifier.padding(
                            start = 4.dp,
                            top = 0.dp,
                            end = 0.dp,
                            bottom = 0.dp
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun RequestedDeliveryDateChangeAlert(alert: UiAlert) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 16.dp)
    ) {
        C3SimpleCard {
            ConstraintLayout(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                val (description, flag, status, poValue, divider1, oldDate, newDate, supplier, divider2, helpful, notHelpful, contact) = createRefs()
                Text(
                    alert.description,
                    style = MaterialTheme.typography.h3,
                    color = MaterialTheme.colors.primary,
                    modifier = Modifier
                        .constrainAs(description) {
                            top.linkTo(parent.top)
                            start.linkTo(parent.start)
                            end.linkTo(flag.start)
                        }
                        .padding(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 0.dp)
                )
                C3IconButton(
                    onClick = { },
                    modifier = Modifier
                        .constrainAs(flag) {
                            top.linkTo(parent.top)
                            end.linkTo(parent.end)
                        }) {
                    Icon(
                        painter = painterResource(id = R.drawable.flag),
                        tint = MaterialTheme.colors.onBackground,
                        contentDescription = "flag"
                    )
                }
                SplitText(
                    modifier = Modifier.constrainAs(status) {
                        top.linkTo(description.bottom, margin = 8.dp)
                    },
                    SpanStyle(Orange) to (alert.currentState?.name ?: ""),
                    null to (alert.timestamp ?: ""),
                )
                SplitText(
                    modifier = Modifier.constrainAs(poValue) {
                        top.linkTo(status.bottom, margin = 0.dp)
                    },
                    // TODO!!! not clear which api data.
                    SpanStyle(Lila40) to stringResource(id = R.string.open_po_line_value),
                    null to "-",
                )
                ListDivider(Modifier.constrainAs(divider1) { top.linkTo(poValue.bottom) })
                LabeledValue(
                    label = stringResource(R.string.old_requested_delivery_date),
                    value = "-",
                    modifier = Modifier
                        .constrainAs(oldDate) {
                            top.linkTo(divider1.bottom)
                            start.linkTo(parent.start)
                            end.linkTo(newDate.start)
                            width = Dimension.fillToConstraints
                        },
                )
                LabeledValue(
                    label = stringResource(R.string.new_requested_delivery_date),
                    value = "-", // TODO!!! not clear which api data.
                    modifier = Modifier
                        .constrainAs(newDate) {
                            top.linkTo(divider1.bottom)
                            start.linkTo(oldDate.end, margin = 8.dp)
                            end.linkTo(supplier.start)
                            width = Dimension.fillToConstraints
                        },
                )
                LabeledValue(
                    label = stringResource(R.string.supplier__),
                    value = "-", // TODO!!! not clear which api data.
                    modifier = Modifier
                        .constrainAs(supplier) {
                            top.linkTo(divider1.bottom)
                            start.linkTo(newDate.end, margin = 8.dp)
                            end.linkTo(parent.end)
                            width = Dimension.fillToConstraints
                        },
                )
                ListDivider(Modifier.constrainAs(divider2) { top.linkTo(supplier.bottom) })
                Row(
                    modifier = Modifier
                        .constrainAs(helpful) {
                            top.linkTo(divider2.bottom, margin = 8.dp)
                            start.linkTo(parent.start)
                        },
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_baseline_thumb_up_alt_24),
                        tint = SecondaryVariantColor,
                        contentDescription = "Helpful"
                    )
                    Text(
                        text = stringResource(id = R.string.helpful),
                        style = MaterialTheme.typography.h3,
                        color = SecondaryVariantColor,
                        modifier = Modifier.padding(
                            start = 4.dp,
                            top = 0.dp,
                            end = 0.dp,
                            bottom = 0.dp
                        )
                    )
                }
                Row(
                    modifier = Modifier
                        .constrainAs(notHelpful) {
                            top.linkTo(divider2.bottom, margin = 8.dp)
                            start.linkTo(helpful.end)
                        }
                        .padding(start = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_baseline_thumb_down_alt_24),
                        tint = SecondaryVariantColor,
                        contentDescription = "Not helpful"
                    )
                    Text(
                        text = stringResource(id = R.string.not_helpful),
                        style = MaterialTheme.typography.h3,
                        color = SecondaryVariantColor,
                        modifier = Modifier.padding(
                            start = 4.dp,
                            top = 0.dp,
                            end = 0.dp,
                            bottom = 0.dp
                        )
                    )
                }
                C3IconButton(
                    onClick = { },
                    modifier = Modifier
                        .constrainAs(contact) {
                            top.linkTo(divider2.bottom)
                            end.linkTo(parent.end)
                        }) {
                    Icon(
                        painter = painterResource(id = R.drawable.person_card),
                        tint = MaterialTheme.colors.onBackground,
                        contentDescription = "contact"
                    )
                }
            }
        }
    }
}

@Composable
private fun PurchaseOrderAlert(alert: UiAlert) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 16.dp)
    ) {
        C3SimpleCard {
            ConstraintLayout(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                val (description, flag, status, poValue, item, divider1, plannedDate, requestedDate, openedDate, divider2, helpful, notHelpful, contact) = createRefs()
                Text(
                    alert.description,
                    style = MaterialTheme.typography.h3,
                    color = MaterialTheme.colors.primary,
                    modifier = Modifier
                        .constrainAs(description) {
                            top.linkTo(parent.top)
                            start.linkTo(parent.start)
                            end.linkTo(flag.start)
                        }
                        .padding(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 0.dp)
                )
                C3IconButton(
                    onClick = { },
                    modifier = Modifier
                        .constrainAs(flag) {
                            top.linkTo(parent.top)
                            end.linkTo(parent.end)
                        }) {
                    Icon(
                        painter = painterResource(id = R.drawable.flag),
                        tint = MaterialTheme.colors.onBackground,
                        contentDescription = "flag"
                    )
                }
                SplitText(
                    modifier = Modifier.constrainAs(status) {
                        top.linkTo(description.bottom, margin = 8.dp)
                    },
                    SpanStyle(Orange) to (alert.currentState?.name ?: ""),
                    null to (alert.timestamp ?: ""),
                )
                SplitText(
                    modifier = Modifier.constrainAs(poValue) {
                        top.linkTo(status.bottom)
                    },
                    // TODO!!! not clear which api data.
                    SpanStyle(Lila40) to stringResource(id = R.string.open_po_line_value),
                    null to "-",
                )
                SplitText(
                    modifier = Modifier.constrainAs(item) {
                        top.linkTo(poValue.bottom)
                    },
                    // TODO!!! not clear which api data.
                    SpanStyle(PrimaryColor) to stringResource(id = R.string.item),
                    null to "-",
                )
                ListDivider(Modifier.constrainAs(divider1) { top.linkTo(item.bottom) })
                LabeledValue(
                    label = stringResource(R.string.planned_lead_time),
                    value = "-",
                    modifier = Modifier
                        .constrainAs(plannedDate) {
                            top.linkTo(divider1.bottom)
                            start.linkTo(parent.start)
                            end.linkTo(requestedDate.start)
                            width = Dimension.fillToConstraints
                        },
                )
                LabeledValue(
                    label = stringResource(R.string.requested_delivery_date_),
                    value = "-", // TODO!!! not clear which api data.
                    modifier = Modifier
                        .constrainAs(requestedDate) {
                            top.linkTo(divider1.bottom)
                            start.linkTo(plannedDate.end, margin = 8.dp)
                            end.linkTo(openedDate.start)
                            width = Dimension.fillToConstraints
                        },
                )
                LabeledValue(
                    label = stringResource(R.string.opened_date_),
                    value = "-", // TODO!!! not clear which is savings opp from api data.
                    modifier = Modifier
                        .constrainAs(openedDate) {
                            top.linkTo(divider1.bottom)
                            start.linkTo(requestedDate.end, margin = 8.dp)
                            end.linkTo(parent.end)
                            width = Dimension.fillToConstraints
                        },
                )
                ListDivider(Modifier.constrainAs(divider2) { top.linkTo(plannedDate.bottom) })
                Row(
                    modifier = Modifier
                        .constrainAs(helpful) {
                            top.linkTo(divider2.bottom, margin = 8.dp)
                            start.linkTo(parent.start)
                        },
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_baseline_thumb_up_alt_24),
                        tint = SecondaryVariantColor,
                        contentDescription = "Helpful"
                    )
                    Text(
                        text = stringResource(id = R.string.helpful),
                        style = MaterialTheme.typography.h3,
                        color = SecondaryVariantColor,
                        modifier = Modifier.padding(
                            start = 4.dp,
                            top = 0.dp,
                            end = 0.dp,
                            bottom = 0.dp
                        )
                    )
                }
                Row(
                    modifier = Modifier
                        .constrainAs(notHelpful) {
                            top.linkTo(divider2.bottom, margin = 8.dp)
                            start.linkTo(helpful.end)
                        }
                        .padding(start = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_baseline_thumb_down_alt_24),
                        tint = SecondaryVariantColor,
                        contentDescription = "Not helpful"
                    )
                    Text(
                        text = stringResource(id = R.string.not_helpful),
                        style = MaterialTheme.typography.h3,
                        color = SecondaryVariantColor,
                        modifier = Modifier.padding(
                            start = 4.dp,
                            top = 0.dp,
                            end = 0.dp,
                            bottom = 0.dp
                        )
                    )
                }
                C3IconButton(
                    onClick = { },
                    modifier = Modifier
                        .constrainAs(contact) {
                            top.linkTo(divider2.bottom)
                            end.linkTo(parent.end)
                        }) {
                    Icon(
                        painter = painterResource(id = R.drawable.person_card),
                        tint = MaterialTheme.colors.onBackground,
                        contentDescription = "contact"
                    )
                }
            }
        }
    }
}

@Composable
private fun DUNSRiskAlert(alert: UiAlert) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 16.dp)
    ) {
        C3SimpleCard {
            ConstraintLayout(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                val (description, flag, status, poValue, divider1, source, ser, ssl, emma, divider2, helpful, notHelpful, contact) = createRefs()
                Text(
                    alert.description,
                    style = MaterialTheme.typography.h3,
                    color = MaterialTheme.colors.primary,
                    modifier = Modifier
                        .constrainAs(description) {
                            top.linkTo(parent.top)
                            start.linkTo(parent.start)
                            end.linkTo(flag.start)
                        }
                        .padding(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 0.dp)
                )
                C3IconButton(
                    onClick = { },
                    modifier = Modifier
                        .constrainAs(flag) {
                            top.linkTo(parent.top)
                            end.linkTo(parent.end)
                        }) {
                    Icon(
                        painter = painterResource(id = R.drawable.flag),
                        tint = MaterialTheme.colors.onBackground,
                        contentDescription = "flag"
                    )
                }
                SplitText(
                    modifier = Modifier.constrainAs(status) {
                        top.linkTo(description.bottom, margin = 8.dp)
                    },
                    SpanStyle(Orange) to (alert.currentState?.name ?: ""),
                    null to (alert.timestamp ?: ""),
                )
                SplitText(
                    modifier = Modifier.constrainAs(poValue) {
                        top.linkTo(status.bottom, margin = 0.dp)
                    },
                    // TODO!!! not clear which is savings opp from api data.
                    SpanStyle(Lila40) to stringResource(id = R.string.open_po_line_value),
                    null to "-",
                )
                ListDivider(Modifier.constrainAs(divider1) { top.linkTo(poValue.bottom) })
                LabeledValue(
                    label = stringResource(R.string.source),
                    value = "-",
                    modifier = Modifier
                        .constrainAs(source) {
                            top.linkTo(divider1.bottom)
                            start.linkTo(parent.start)
                            end.linkTo(ser.start)
                            width = Dimension.fillToConstraints
                        },
                )
                LabeledValue(
                    label = stringResource(R.string.ser),
                    value = "-", // TODO!!! not clear which is savings opp from api data.
                    modifier = Modifier
                        .constrainAs(ser) {
                            top.linkTo(divider1.bottom)
                            start.linkTo(source.end, margin = 8.dp)
                            end.linkTo(ssl.start)
                            width = Dimension.fillToConstraints
                        },
                )
                LabeledValue(
                    label = stringResource(R.string.ssl),
                    value = "-", // TODO!!! not clear which is savings opp from api data.
                    modifier = Modifier
                        .constrainAs(ssl) {
                            top.linkTo(divider1.bottom)
                            start.linkTo(ser.end, margin = 8.dp)
                            end.linkTo(emma.start)
                            width = Dimension.fillToConstraints
                        },
                )
                LabeledValue(
                    label = stringResource(R.string.emma),
                    value = "-", // TODO!!! not clear which is savings opp from api data.
                    modifier = Modifier
                        .constrainAs(emma) {
                            top.linkTo(divider1.bottom)
                            start.linkTo(ssl.end, margin = 8.dp)
                            end.linkTo(parent.end)
                            width = Dimension.fillToConstraints
                        },
                )
                ListDivider(Modifier.constrainAs(divider2) { top.linkTo(emma.bottom) })
                Row(
                    modifier = Modifier
                        .constrainAs(helpful) {
                            top.linkTo(divider2.bottom, margin = 8.dp)
                            start.linkTo(parent.start)
                        },
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_baseline_thumb_up_alt_24),
                        tint = SecondaryVariantColor,
                        contentDescription = "Helpful"
                    )
                    Text(
                        text = stringResource(id = R.string.helpful),
                        style = MaterialTheme.typography.h3,
                        color = SecondaryVariantColor,
                        modifier = Modifier.padding(
                            start = 4.dp,
                            top = 0.dp,
                            end = 0.dp,
                            bottom = 0.dp
                        )
                    )
                }
                Row(
                    modifier = Modifier
                        .constrainAs(notHelpful) {
                            top.linkTo(divider2.bottom, margin = 8.dp)
                            start.linkTo(helpful.end)
                        }
                        .padding(start = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_baseline_thumb_down_alt_24),
                        tint = SecondaryVariantColor,
                        contentDescription = "Not helpful"
                    )
                    Text(
                        text = stringResource(id = R.string.not_helpful),
                        style = MaterialTheme.typography.h3,
                        color = SecondaryVariantColor,
                        modifier = Modifier.padding(
                            start = 4.dp,
                            top = 0.dp,
                            end = 0.dp,
                            bottom = 0.dp
                        )
                    )
                }
                C3IconButton(
                    onClick = { },
                    modifier = Modifier
                        .constrainAs(contact) {
                            top.linkTo(divider2.bottom)
                            end.linkTo(parent.end)
                        }) {
                    Icon(
                        painter = painterResource(id = R.drawable.person_card),
                        tint = MaterialTheme.colors.onBackground,
                        contentDescription = "contact"
                    )
                }
            }
        }
    }
}

@Composable
private fun RapidRatingsRiskAlert(alert: UiAlert) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 16.dp)
    ) {
        C3SimpleCard {
            ConstraintLayout(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                val (description, flag, status, poValue, divider1, source, fhr, divider2, helpful, notHelpful, contact) = createRefs()
                Text(
                    alert.description,
                    style = MaterialTheme.typography.h3,
                    color = MaterialTheme.colors.primary,
                    modifier = Modifier
                        .constrainAs(description) {
                            top.linkTo(parent.top)
                            start.linkTo(parent.start)
                            end.linkTo(flag.start)
                        }
                        .padding(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 0.dp)
                )
                C3IconButton(
                    onClick = { },
                    modifier = Modifier
                        .constrainAs(flag) {
                            top.linkTo(parent.top)
                            end.linkTo(parent.end)
                        }) {
                    Icon(
                        painter = painterResource(id = R.drawable.flag),
                        tint = MaterialTheme.colors.onBackground,
                        contentDescription = "flag"
                    )
                }
                SplitText(
                    modifier = Modifier.constrainAs(status) {
                        top.linkTo(description.bottom, margin = 8.dp)
                    },
                    SpanStyle(Orange) to (alert.currentState?.name ?: ""),
                    null to (alert.timestamp ?: ""),
                )
                SplitText(
                    modifier = Modifier.constrainAs(poValue) {
                        top.linkTo(status.bottom, margin = 0.dp)
                    },
                    // TODO!!! not clear which is savings opp from api data.
                    SpanStyle(Lila40) to stringResource(id = R.string.open_po_val),
                    null to "-",
                )
                ListDivider(Modifier.constrainAs(divider1) { top.linkTo(poValue.bottom) })
                LabeledValue(
                    label = stringResource(R.string.source),
                    value = "-",
                    modifier = Modifier
                        .constrainAs(source) {
                            top.linkTo(divider1.bottom)
                            start.linkTo(parent.start)
                            end.linkTo(fhr.start)
                            width = Dimension.fillToConstraints
                        },
                )
                LabeledValue(
                    label = stringResource(R.string.fhr),
                    value = "-", // TODO!!! not clear which is savings opp from api data.
                    modifier = Modifier
                        .constrainAs(fhr) {
                            top.linkTo(divider1.bottom)
                            start.linkTo(source.end, margin = 8.dp)
                            end.linkTo(parent.end)
                            width = Dimension.fillToConstraints
                        },
                )
                ListDivider(Modifier.constrainAs(divider2) { top.linkTo(source.bottom) })
                Row(
                    modifier = Modifier
                        .constrainAs(helpful) {
                            top.linkTo(divider2.bottom, margin = 8.dp)
                            start.linkTo(parent.start)
                        },
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_baseline_thumb_up_alt_24),
                        tint = SecondaryVariantColor,
                        contentDescription = "Helpful"
                    )
                    Text(
                        text = stringResource(id = R.string.helpful),
                        style = MaterialTheme.typography.h3,
                        color = SecondaryVariantColor,
                        modifier = Modifier.padding(
                            start = 4.dp,
                            top = 0.dp,
                            end = 0.dp,
                            bottom = 0.dp
                        )
                    )
                }
                Row(
                    modifier = Modifier
                        .constrainAs(notHelpful) {
                            top.linkTo(divider2.bottom, margin = 8.dp)
                            start.linkTo(helpful.end)
                        }
                        .padding(start = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_baseline_thumb_down_alt_24),
                        tint = SecondaryVariantColor,
                        contentDescription = "Not helpful"
                    )
                    Text(
                        text = stringResource(id = R.string.not_helpful),
                        style = MaterialTheme.typography.h3,
                        color = SecondaryVariantColor,
                        modifier = Modifier.padding(
                            start = 4.dp,
                            top = 0.dp,
                            end = 0.dp,
                            bottom = 0.dp
                        )
                    )
                }
                C3IconButton(
                    onClick = { },
                    modifier = Modifier
                        .constrainAs(contact) {
                            top.linkTo(divider2.bottom)
                            end.linkTo(parent.end)
                        }) {
                    Icon(
                        painter = painterResource(id = R.drawable.person_card),
                        tint = MaterialTheme.colors.onBackground,
                        contentDescription = "contact"
                    )
                }
            }
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
            IconButton(onClick = { onChangeFilter(Gson().toJson(uiState.selectedCategories)) }) {
                Icon(
                    imageVector = Icons.Filled.Settings,
                    contentDescription = stringResource(R.string.cd_settings_menu)
                )
            }
        }
    )
}