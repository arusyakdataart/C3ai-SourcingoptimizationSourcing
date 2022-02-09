package com.c3ai.sourcingoptimization.presentation.models

import com.c3ai.sourcingoptimization.domain.model.C3Item
import com.c3ai.sourcingoptimization.domain.model.C3Location
import com.c3ai.sourcingoptimization.domain.model.UnitValue

data class UiVendor(
    val id: String,
    val name: String,
    val allPOValue: String,
    val active: Boolean,
    val diversity: Boolean,
    val hasActiveContracts: Boolean,
    val location: C3Location?,
    val items: List<C3Item>,
    val purchaseOrders: List<UiPurchaseOrder.Order>
)