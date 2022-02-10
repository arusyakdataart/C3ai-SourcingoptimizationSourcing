package com.c3ai.sourcingoptimization.common.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer

@OptIn(ExperimentalFoundationApi::class)
@Composable
inline fun <T> CollapsingContentList(
    modifier: Modifier = Modifier,
    contentModifier: Modifier = Modifier,
    items: List<T>,
    noinline header: @Composable (LazyItemScope.() -> Unit)? = null,
    crossinline content: @Composable BoxScope.() -> Unit,
    crossinline itemContent: @Composable LazyItemScope.(item: T) -> Unit
) {
    val lazyListState = rememberLazyListState()
    var scrolledY = 0f
    var previousOffset = 0
    LazyColumn(
        modifier.fillMaxSize(),
        lazyListState,
    ) {
        item {
            Box(
                modifier = contentModifier
                    .graphicsLayer {
                        scrolledY += lazyListState.firstVisibleItemScrollOffset - previousOffset
                        translationY = scrolledY * 0.5f
                        previousOffset = lazyListState.firstVisibleItemScrollOffset
                    }
                    .fillMaxWidth(),
                content = content
            )
        }
        header?.let { stickyHeader(content = header) }
        items(items = items, itemContent = itemContent)
    }
}