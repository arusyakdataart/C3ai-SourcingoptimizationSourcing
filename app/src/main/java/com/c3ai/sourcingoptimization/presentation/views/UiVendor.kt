package com.c3ai.sourcingoptimization.presentation.views

import com.c3ai.sourcingoptimization.domain.model.C3Location

data class UiVendor(
    val id: String,
    val name: String,
    val allPOValue: String,
    val openPOValue: String,
    val closedPOValue: String,
    val avgPOValue: String,
    val active: Boolean,
    val diversity: Boolean,
    val hasActiveContracts: Boolean,
    val hasActiveAlerts: Boolean,
    val numberOfActiveAlerts: String,
    val location: C3Location?,
    val email: String?,
    val phone: String?,
    val items: List<UiItem>,
    val purchaseOrders: List<UiPurchaseOrder.Order>
)