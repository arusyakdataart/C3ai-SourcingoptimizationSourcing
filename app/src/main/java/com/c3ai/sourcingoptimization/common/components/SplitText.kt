package com.c3ai.sourcingoptimization.common.components

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle


@Composable
fun SplitText(
    modifier: Modifier = Modifier,
    vararg texts: Pair<SpanStyle?, String>,
) {
    Text(
        buildAnnotatedString {
            texts.forEach {
                withStyle(it.first ?: SpanStyle(MaterialTheme.colors.primary)) {
                    append(it.second)
                }
                if (texts.last() != it) append(" • ")
            }
        },
        style = MaterialTheme.typography.subtitle1,
        modifier = modifier,
    )
}