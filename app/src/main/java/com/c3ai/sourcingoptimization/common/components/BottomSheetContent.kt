package com.c3ai.sourcingoptimization.common.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.c3ai.sourcingoptimization.ui.theme.BackgroundVariantColor

@Composable
fun BottomSheetContent(
    vararg items: BottomSheetItem
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(144.dp)
            .background(BackgroundVariantColor)
    ) {
        items.map {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable {
                    it.onClick()
                }
                    .weight(1f)
                    .fillMaxWidth()
                    .height(48.dp)
                    .padding(horizontal = 16.dp)
            ) {
                Icon(
                    imageVector = it.image,
                    contentDescription = it.contentDescription
                )
                Text(
                    it.text,
                    style = MaterialTheme.typography.h4,
                    color = MaterialTheme.colors.secondaryVariant,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }
        }
    }
}

data class BottomSheetItem(
    val image: ImageVector,
    val contentDescription: String,
    val text: String,
    val onClick: () -> Unit
)
