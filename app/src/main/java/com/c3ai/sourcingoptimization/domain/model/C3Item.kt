package com.c3ai.sourcingoptimization.domain.model

data class C3Items(
    val objs: List<C3Item>?,
    val count: Int,
    val hasMore: Boolean
)

data class C3Item(
    val description: String?,
    val family: String?,
    val numberOfOpenOrders: Int?,
    val latestOrderLineDate: String?,
    val lastUnitPricePaid: UnitValue?,
    val averageUnitPricePaid: UnitValue?,
    val lastUnitPriceLocalPaid: UnitValue?,
    val averageUnitPriceLocalPaid: UnitValue?,
    val minimumUnitPricePaid: UnitValue?,
    val minimumUnitPriceLocalPaid: UnitValue?,
    val itemFacilityInventoryParams: List<InventoryParams>?,
    val currentInventory: Value?,
    val unfulfilledOrderQuantity: UnitValue?,
    val unfulfilledOrderCost: UnitValue?,
    val numberOfVendors: Int?,
    val recentPoLinesCost: UnitValue?,
    val minPoLinesUnitPrice: UnitValue?,
    val weightedAveragePoLineUnitPrice: UnitValue?,
    val hasActiveAlerts: Boolean?,
    val numberOfActiveAlerts: Int?,
    val id: String?,
    val name: String?
)

data class UnitValue(
    val value: Double?,
    val unit: Unit?
)

data class InventoryParams(
    val item: InventoryItem?,
    val quantityInStock: Value?,
    val id: String?,
    val version: Int?
)

data class InventoryItem(
    val id: String?
)

data class Value(
    val value: Int?
)
