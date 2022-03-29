package com.c3ai.sourcingoptimization.domain.model

import java.util.*

data class Alert(
    val id: String,
    val alertType: String?,
    val category: C3Category?,
    val description: String,
    val currentState: C3Category?,
    val readStatus: String?,
    val flagged: Boolean?,
    val timestamp: Date?,
    val redirectUrl: String?
)
