package com.c3ai.sourcingoptimization.domain.model

data class Vendor(
    val id: String,
    val name: String,
    val numberOfActiveAlerts: Int,
    val location: Location
)