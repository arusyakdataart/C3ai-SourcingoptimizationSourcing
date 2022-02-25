package com.c3ai.sourcingoptimization.common.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ReadMore
import androidx.compose.material.icons.filled.Warning
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.c3ai.sourcingoptimization.R
import com.c3ai.sourcingoptimization.presentation.views.UiPurchaseOrder
import com.c3ai.sourcingoptimization.ui.theme.Green40
import com.c3ai.sourcingoptimization.ui.theme.Lila40

@Composable
fun PoLinesListExpanded(
    item: UiPurchaseOrder.Line,
    padding: Dp = 0.dp,
    onPOAlertsClick: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = padding, top = 16.dp, end = padding, bottom = padding)
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
                        painter = painterResource(id = R.drawable.alert_white),
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
//                var expanded by remember { mutableStateOf(false) }
//                IconButton(
//                    onClick = { expanded = true },
//                    Modifier
//                        .size(40.dp)
//                        .constrainAs(readMore) {
//                            top.linkTo(facility.bottom)
//                            end.linkTo(parent.end)
//                        }
//                ) {
//                    Icon(
//                        imageVector = Icons.Filled.ReadMore,
//                        contentDescription = stringResource(R.string.cd_read_more)
//                    )
//                    PoLinesListReadMore(item = item, expanded = expanded) {
//                        expanded = false
//                    }
//                }
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
