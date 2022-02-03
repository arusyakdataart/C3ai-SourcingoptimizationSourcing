package com.c3ai.sourcingoptimization.data.network.requests

/**
 * Data class with parameters for PurchaseOrder[getPOLines] request
 * @see C3ApiService
 * */
data class POLinesDetailsParameters(
    @Transient val itemId: String
) : RequestParameters {

    override val spec: C3Spec = C3Spec(
        include = listOf(
            "name",
            "actualLeadTime",
            "orderCreationDate",
            "promisedDeliveryDate",
            "requestedDeliveryDate",
            "closedDate",
            "unitPrice",
            "fulfilled",
            "fulfilledStr",
            "unitPriceLocal",
            "requestedLeadTime",
            "plannedLeadTime",
            "totalCost",
            "totalQuantity",
            "vendor",
            "meta",
            "numberOfActiveAlerts",
            "order"
        ),
        filter = "id == '$itemId'"
    )
}