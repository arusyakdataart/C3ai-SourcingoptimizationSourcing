package com.c3ai.sourcingoptimization.common.components

import android.content.res.Configuration
import androidx.annotation.DrawableRes
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.c3ai.sourcingoptimization.ui.theme.Gray40

@Composable
fun TutorialChip(modifier: Modifier = Modifier, text: String, color: Color = Color(0xff00BCD4)) {
    Card(
        elevation = 0.dp,
        modifier = modifier,
        backgroundColor = Color(0xFFE0E0E0),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(start = 8.dp, top = 4.dp, end = 8.dp, bottom = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp, 8.dp)
                    .clip(CircleShape)
                    .background(color = color)
            )
            Spacer(Modifier.width(4.dp))
            Text(text = text)
        }
    }
}

@Composable
fun Chip(
    modifier: Modifier = Modifier,
    text: String,
    @DrawableRes drawableRes: Int = -1,
    cancelable: Boolean = false
) {

    Surface(
        elevation = 0.dp,
        color = MaterialTheme.colors.background,
        shape = RoundedCornerShape(16.dp),
        modifier = modifier
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (drawableRes != -1) {
                Image(
                    painter = painterResource(drawableRes),
                    modifier = Modifier
                        .padding(8.dp)
                        .size(20.dp)
                        .clip(CircleShape),
                    contentDescription = null
                )
            }
            Text(
                text = text,
                style = MaterialTheme.typography.body2,
                modifier = Modifier.padding(end = 8.dp)
            )

            if (cancelable) {
                CircleCloseButton(Modifier.padding(end = 8.dp))
            }
        }
    }
}

@Composable
fun CancelableChip(
    modifier: Modifier = Modifier,
    title: String,
    @DrawableRes drawableRes: Int = -1,
    onClick: ((String) -> Unit)? = null,
    onCancel: ((String) -> Unit)? = null
) {

    Surface(
        elevation = 0.dp,
        modifier = modifier,
        color = Color(0xFFE0E0E0),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clickable {
                    onClick?.run {
                        invoke(title)
                    }
                }
                .padding(vertical = 8.dp, horizontal = 10.dp)
        ) {

            if (drawableRes != -1) {
                Image(
                    painter = painterResource(drawableRes),
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .size(20.dp)
                        .clip(CircleShape),
                    contentDescription = null
                )
            }

            Text(
                text = title,
                style = MaterialTheme.typography.body2,
                modifier = Modifier.padding(end = 8.dp)
            )

            Surface(color = Color.DarkGray, modifier = Modifier, shape = CircleShape) {
                IconButton(
                    onClick = {
                        onCancel?.run {
                            invoke(title)
                        }
                    },
                    modifier = Modifier
                        .size(16.dp)
                        .padding(1.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        tint = Color(0xFFE0E0E0),
                        contentDescription = null
                    )
                }
            }
        }
    }
}


@Composable
fun OutlinedChip(
    modifier: Modifier = Modifier,
    text: String,
    selected: Boolean = false,
    @DrawableRes drawableRes: Int = -1,
    closable: Boolean = false,
    onClick: ((String) -> Unit)? = null,
) {
    Surface(
        elevation = 0.dp,
        border = BorderStroke(
            1.dp,
            if (selected) Gray40 else MaterialTheme.colors.onBackground
        ),
        shape = RoundedCornerShape(16.dp),
        modifier = modifier.clickable { onClick?.invoke(text) },
        color = if (selected) Gray40 else MaterialTheme.colors.surface
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (drawableRes != -1) {
                Image(
                    painter = painterResource(drawableRes),
                    modifier = Modifier
                        .padding(8.dp)
                        .size(20.dp)
                        .clip(CircleShape),
                    contentDescription = null
                )
            }
            Text(
                text,
                style = MaterialTheme.typography.h4,
                color = if (selected) Color.Black else Gray40,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
            )

            if (closable) {
                CircleCloseButton(Modifier.padding(end = 8.dp))
            }
        }
    }
}

@Composable
fun CircleCloseButton(modifier: Modifier = Modifier) {
    Surface(color = Color.DarkGray, modifier = modifier, shape = CircleShape) {
        IconButton(
            onClick = {},
            modifier = Modifier
                .size(16.dp)
                .padding(1.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Close,
                tint = Color(0xFFE0E0E0),
                contentDescription = null
            )
        }
    }
}

@Composable
@Preview
@Preview("dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(device = Devices.PIXEL_C)
private fun TutorialChipReview() {
    TutorialChip(text = "Tutorial Chip")
}

@Composable
@Preview
@Preview("dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(device = Devices.PIXEL_C)
private fun SuggestionChipReview() {
    CancelableChip(title = "Suggestion")
}