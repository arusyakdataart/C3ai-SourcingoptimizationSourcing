package com.c3ai.sourcingoptimization.domain.model

data class MarketPriceIndex(
    val currency: Id?,
    val id: String,
    val name: String,
    val version: Int?
)