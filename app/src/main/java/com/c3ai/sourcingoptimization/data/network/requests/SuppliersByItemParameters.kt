package com.c3ai.sourcingoptimization.data.network.requests

/**
 * Data class with parameters for Supplier[getSuppliersByItem] request
 * @see C3ApiService
 * */
data class SuppliersByItemParameters(
    @Transient val itemId: String
) : RequestParameters {

    override val spec: C3Spec = C3Spec(
        include = listOf(
            "id",
            "name",
            "allPOValue",
            "openPOValue",
            "hasActiveAlerts",
            "hasActiveContracts",
            "numberOfActiveAlerts",
            "diversity",
            "active",
            "location.city",
            "location.region",
            "location.address",
            "items",
            "purchaseOrders"
        ),
        filter = "items.id == '$itemId'"
    )
}