package com.c3ai.sourcingoptimization.domain.model

data class C3UnitValue(
    val unit: C3Unit,
    val value: Double
) {

    companion object
}

data class C3Unit(
    val id: String?,
    val symbol: String?,
    val concept: String?,
    val name: String?
) {

    companion object
}
