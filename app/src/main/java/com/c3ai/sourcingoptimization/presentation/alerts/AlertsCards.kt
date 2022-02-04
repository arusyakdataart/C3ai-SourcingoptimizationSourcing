package com.c3ai.sourcingoptimization.presentation.alerts

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.c3ai.sourcingoptimization.domain.model.Alert
import com.c3ai.sourcingoptimization.domain.model.SearchItem

@Composable
fun AlertCardSimple(
    alert: Alert,
    navigateTo: (String) -> Unit,
    onToggleFavorite: () -> Unit
) {
    Row(
        modifier = Modifier
            .clickable(onClick = { navigateTo(alert.id) })
            .padding(16.dp)
            .semantics {
                // By defining a custom action, we tell accessibility services that this whole
                // composable has an action attached to it. The accessibility service can choose
                // how to best communicate this action to the user.
                customActions = listOf(
                    CustomAccessibilityAction(
                        label = "",
                        action = { onToggleFavorite(); true }
                    )
                )
            }
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(alert.title, style = MaterialTheme.typography.subtitle1)
        }
    }
}