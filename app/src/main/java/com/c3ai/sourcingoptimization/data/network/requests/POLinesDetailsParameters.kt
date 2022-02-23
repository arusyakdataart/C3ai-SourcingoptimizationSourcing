package com.c3ai.sourcingoptimization.data.network.requests

/**
 * Data class with parameters for PurchaseOrder[getPOLines] request
 * @see C3ApiService
 * */
data class POLinesDetailsParameters(
    @Transient val orderId: String
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
            "numberOfActiveAlerts",
            "order.vendor.name",
            "order.vendor.numberOfActiveAlerts",
            "order.vendor.location.region",
            "order.vendor.location.city",
            "order.vendor.location.address",
            "order.vendor.location.state",
            "order.buyer.name",
            "order.buyer.location",
            "order.to.name",
            "order.to.city",
            "order.to.geometry",
            "order.to.address",
            "order.to.address1",
            "order.to.postal_code",
            "order.to.state",
            "order.to.location",
            "order.to.numberOfActiveAlerts",
        ),
        filter = "order.id == '$orderId'"
    )
}