package com.c3ai.sourcingoptimization.common.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp

@Composable
fun LabeledValue(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    valueStyle: TextStyle = MaterialTheme.typography.subtitle2,
    valueColor: Color = MaterialTheme.colors.primary,
) {
    Column(modifier = modifier) {
        Text(label, style = MaterialTheme.typography.h5, color = MaterialTheme.colors.secondary)
        Text(value, style = valueStyle, color = valueColor, modifier = Modifier.padding(top = 4.dp))
    }
}