package com.c3ai.sourcingoptimization.common.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp

@Composable
inline fun <T> CollapsingContentList(
    modifier: Modifier = Modifier,
    contentModifier: Modifier = Modifier,
    items: List<T>,
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
        items(items = items, itemContent = itemContent)
    }
}