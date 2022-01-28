package com.c3ai.sourcingoptimization.common.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Checkbox
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.unit.dp

/**
 * A button component for the application, styled by application theme[C3AppTheme].
 * @see MaterialTheme
 * @see Button
 * */
@Composable
fun SharedPrefsToggle(
    text: String,
    value: Boolean,
    onValueChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = Modifier
            .padding(24.dp)
            .composed { modifier }
    ) {
        Checkbox(checked = value, onCheckedChange = onValueChanged)
        Text(text)
    }
}