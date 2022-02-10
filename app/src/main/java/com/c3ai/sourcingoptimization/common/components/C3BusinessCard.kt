package com.c3ai.sourcingoptimization.common.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.c3ai.sourcingoptimization.ui.theme.Gray70


@Composable
fun BusinessCard(
    modifier: Modifier = Modifier,
    label: String,
    title: String,
    subtitle: String,
) {
    ConstraintLayout(modifier = modifier.fillMaxWidth()) {
        val (header, image, titleText, subtitleText) = createRefs()
        val nameShort = title.split(" ")
            .joinToString("") { it[0].toString() }.uppercase()
        Text(
            label.uppercase(),
            style = MaterialTheme.typography.subtitle2,
            color = MaterialTheme.colors.secondary,
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(header) {
                    top to parent.top
                    start to parent.start
                }
        )
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Gray70)
                .constrainAs(image) {
                    top.linkTo(header.bottom, margin = 16.dp)
                    start to parent.start
                }) {
            Text(
                nameShort,
                style = MaterialTheme.typography.h1,
                color = MaterialTheme.colors.primary,
            )
        }
        Text(
            title,
            style = MaterialTheme.typography.h3,
            color = MaterialTheme.colors.primary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .constrainAs(titleText) {
                    top.linkTo(header.bottom, margin = 16.dp)
                    bottom to subtitleText.top
                    start.linkTo(image.end, margin = 16.dp)
                }
        )
        Text(
            subtitle,
            style = MaterialTheme.typography.subtitle1,
            color = MaterialTheme.colors.secondary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .constrainAs(subtitleText) {
                    top to titleText.bottom
                    bottom to parent.bottom
                    start.linkTo(image.end, margin = 16.dp)
                }
        )
    }
}