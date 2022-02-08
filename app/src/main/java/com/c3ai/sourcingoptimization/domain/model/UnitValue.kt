package com.c3ai.sourcingoptimization.domain.model

data class UnitValue(
    val unit: Unit,
    val value: Double
) {

    val valueString: String
        get() = unit.id + String.format("%.3f", value)

    companion object
}

data class Unit(
    val id: String
) {

    override fun toString(): String {
        return id
    }
}
