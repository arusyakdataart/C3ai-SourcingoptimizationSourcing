package com.c3ai.sourcingoptimization.presentation.views.itemdetails

data class IndexPriceCharts(
    val categories: List<String>,
    val data: List<Double>,
    val maxValue: Double,
    val graphYearFormat: String,
    val dateText: String,
    val priceText: String,
)
