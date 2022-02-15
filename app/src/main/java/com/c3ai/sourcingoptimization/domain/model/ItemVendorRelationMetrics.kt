package com.c3ai.sourcingoptimization.domain.model

data class ItemVendorRelationMetrics(
    val result: Map<String, ItemVendorRelationMetric>
)

data class ItemVendorRelationMetric(
    val OrderLineValue: OrderLineValue
)

data class OrderLineValue(
    val type: String,
    val count: Int,
    val dates: List<String>,
    val data: List<Double>,
    val missing: List<Int>,
    val unit: C3Unit,
    val meta: Meta,
    val timeZone: String,
    val interval: String,
    val start: String,
    val end: String
)