package com.c3ai.sourcingoptimization.presentation.item_details.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.compose.collectAsLazyPagingItems
import com.c3ai.sourcingoptimization.common.components.PoLinesListExpanded
import com.c3ai.sourcingoptimization.presentation.item_details.ItemDetailsUiState


/**
 * Decomposition of item details[ItemDetailsDataScreen] with separate component for POLines tab
 * to make a code supporting easier[POLinesComponent].
 * */
@Composable
fun POLinesComponent(
    uiState: ItemDetailsUiState.HasItem,
    loadData: () -> Unit,
    onAlertsClick: (String) -> Unit,
) {
    LaunchedEffect(uiState.itemId) {
        loadData()
    }

    val lazyItems = uiState.poLines?.collectAsLazyPagingItems()
    val listState = rememberLazyListState()

    lazyItems?.let { items ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            listState,
            contentPadding = PaddingValues(horizontal = 16.dp),
        ) {
            items(items.itemCount) { index ->
                val item = items[index]!!
                PoLinesListExpanded(
                    item,
                    onPOAlertsClick = onAlertsClick
                )
            }
        }
    }
}