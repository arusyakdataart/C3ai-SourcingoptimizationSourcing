package com.c3ai.sourcingoptimization.common.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.c3ai.sourcingoptimization.ui.theme.Accent


@Composable
fun Tabs(vararg items: TabItem) {
    var tabIndex by remember { mutableStateOf(0) }
    Surface(
        elevation = 6.dp,
        color = MaterialTheme.colors.background,
    ) {
        ScrollableTabRow(
            selectedTabIndex = tabIndex,
            backgroundColor = Color.Transparent,
            edgePadding = 0.dp,
            indicator = @Composable { tabPositions ->
                TabRowDefaults.Indicator(
                    color = Accent,
                    modifier = Modifier.tabIndicatorOffset(tabPositions[tabIndex])
                )
            },
            divider = @Composable {},
        ) {
            items.forEachIndexed { index, item ->
                Tab(
                    selected = tabIndex == index,
                    onClick = {
                        tabIndex = index
                        item.onTabItemSelected()
                    },
                    text = { Text(text = item.title) })
            }
        }
    }
}

data class TabItem(
    val title: String,
    val onTabItemSelected: () -> Unit
)