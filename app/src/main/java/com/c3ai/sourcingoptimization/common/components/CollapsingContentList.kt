package com.c3ai.sourcingoptimization.common.components

import androidx.compose.animation.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.c3ai.sourcingoptimization.R
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class, ExperimentalAnimationApi::class)
@Composable
inline fun <T> CollapsingContentList(
    modifier: Modifier = Modifier,
    contentModifier: Modifier = Modifier,
    items: List<T>,
    reverseLayout: Boolean = false,
    verticalArrangement: Arrangement.Vertical =
        if (!reverseLayout) Arrangement.Top else Arrangement.Bottom,
    noinline header: @Composable (LazyItemScope.() -> Unit)? = null,
    crossinline content: @Composable BoxScope.() -> Unit,
    crossinline itemContent: @Composable LazyItemScope.(item: T) -> Unit
) {
    Box {
        val coroutineScope = rememberCoroutineScope()
        val listState = rememberLazyListState()
        var scrolledY = 0f
        var previousOffset = 0
        LazyColumn(
            modifier.fillMaxSize(),
            listState,
            reverseLayout = reverseLayout,
            verticalArrangement = verticalArrangement
        ) {
            item {
                Box(
                    modifier = contentModifier
                        .graphicsLayer {
                            scrolledY += listState.firstVisibleItemScrollOffset - previousOffset
                            translationY = scrolledY * 0.5f
                            previousOffset = listState.firstVisibleItemScrollOffset
                        }
                        .fillMaxWidth(),
                    content = content
                )
            }
            header?.let { stickyHeader(content = header) }
            items(items = items, itemContent = itemContent)
        }
        val showButton by remember {
            derivedStateOf { listState.firstVisibleItemIndex > 0 }
        }
        AnimatedVisibility(
            visible = showButton,
            enter = fadeIn() + scaleIn(),
            exit = scaleOut() + fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(12.dp),
        ) {
            FloatingActionButton(
                onClick = {
                    coroutineScope.launch {
                        // Animate scroll to the first item
                        listState.animateScrollToItem(index = 0)
                    }
                },
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowUpward,
                    contentDescription = stringResource(R.string.cd_arrow_up)
                )
            }
        }
    }
}