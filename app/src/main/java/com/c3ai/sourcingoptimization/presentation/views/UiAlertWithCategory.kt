package com.c3ai.sourcingoptimization.presentation.views

import com.c3ai.sourcingoptimization.domain.model.C3Category

data class UiAlertWithCategory(
    val category: String,
    val alert: UiAlert? = null
)

data class UiAlert(
    val id: String,
    val alertType: String?,
    val category: C3Category?,
    val description: String,
    val currentState: C3Category?,
    val readStatus: String?,
    val flagged: Boolean?,
    val timestamp: String?,
    val redirectUrl: String?
)
