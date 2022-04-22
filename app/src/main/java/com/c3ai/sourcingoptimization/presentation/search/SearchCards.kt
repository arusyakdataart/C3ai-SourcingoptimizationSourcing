package com.c3ai.sourcingoptimization.presentation.search

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.c3ai.sourcingoptimization.R
import com.c3ai.sourcingoptimization.domain.model.RecentSearchItem
import com.c3ai.sourcingoptimization.domain.model.SearchItem


@Composable
fun SearchImage(
    imageVector: ImageVector = Icons.Filled.History,
    modifier: Modifier = Modifier
) {
    Image(
        imageVector = imageVector,
        contentDescription = null, // decorative
        modifier = modifier
            .size(20.dp, 20.dp)
            .clip(MaterialTheme.shapes.small)
    )
}

@Composable
fun SearchTitle(searchItem: SearchItem) {
    Text(searchItem.name, style = MaterialTheme.typography.subtitle1)
}

@Composable
fun SearchCardSimple(
    searchItem: SearchItem,
    navigateTo: (String) -> Unit,
    onToggleFavorite: () -> Unit
) {
    Row(
        modifier = Modifier
            .clickable(onClick = { navigateTo(searchItem.id) })
            .padding(16.dp)
            .semantics {
                // By defining a custom action, we tell accessibility services that this whole
                // composable has an action attached to it. The accessibility service can choose
                // how to best communicate this action to the user.
                customActions = listOf(
                    CustomAccessibilityAction(
                        label = "",
                        action = { onToggleFavorite(); true }
                    )
                )
            }
    ) {
        SearchImage(modifier = Modifier.padding(end = 16.dp))
        Column(modifier = Modifier.weight(1f)) {
            SearchTitle(searchItem)
        }
    }
}

@Composable
fun RecentSearch(item: RecentSearchItem) {
    val searchFilters = stringArrayResource(R.array.searchFilters).toList()

    ConstraintLayout {
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
            item.filters.joinToString { searchFilters[it] },
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