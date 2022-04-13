package com.c3ai.sourcingoptimization.domain.model

import com.google.gson.annotations.SerializedName

data class ItemMarketPriceIndexRelationMetrics(
    val result: Map<String, ItemMarketPriceIndexRelationMetric>
)

data class ItemMarketPriceIndexRelationMetric(
    @SerializedName("IndexPrice")
    val indexPrice: IndexPrice
)

data class IndexPrice(
    val count: Int,
    val dates: List<String>,
    val data: List<Double>,
    val missing: List<Int>,
    val timeZone: String,
    val interval: String,
    val start: String,
    val end: String,
    var indexId: String?,
    var indexName: String?
)