package com.c3ai.sourcingoptimization.presentation.views

data class SuppliersChart(
    val categories: List<String>,
    val data: List<Double>,
    val suppliersChartDataMaxValue: Double?,
    val dataLabelsFormat: String,
)
