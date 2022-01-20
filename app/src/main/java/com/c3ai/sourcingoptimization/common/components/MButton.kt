package com.c3ai.sourcingoptimization.common.components

import androidx.compose.foundation.layout.height
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.unit.dp

@Composable
fun MButton(
    modifier: Modifier,
    enabled: Boolean = true,
    text: String,
    onClick: () -> Unit
) {
    Button(
        modifier = Modifier.composed { modifier }
            .height(40.dp),
        enabled = enabled,
        content = { Text(text = text, color = MaterialTheme.colors.onPrimary) },
        onClick = onClick
    )
}