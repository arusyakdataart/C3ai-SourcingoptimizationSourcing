package com.c3ai.sourcingoptimization.presentation.alerts

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.c3ai.sourcingoptimization.R
import com.c3ai.sourcingoptimization.common.components.*
import com.c3ai.sourcingoptimization.presentation.views.UiAlert
import com.c3ai.sourcingoptimization.ui.theme.Green40
import com.c3ai.sourcingoptimization.ui.theme.Lila40
import com.c3ai.sourcingoptimization.ui.theme.Orange
import com.c3ai.sourcingoptimization.ui.theme.SecondaryVariantColor

@Composable
fun RapidRatingsRiskAlert(
    alert: UiAlert,
    onChangeFeedbackHelpful: () -> Unit,
    onChangeFeedbackUnhelpful: () -> Unit
) {
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
                        imageVector = if (alert.flagged == true) Icons.Filled.Flag else Icons.Outlined.Flag,
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
                    C3IconButton(
                        onClick = onChangeFeedbackHelpful
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_baseline_thumb_up_alt_24),
                            tint = if (alert.feedback?.helpful == true) Green40 else SecondaryVariantColor,
                            contentDescription = "Helpful"
                        )
                    }
                    Text(
                        text = stringResource(id = R.string.helpful),
                        style = MaterialTheme.typography.h3,
                        color = if (alert.feedback?.helpful == true) Green40 else SecondaryVariantColor,
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
                    C3IconButton(
                        onClick = onChangeFeedbackUnhelpful
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_baseline_thumb_down_alt_24),
                            tint = SecondaryVariantColor,
                            contentDescription = "Not helpful"
                        )
                    }
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