package com.c3ai.sourcingoptimization.presentation.views

import com.c3ai.sourcingoptimization.domain.model.InventoryParams
import com.c3ai.sourcingoptimization.domain.model.Value

data class UiItem(
    val id: String,
    val name: String?,
    val description: String?,
    val family: String?,
    val numberOfOpenOrders: Int?,
    val latestOrderLineDate: String,
    val lastUnitPricePaid: String,
    val averageUnitPricePaid: String,
    val minimumUnitPricePaid: String,
    val itemFacilityInventoryParams: List<InventoryParams>?,
    val currentInventory: String,
    val unfulfilledOrderQuantity: String,
    val unfulfilledOrderCost: String,
    val numberOfVendors: Int?,
    val recentPoLinesCost: String,
    val minPoLinesUnitPrice: String,
    val weightedAveragePoLineUnitPrice: String,
    val hasActiveAlerts: Boolean?,
    val numberOfActiveAlerts: String,
)