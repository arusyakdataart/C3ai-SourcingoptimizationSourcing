package com.c3ai.sourcingoptimization.domain.model

data class Location(
    val region: String,
    val city: String,
    val address: String,
    val state: String,
) {

    override fun toString(): String {
        return city
    }
}