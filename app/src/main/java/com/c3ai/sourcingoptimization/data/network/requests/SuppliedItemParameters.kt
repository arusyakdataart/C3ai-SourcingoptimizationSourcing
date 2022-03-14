package com.c3ai.sourcingoptimization.data.network.requests

import com.c3ai.sourcingoptimization.utilities.PAGINATED_RESPONSE_LIMIT

/**
 * Data class with parameters for Supplier[getSuppliedItems] request
 * @see C3ApiService
 * */
data class SuppliedItemParameters(
    @Transient val vendorId: String,
    @Transient val order: String,
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
        order = order,
        filter = "vendor.id == '$vendorId'"
    )
}