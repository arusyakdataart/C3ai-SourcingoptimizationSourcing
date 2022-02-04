package com.c3ai.sourcingoptimization.domain.model

data class C3Supplier(
    val id: String,
    val name: String,
    val active: Boolean,
    val diversity: Boolean,
    val hasActiveContracts: Boolean,
    val items: List<C3Item>,
    val purchaseOrders: List<POLine>
)