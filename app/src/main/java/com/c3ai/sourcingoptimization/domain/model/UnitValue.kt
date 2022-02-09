package com.c3ai.sourcingoptimization.domain.model

data class UnitValue(
    val unit: Unit,
    val value: Double
) {

    companion object
}

data class Unit(
    val id: String
)
