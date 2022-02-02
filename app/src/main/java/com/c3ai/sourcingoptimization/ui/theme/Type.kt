package com.c3ai.sourcingoptimization.ui.theme

import androidx.compose.material.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.c3ai.sourcingoptimization.R

private val Inter = FontFamily(
    Font(R.font.inter_regular),
    Font(R.font.inter_medium, FontWeight.W500),
    Font(R.font.inter_semibold, FontWeight.W600)
)
// Set of Material typography styles to start with
val Typography = Typography(
    defaultFontFamily = Inter,
    body1 = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp
    ),
    subtitle1 = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp
    ),
    button = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
    ),
    caption = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp
    )
)