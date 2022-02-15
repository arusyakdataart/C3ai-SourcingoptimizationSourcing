package com.c3ai.sourcingoptimization.domain.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ItemVendorRelationMetrics(
    @SerializedName("id")
    @Expose
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