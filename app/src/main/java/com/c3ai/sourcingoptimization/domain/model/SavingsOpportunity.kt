package com.c3ai.sourcingoptimization.domain.model

data class SavingsOpportunityItem(
    val result: AAA?
)

data class AAA(
    val item0: SavingsOpportunity?
)

data class SavingsOpportunity(
    val SavingsOpportunityCompound: SavingsOpportunityCompound?
)

data class SavingsOpportunityCompound(
    val type: String?,
    val count: Int?,
    val dates: List<String>?,
    val data: List<Double>?,
    val missing: List<Double>?,
    val unit: Unit?,
    val timeZone: String?,
    val interval: String?,
    val start: String?,
    val end: String?
)