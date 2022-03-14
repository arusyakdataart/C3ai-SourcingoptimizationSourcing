package com.c3ai.sourcingoptimization.data.network.requests

import com.c3ai.sourcingoptimization.utilities.PAGINATED_RESPONSE_LIMIT

/**
 * Data class with parameters for Item[getItemsSupplied] request
 * @see C3ApiService
 * */
data class ItemSuppliedParameters(
    @Transient val vendorId: String,
    @Transient val limit: Int = PAGINATED_RESPONSE_LIMIT,
    @Transient val offset: Int = 0
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
        limit = limit,
        offset = offset,
        filter = "vendor.id == '$vendorId'"
    )
}