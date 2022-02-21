package com.c3ai.sourcingoptimization.domain.model

data class C3Item(
    val id: String,
    val name: String?,
    val description: String?,
    val family: String?,
    val numberOfOpenOrders: Int?,
    val latestOrderLineDate: String?,
    val lastUnitPricePaid: C3UnitValue?,
    val averageUnitPricePaid: C3UnitValue?,
    val lastUnitPriceLocalPaid: C3UnitValue?,
    val averageUnitPriceLocalPaid: C3UnitValue?,
    val minimumUnitPricePaid: C3UnitValue?,
    val minimumUnitPriceLocalPaid: C3UnitValue?,
    val itemFacilityInventoryParams: List<InventoryParams>?,
    val currentInventory: Value?,
    val unfulfilledOrderQuantity: C3UnitValue?,
    val unfulfilledOrderCost: C3UnitValue?,
    val numberOfVendors: Int?,
    val recentPoLinesCost: C3UnitValue?,
    val minPoLinesUnitPrice: C3UnitValue?,
    val weightedAveragePoLineUnitPrice: C3UnitValue?,
    val hasActiveAlerts: Boolean?,
    val numberOfActiveAlerts: Int,
) {

    companion object
}

data class InventoryParams(
    val item: Id?,
    val quantityInStock: Value?,
    val id: String?,
    val version: Int?
)

data class Id(
    val id: String
)

data class Value(
    val value: Int?
)
