package com.c3ai.sourcingoptimization.data.network.requests

/**
 * Data class with parameters for Item[getEvalMetrics] request
 * @see C3ApiService
 * */
data class EvalMetricsParameters(
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