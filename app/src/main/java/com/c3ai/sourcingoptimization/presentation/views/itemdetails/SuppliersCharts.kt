package com.c3ai.sourcingoptimization.presentation.views.itemdetails

data class SuppliersCharts(
    val categories: List<String>,
    val data: List<Double>,
    val maxValue: Double?,
    val dataLabelsFormat: String,
    val suppliers: ChartSuppliers?
)

data class ChartSuppliers(
    val ids: List<String>?,
    val chartData: Map<String, String>?
)
