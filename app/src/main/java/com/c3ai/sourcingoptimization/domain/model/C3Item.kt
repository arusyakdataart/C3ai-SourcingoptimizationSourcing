package com.c3ai.sourcingoptimization.domain.model

data class C3Item(
    val id: String,
    val name: String? = "",
    val description: String? = "",
    val family: String? = "",
    val numberOfOpenOrders: Int? = null,
    val latestOrderLineDate: String? = "",
    val lastUnitPricePaid: C3UnitValue? = null,
    val averageUnitPricePaid: C3UnitValue? = null,
    val lastUnitPriceLocalPaid: C3UnitValue? = null,
    val averageUnitPriceLocalPaid: C3UnitValue? = null,
    val minimumUnitPricePaid: C3UnitValue? = null,
    val minimumUnitPriceLocalPaid: C3UnitValue? = null,
    val itemFacilityInventoryParams: List<InventoryParams>? = emptyList(),
    val currentInventory: C3UnitValue? = null,
    val unfulfilledOrderQuantity: C3UnitValue? = null,
    val unfulfilledOrderCost: C3UnitValue? = null,
    val numberOfVendors: Int? = null,
    val recentPoLinesCost: C3UnitValue? = null,
    val minPoLinesUnitPrice: C3UnitValue? = null,
    val weightedAveragePoLineUnitPrice: C3UnitValue? = null,
    val hasActiveAlerts: Boolean? = null,
    val numberOfActiveAlerts: Int? = null,
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
    val value: Double?
)
