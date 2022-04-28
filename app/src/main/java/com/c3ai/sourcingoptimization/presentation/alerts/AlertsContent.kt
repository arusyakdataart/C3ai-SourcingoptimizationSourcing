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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.c3ai.sourcingoptimization.R
import com.c3ai.sourcingoptimization.common.components.CollapsableLayout
import com.c3ai.sourcingoptimization.common.components.PButton
import com.c3ai.sourcingoptimization.presentation.views.UiAlert
import com.c3ai.sourcingoptimization.ui.theme.BackgroundColor
import com.c3ai.sourcingoptimization.utilities.PAGINATED_RESPONSE_LIMIT
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterialApi::class)
@Composable
fun AlertsContent(
    uiState: AlertsUiState,
    viewModel: AlertsViewModel,
    coroutineScope: CoroutineScope,
    bottomState: ModalBottomSheetState,
    modifier: Modifier,
    onRefreshDetails: () -> Unit,
    onCollapsableItemClick: (String) -> Unit,
    onSupplierClick: (String) -> Unit,
    onItemClick: (String) -> Unit,
    onPOClick: (String) -> Unit,
    onContactClick: (String) -> Unit,
) {

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

                    itemsIndexed(items = categoryList.getValue(it)) { index, it ->

                        viewModel.onChangeListScrollPosition(index)
                        val page = viewModel.pages[0].value
                        if ((index + 1) >= (page * PAGINATED_RESPONSE_LIMIT)){
                            viewModel.nextPage()
                        }
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
                                    val supplierId = it.redirectUrl?.substring(
                                        it.redirectUrl.lastIndexOf("/") + 1
                                    )
                                    if (supplierId != null) {
                                        onContactClick(supplierId)
                                    }
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
                        Box(modifier.fillMaxSize()) { /* empty screen */ }
                    }
                }
            }
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