package com.c3ai.sourcingoptimization.data.network.requests

/**
 * Data class with parameters for PurchaseOrder[getDetailedPO] request
 * @see C3ApiService
 * */
data class DetailedPOParameters(
    @Transient val itemIds: List<String>
) : RequestParameters {

    override val spec: C3Spec = C3Spec(
        include = listOf(
            "vendor.id",
            "vendor.name",
            "vendor.numberOfActiveAlerts",
            "vendor.location.region",
            "vendor.location.city",
            "vendor.location.address",
            "vendor.location.state",
            "buyer.id",
            "buyer.name",
            "to.name",
            "to.id",
            "to.city",
            "to.geometry",
            "to.address",
            "to.address1",
            "to.postal_code",
            "to.state"
        ),
        filter = itemIds.joinToString { "id == '$it'||" }
    )
}