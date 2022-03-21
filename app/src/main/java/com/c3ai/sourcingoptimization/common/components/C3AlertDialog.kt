package com.c3ai.sourcingoptimization.common.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material.AlertDialog
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.c3ai.sourcingoptimization.ui.theme.Accent

/**
 * AlertDialog for the App screens
 */
@Composable
fun C3AlertDialog(
    title: String,
    text: String,
    dismissButtonText: String,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = {
            onDismiss()
        },
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.h2,
                color = MaterialTheme.colors.primary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        text = {
            Text(
                text = text,
                style = MaterialTheme.typography.h4,
                color = MaterialTheme.colors.secondary
            )
        },
        confirmButton = {},
        dismissButton = {
            Text(
                text = dismissButtonText,
                style = MaterialTheme.typography.h3,
                color = Accent,
                modifier = Modifier
                    .padding(top = 0.dp, start = 16.dp, bottom = 16.dp, end = 16.dp)
                    .clickable {
                        onDismiss()
                    }
            )
        }
    )
}