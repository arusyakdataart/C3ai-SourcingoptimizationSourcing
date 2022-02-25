package com.c3ai.sourcingoptimization.common.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.c3ai.sourcingoptimization.ui.theme.DividerColor

@Composable
fun ListDivider(modifier: Modifier = Modifier) {
    Divider(
        modifier = modifier.padding(vertical = 16.dp),
        color = DividerColor
    )
}