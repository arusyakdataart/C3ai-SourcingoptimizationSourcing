package com.c3ai.sourcingoptimization.common.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * A button component for the application, styled by application theme[C3AppTheme].
 * @see MaterialTheme
 * @see Button
 * */
@Composable
fun PButton(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    text: String,
    onClick: () -> Unit
) {
    OutlinedButton(
        modifier = modifier
            .fillMaxWidth()
            .height(40.dp),
        border = BorderStroke(1.dp, MaterialTheme.colors.onBackground),
        enabled = enabled,
        onClick = onClick,
        colors = ButtonDefaults.outlinedButtonColors(
            backgroundColor = Color.Transparent
        )
    ) { Text(text) }
}

/**
 * A button component for the application, styled by application theme[C3AppTheme].
 * @see MaterialTheme
 * @see Button
 * */
@Composable
fun SButton(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    text: String,
    onClick: () -> Unit
) {
    Button(
        modifier = modifier
            .height(40.dp),
        enabled = enabled,
        onClick = onClick
    ) { Text(text = text, color = MaterialTheme.colors.onPrimary) }
}