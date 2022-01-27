package com.c3ai.sourcingoptimization.data.network.requests

data class ItemDetailsRequest (
    val include: List<String> = listOf(
        "description",
        "family",
        "numberOfOpenOrders",
        "latestOrderLineDate",
        "lastUnitPricePaid",
        "averageUnitPricePaid",
        "lastUnitPriceLocalPaid",
        "averageUnitPriceLocalPaid",
        "minimumUnitPricePaid",
        "minimumUnitPriceLocalPaid",
        "unfulfilledOrderQuantity",
        "unfulfilledOrderCost",
        "numberOfVendors",
        "recentPoLinesCost",
        "minPoLinesUnitPrice",
        "weightedAveragePoLineUnitPrice",
        "hasActiveAlerts",
        "numberOfActiveAlerts",
        "id",
        "name",
        "meta",
        "currentInventory"),
    val itemId: String
) {
    val filter = "id == $itemId"
}