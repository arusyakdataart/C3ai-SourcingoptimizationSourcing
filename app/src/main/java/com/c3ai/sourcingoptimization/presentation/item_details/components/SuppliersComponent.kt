package com.c3ai.sourcingoptimization.presentation.item_details.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Link
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.paging.compose.collectAsLazyPagingItems
import com.c3ai.sourcingoptimization.R
import com.c3ai.sourcingoptimization.common.components.*
import com.c3ai.sourcingoptimization.presentation.item_details.ItemDetailsUiState
import java.util.*


/**
 * Decomposition of item details[ItemDetailsDataScreen] with separate component for Suppliers tab
 * to make a code supporting easier[SuppliersComponent].
 * */
@Composable
fun SuppliersComponent(
    uiState: ItemDetailsUiState.HasItem,
    loadData: () -> Unit,
    onSupplierClick: (String) -> Unit,
    onAlertsClick: (String) -> Unit,
    onContactClick: (String) -> Unit,
) {
    LaunchedEffect(uiState.itemId) {
        loadData()
    }

    val lazyItems = uiState.suppliers?.collectAsLazyPagingItems()
    val listState = rememberLazyListState()

    lazyItems?.let { items ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            listState,
            contentPadding = PaddingValues(all = 16.dp),
        ) {
            items(items.itemCount) { index ->
                val item = items[index]!!
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
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
                                title,
                                alerts,
                                facility,
                                divider,
                                closedPOLValue,
                                openPOLValue,
                                avgUnitPrice,
                            ) = createRefs()
                            IconText(
                                stringResource(R.string.supplier_, item.id).uppercase(),
                                style = MaterialTheme.typography.subtitle2,
                                color = MaterialTheme.colors.secondary,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(end = 34.dp)
                                    .clickable { onSupplierClick(item.id) }
                                    .constrainAs(title) {
                                        top.linkTo(parent.top)
                                        start.linkTo(parent.start)
                                    }

                            ) {
                                Icon(Icons.Filled.Link, "", tint = MaterialTheme.colors.primary)
                            }
                            BusinessCard(
                                title = "FA",
                                subtitle = "",
                                image1 = R.drawable.person_card,
                                image2 = R.drawable.alert,
                                onIcon1Click = { onContactClick(item.id) },
                                onIcon2Click = { onAlertsClick(item.id) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .constrainAs(facility) {
                                        top.linkTo(title.bottom, margin = 8.dp)
                                        start.linkTo(parent.start)
                                    }
                            )
                            ListDivider(Modifier.constrainAs(divider) { top.linkTo(facility.bottom) })
                            LabeledValue(
                                label = stringResource(R.string.closed_pol_value),
                                value = item.closedPOValue,
                                labelModifier = Modifier.height(32.dp),
                                modifier = Modifier
                                    .constrainAs(closedPOLValue) {
                                        top.linkTo(divider.bottom)
                                        start.linkTo(parent.start)
                                        end.linkTo(openPOLValue.start)
                                        width = Dimension.fillToConstraints
                                    },
                            )
                            LabeledValue(
                                label = stringResource(R.string.open_pol_value),
                                value = item.openPOValue,
                                labelModifier = Modifier.height(32.dp),
                                modifier = Modifier
                                    .constrainAs(openPOLValue) {
                                        top.linkTo(divider.bottom)
                                        start.linkTo(closedPOLValue.end, margin = 32.dp)
                                        end.linkTo(avgUnitPrice.start)
                                        width = Dimension.fillToConstraints
                                    },
                            )
                            LabeledValue(
                                label = stringResource(R.string.avg_unit_price),
                                value = item.avgPOValue,
                                labelModifier = Modifier.height(32.dp),
                                modifier = Modifier
                                    .constrainAs(avgUnitPrice) {
                                        top.linkTo(divider.bottom)
                                        start.linkTo(openPOLValue.end, margin = 32.dp)
                                        end.linkTo(parent.end)
                                        width = Dimension.fillToConstraints
                                    },
                            )
                        }
                    }
                }
            }
        }
    }
}