package com.c3ai.sourcingoptimization.domain.model

data class AlertFeedback(
    val id: String,
    var helpful: Boolean?,
    val parent: C3Number?
)