package com.c3ai.sourcingoptimization.common.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
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
    image1: Int? = null,
    image2: Int? = null

) {
    ConstraintLayout(modifier = modifier.fillMaxWidth()) {
        val (header, image, titleText, subtitleText, icon1, icon2) = createRefs()
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
                    top.linkTo(titleText.bottom)
                    start.linkTo(image.end, margin = 16.dp)
                }
        )
        if (image1 != null) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .padding(start = 16.dp)
                    .size(24.dp)
                    .constrainAs(icon1) {
                        top.linkTo(header.bottom, margin = 16.dp)
                        end.linkTo(parent.end)
                    }) {
                Icon(
                    painter = painterResource(id = image1),
                    contentDescription = null
                )
            }
        }

        if (image2 != null) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(24.dp)
                    .constrainAs(icon2) {
                        top.linkTo(header.bottom, margin = 16.dp)
                        end.linkTo(icon1.start)
                    }) {
                Icon(
                    painter = painterResource(id = image2),
                    contentDescription = null
                )
            }
        }
    }
}