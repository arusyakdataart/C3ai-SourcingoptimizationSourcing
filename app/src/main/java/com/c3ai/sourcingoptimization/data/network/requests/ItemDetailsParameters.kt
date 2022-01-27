package com.c3ai.sourcingoptimization.data.network.requests

data class ItemDetailsParameters(
    @Transient val itemId: String
) : RequestParameters {

    override val spec: C3Spec = C3Spec(
        include = listOf(
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
            "currentInventory"
        ),
        filter = "id == '$itemId'"
    )
}