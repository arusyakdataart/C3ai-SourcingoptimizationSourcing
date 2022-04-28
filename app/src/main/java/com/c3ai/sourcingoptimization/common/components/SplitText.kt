package com.c3ai.sourcingoptimization.common.components

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle

@Composable
fun SplitText(
    modifier: Modifier = Modifier,
    vararg texts: Pair<SpanStyle?, String>,
) {
    SplitText(modifier = modifier, texts = texts, maxLines = 1)
}

@Composable
fun SplitText(
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.subtitle1,
    overflow: TextOverflow = TextOverflow.Clip,
    maxLines: Int = Int.MAX_VALUE,
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
        style = style,
        overflow = overflow,
        maxLines = maxLines,
        modifier = modifier,
    )
}