package com.c3ai.sourcingoptimization.domain.model

data class RecentSearchItem(
    val input: String,
    val filters: Set<Int>
)
