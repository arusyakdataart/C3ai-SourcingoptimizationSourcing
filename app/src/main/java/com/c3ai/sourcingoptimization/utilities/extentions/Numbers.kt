package com.c3ai.sourcingoptimization.utilities.extentions

import java.text.DecimalFormat


fun Double.formatNumberLocal(): Double {
    val dec = DecimalFormat("#,###.##")
    val formattedNumber = dec.format(this / 1000000)
    return formattedNumber.toDouble()
}