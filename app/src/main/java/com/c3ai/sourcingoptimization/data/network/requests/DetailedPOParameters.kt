package com.c3ai.sourcingoptimization.data.network.requests

/**
 * Data class with parameters for PurchaseOrder[getDetailedPO] request
 * @see C3ApiService
 * */
data class DetailedPOParameters(
    @Transient val itemId: String
) : RequestParameters {

    override val spec: C3Spec = C3Spec(
        include = listOf(
            "id",
            "name",
            "totalCost",
            "orderCreationDate",
            "closedDate",
            "fulfilledStr",
            "buyer.id",
            "buyer.name",
            "vendor.id",
            "vendor.name",
            "vendor.numberOfActiveAlerts",
            "vendor.location.address"
        ),
        filter = "id == '$itemId'"
    )
}