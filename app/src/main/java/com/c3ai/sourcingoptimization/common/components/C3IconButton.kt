package com.c3ai.sourcingoptimization.common.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.c3ai.sourcingoptimization.ui.theme.Warning

@Composable
fun C3IconButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    badgeText: String = "",
    content: @Composable () -> Unit
) {
    IconButton(
        onClick = onClick,
        modifier.size(40.dp)
    ) {
        Box(contentAlignment = Alignment.TopEnd) {
            content()
            if (badgeText.isNotEmpty()) {
                Text(
                    text = badgeText,
                    style = MaterialTheme.typography.h5,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .size(14.dp)
                        .clip(CircleShape)
                        .background(Warning)
                )
            }
        }
    }
}