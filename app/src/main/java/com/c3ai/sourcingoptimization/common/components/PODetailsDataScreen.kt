package com.c3ai.sourcingoptimization.common.components

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.c3ai.sourcingoptimization.R
import com.c3ai.sourcingoptimization.presentation.po_details.PODetailsUiState
import com.c3ai.sourcingoptimization.ui.theme.Green40

@ExperimentalMaterialApi
@OptIn(ExperimentalComposeUiApi::class, ExperimentalAnimationApi::class)
@Composable
fun PODetailsDataScreen(
    uiState: PODetailsUiState.HasDetails,
    onContactBuyerClick: () -> Unit,
    onContactSupplierClick: () -> Unit,
) {
    val item = uiState.order
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 16.dp)
    ) {
        C3SimpleCard {
            ConstraintLayout(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                // Create references for the composables to constrain
                val (status, totalCost, openedDate, closedDate, divider1, buyer, spacer1, divider2, vendor, spacer2) = createRefs()
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
                ListDivider(Modifier.constrainAs(divider1) { top.linkTo(totalCost.bottom) })
                BusinessCard(
                    label = stringResource(R.string.buyer_, item.buyer?.id ?: ""),
                    title = item.buyer?.name ?: "",
                    subtitle = "",
                    image1 = R.drawable.alert,
                    image2 = R.drawable.person_card,
                    onIcon2Click = { onContactBuyerClick() },
                    modifier = Modifier
                        .constrainAs(buyer) {
                            top.linkTo(divider1.bottom)
                        }
                )
                Spacer(modifier = Modifier
                    .height(16.dp)
                    .constrainAs(spacer1) { top.linkTo(buyer.bottom) })
                ListDivider(Modifier.constrainAs(divider2) { top.linkTo(spacer1.bottom) })
                BusinessCard(
                    label = stringResource(R.string.supplier_, item.vendor?.id ?: ""),
                    title = item.vendor?.name ?: "",
                    subtitle = item.vendor?.location?.address?.components?.joinToString {
                        it.name ?: ""
                    } ?: "",
                    image1 = R.drawable.alert,
                    image2 = R.drawable.person_card,
                    onIcon2Click = { onContactSupplierClick() },
                    modifier = Modifier
                        .constrainAs(vendor) {
                            top.linkTo(divider2.bottom)
                        }
                )
                Spacer(modifier = Modifier
                    .height(16.dp)
                    .constrainAs(spacer2) { top.linkTo(vendor.bottom) })
            }
        }
    }
}