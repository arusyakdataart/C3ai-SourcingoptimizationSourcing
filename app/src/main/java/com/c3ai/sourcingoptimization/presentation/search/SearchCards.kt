package com.c3ai.sourcingoptimization.presentation.search

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.c3ai.sourcingoptimization.R
import com.c3ai.sourcingoptimization.common.components.SplitText
import com.c3ai.sourcingoptimization.domain.model.*
import com.c3ai.sourcingoptimization.ui.theme.*


@Composable
fun SearchImage(
    searchItem: SearchItem,
    imageVectorDefault: ImageVector = Icons.Filled.DisabledByDefault,
    modifier: Modifier = Modifier
) {
    Image(
        imageVector = when (searchItem) {
            is ItemSearchItem -> Icons.Filled.GridView
            is SupplierSearchItem -> Icons.Filled.Apartment
            is AlertSearchItem -> Icons.Filled.Warning
            is POLSearchItem -> Icons.Filled.ContentPaste
            is POSearchItem -> Icons.Filled.ContentPaste
            else -> imageVectorDefault
        },
        contentDescription = null,
        colorFilter = ColorFilter.tint(Gray60),
        modifier = modifier
            .size(20.dp, 20.dp)
            .clip(MaterialTheme.shapes.small)
    )
}

@Composable
fun SearchTitle(searchItem: SearchItem) {
    Text(
        text = when (searchItem) {
            is ItemSearchItem -> stringResource(R.string.item_, searchItem.id)
            is SupplierSearchItem -> stringResource(R.string.supplier_, searchItem.id)
            is AlertSearchItem -> stringResource(R.string.alert_for_, searchItem.alertType)
            is POLSearchItem -> stringResource(R.string.pol_, searchItem.id)
            is POSearchItem -> stringResource(R.string.po_, searchItem.id)
            else -> ""
        },
        style = MaterialTheme.typography.h3,
        color = MaterialTheme.colors.primary
    )
}

@Composable
fun SearchCardSimple(
    item: SearchItem,
    navigateTo: (SearchItem) -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clickable(onClick = { navigateTo(item) })
    ) {
        SearchImage(item, modifier = Modifier.padding(end = 16.dp))
        Column(modifier = Modifier.weight(1f)) {
            SearchTitle(item)
            when (item) {
                is ItemSearchItem ->
                    Text(
                        "${item.name}, ${item.description}",
                        style = MaterialTheme.typography.h4,
                        color = MaterialTheme.colors.secondary
                    )
                is SupplierSearchItem -> {
                    val contractColor = if (item.hasActiveContracts) Green40 else Lila40
                    val diversityColor = if (item.diversity) Green40 else Lila40
                    SplitText(
                        modifier = Modifier,
                        style = MaterialTheme.typography.h4,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                        SpanStyle(MaterialTheme.colors.primary) to item.name,
                        SpanStyle(contractColor) to stringResource(R.string.contract),
                        SpanStyle(diversityColor) to stringResource(R.string.diversity),
                    )
                }
                is AlertSearchItem -> {
                    val currentStateColor = AlertState
                    val flaggedColor = if (item.flagged) Green40 else Danger
                    SplitText(
                        modifier = Modifier,
                        style = MaterialTheme.typography.h4,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                        SpanStyle(MaterialTheme.colors.primary) to item.readStatus,
                        SpanStyle(currentStateColor) to item.currentState.name,
                        SpanStyle(flaggedColor) to stringResource(R.string.flagged),
                        SpanStyle(MaterialTheme.colors.primary) to item.description,
                    )
                }
                is POSearchItem -> {
                    val fulfilledColor = if (item.fulfilled) Green40 else Lila40
                    SplitText(
                        modifier = Modifier,
                        style = MaterialTheme.typography.h4,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                        SpanStyle(fulfilledColor) to item.fulfilledStr,
                        SpanStyle(MaterialTheme.colors.primary) to item.order.id,
                    )
                }
                is POLSearchItem -> {
                    val fulfilledColor = if (item.fulfilled) Green40 else Lila40
                    SplitText(
                        modifier = Modifier,
                        style = MaterialTheme.typography.h4,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                        SpanStyle(fulfilledColor) to item.fulfilledStr,
                        SpanStyle(MaterialTheme.colors.primary) to item.order.id,
                        SpanStyle(MaterialTheme.colors.primary) to item.itemVendorId,
                    )
                }
                else -> null
            }
        }
    }
}

@Composable
fun RecentSearch(
    item: RecentSearchItem,
    onClick: (RecentSearchItem) -> Unit
) {
    val searchFilters = stringArrayResource(R.array.searchFilters).toList()

    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = { onClick(item) })
    ) {
        val (
            icon,
            input,
            filters,
        ) = createRefs()
        Icon(
            imageVector = Icons.Filled.History,
            contentDescription = stringResource(R.string.cd_history),
            modifier = Modifier
                .constrainAs(icon) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    bottom.linkTo(parent.bottom)
                }
        )
        Text(
            item.input,
            style = MaterialTheme.typography.h3,
            color = MaterialTheme.colors.primary,
            modifier = Modifier
                .constrainAs(input) {
                    top.linkTo(parent.top)
                    start.linkTo(icon.end, margin = 16.dp)
                    bottom.linkTo(filters.top)
                }
        )
        Text(
            item.filters?.joinToString { searchFilters[it] } ?: "",
            style = MaterialTheme.typography.subtitle1,
            color = MaterialTheme.colors.secondary,
            modifier = Modifier
                .constrainAs(filters) {
                    top.linkTo(input.bottom)
                    start.linkTo(icon.end, margin = 16.dp)
                    bottom.linkTo(parent.bottom)
                }
        )
    }
}